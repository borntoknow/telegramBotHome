package org.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class Bot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    private final BotMenuHandler menuHandler;
    private final BotCommandHandler commandHandler;

    public Bot() {
        this.menuHandler = new BotMenuHandler();
        this.commandHandler = new BotCommandHandler(this);
        commandHandler.initializeBotCommands();
    }

    @Override
    public String getBotUsername() {
        return "uk_home_name";
    }

    @Override
    public String getBotToken() {
        String token = System.getenv("BOT_TOKEN");

        if (token == null || token.isEmpty()) {
            logger.error("Токен бота отсутствует. Убедитесь, что BOT_TOKEN задан в окружении.");
            throw new IllegalStateException("Token not found. Ensure BOT_TOKEN is set in the environment variables.");
        }
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info("Получено обновление: {}", update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            logger.info("Обработано текстовое сообщение: chatId={}, text={}",
                    update.getMessage().getChatId(), update.getMessage().getText());
            commandHandler.handleIncomingMessage(update);
        } else if (update.hasCallbackQuery()) {
            logger.info("Обработан callback-запрос: chatId={}, data={}",
                    update.getCallbackQuery().getMessage().getChatId(),
                    update.getCallbackQuery().getData());
            menuHandler.handleCallbackQuery(update, this);
        }
    }

    public int sendResponse(long chatId, String text) {
        logger.info("Отправка текстового сообщения: chatId={}, text={}", chatId, text);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            int messageId = execute(message).getMessageId();
            logger.info("Сообщение успешно отправлено: chatId={}, messageId={}", chatId, messageId);
            return messageId;
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке текстового сообщения: chatId={}, error={}", chatId, e.getMessage(), e);
            return -1;
        }
    }

    public int sendResponse(long chatId, String text, InlineKeyboardMarkup replyMarkup) {
        logger.info("Отправка текстового сообщения с клавиатурой: chatId={}, text={}", chatId, text);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(replyMarkup);

        try {
            int messageId = execute(message).getMessageId();
            logger.info("Сообщение с клавиатурой успешно отправлено: chatId={}, messageId={}", chatId, messageId);
            return messageId;
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке текстового сообщения с клавиатурой: chatId={}, error={}", chatId, e.getMessage(), e);
            return -1;
        }
    }

    public int sendImage(long chatId, String filePath) {
        logger.info("Отправка изображения: chatId={}, filePath={}", chatId, filePath);
        if (filePath == null || filePath.isEmpty()) {
            logger.warn("Путь к изображению пуст: chatId={}", chatId);
            sendResponse(chatId, "Изображение недоступно.");
            return -1;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            logger.warn("Файл не найден: chatId={}, filePath={}", chatId, filePath);
            sendResponse(chatId, "Файл не найден: " + filePath);
            return -1;
        }

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(file));

        try {
            int messageId = execute(sendPhoto).getMessageId();
            logger.info("Изображение успешно отправлено: chatId={}, messageId={}", chatId, messageId);
            return messageId;
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке изображения: chatId={}, filePath={}, error={}", chatId, filePath, e.getMessage(), e);
            sendResponse(chatId, "Не удалось отправить изображение.");
            return -1;
        }
    }
}
