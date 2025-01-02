package org.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotMenuHandler {

    private static final Logger logger = LoggerFactory.getLogger(BotMenuHandler.class);

    // Хранение ID последнего сообщения для каждого чата
    private final Map<Long, Integer> lastMessageIds = new HashMap<>();
    private final Map<Long, Integer> lastImageIds = new HashMap<>();


    public void handleCallbackQuery(Update update, Bot bot) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        // Получение данных о пользователе
        String username = update.getCallbackQuery().getFrom().getUserName(); // Никнейм
        String firstName = update.getCallbackQuery().getFrom().getFirstName(); // Имя
        String lastName = update.getCallbackQuery().getFrom().getLastName(); // Фамилия
        long userId = update.getCallbackQuery().getFrom().getId(); // ID пользователя

        logger.info("Callback-запрос от пользователя: userId={}, username={}, firstName={}, lastName={}",
                userId, username != null ? username : "N/A",
                firstName != null ? firstName : "N/A",
                lastName != null ? lastName : "N/A");

        logger.info("Обработан callback-запрос: callbackData={}, chatId={}", callbackData, chatId);

        // Удаление предыдущего текстового сообщения (если оно есть)
        if (lastMessageIds.containsKey(chatId)) {
            int lastMessageId = lastMessageIds.get(chatId);
            deleteMessage(bot, chatId, lastMessageId);
            logger.debug("Удалено предыдущее текстовое сообщение: messageId={}", lastMessageId);
        }

        // Удаление предыдущего изображения (если оно есть)
        if (lastImageIds.containsKey(chatId)) {
            int lastImageMessageId = lastImageIds.get(chatId);
            deleteMessage(bot, chatId, lastImageMessageId);
            logger.debug("Удалено предыдущее изображение: messageId={}", lastImageMessageId);
        }

        // Получение текста ответа из DataHandler
        String responseText = DataHandler.getResponse(callbackData);
        String imagePath = DataHandler.getImage(callbackData);

        logger.info("Ответ текста: {}, Путь изображения: {}", responseText, imagePath);

        // Отправка нового текстового сообщения и сохранение его ID
        int newTextMessageId = bot.sendResponse(chatId, responseText);
        lastMessageIds.put(chatId, newTextMessageId);
        logger.debug("Отправлено новое текстовое сообщение: messageId={}", newTextMessageId);

        // Отправка нового изображения (если есть) и сохранение его ID
        if (imagePath != null) {
            int newImageMessageId = bot.sendImage(chatId, imagePath);
            lastImageIds.put(chatId, newImageMessageId);
            logger.debug("Отправлено новое изображение: messageId={}", newImageMessageId);
        }

        // Убираем эффект нажатия кнопки
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(update.getCallbackQuery().getId());
        answer.setText("Команда обработана.");
        try {
            bot.execute(answer);
            logger.info("Callback-запрос успешно подтверждён: callbackId={}", update.getCallbackQuery().getId());
        } catch (TelegramApiException e) {
            logger.error("Ошибка при подтверждении callback-запроса: {}", e.getMessage(), e);
        }
    }

    public InlineKeyboardMarkup createInlineMenu() {
        logger.info("Создание Inline меню");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Ряд 1
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("⚙️ Администрирование", "administration"));
        row1.add(createButton("🔍 Поиск по таблице", "advanced_search"));
        rows.add(row1);

        // Ряд 2
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("УК Сити Дом", "uk"));
        row2.add(createButton("ХВС, водоотведение", "hvs"));
        row2.add(createButton("ГВС, тепло", "gvs"));
        rows.add(row2);

        // Ряд 3
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(createButton("Электроэнергия", "energy"));
        row3.add(createButton("Оператор ТКО", "trash"));
        row3.add(createButton("Антенна", "ant"));
        rows.add(row3);

        // Ряд 4
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(createButton("Камера 1", "cam1"));
        row4.add(createButton("Камера 2", "cam2"));
        row4.add(createButton("Камера 3", "cam3"));
        row4.add(createButton("Камера 4", "cam4"));
        row4.add(createButton("Камера 5", "cam5"));
        rows.add(row4);

        inlineKeyboardMarkup.setKeyboard(rows);

        logger.info("Меню успешно создано: {} кнопок", rows.size());
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        logger.debug("Создание кнопки: text={}, callbackData={}", text, callbackData);
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private InlineKeyboardButton createUrlButton(String text, String url) {
        logger.debug("Создание кнопки-ссылки: text={}, url={}", text, url);
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setUrl(url);
        return button;
    }

    public void deleteMessage(Bot bot, long chatId, int messageId) {
        logger.info("Удаление сообщения: chatId={}, messageId={}", chatId, messageId);

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(messageId);

        try {
            bot.execute(deleteMessage);
            logger.info("Сообщение успешно удалено: chatId={}, messageId={}", chatId, messageId);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при удалении сообщения: chatId={}, messageId={}, error={}", chatId, messageId, e.getMessage(), e);
        }
    }

}
