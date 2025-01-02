package org.home;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotCommandHandler {

  private static final Logger logger = LoggerFactory.getLogger(BotCommandHandler.class);

  private final Bot bot;

  public BotCommandHandler(Bot bot) {
    this.bot = bot;
  }

  public void initializeBotCommands() {
    List<BotCommand> commands = new ArrayList<>();
    commands.add(new BotCommand("/start", "Начать работу"));
    commands.add(new BotCommand("/help", "Помощь"));
    commands.add(new BotCommand("/settings", "Настройки"));
    commands.add(new BotCommand("/clear", "Очистить чат"));

    SetMyCommands setMyCommands = new SetMyCommands();
    setMyCommands.setCommands(commands);

    // Устанавливаем область видимости для личных чатов
    setMyCommands.setScope(new BotCommandScopeDefault());

    try {
      bot.execute(setMyCommands);
      logger.info("Команды бота успешно инициализированы.");
    } catch (TelegramApiException e) {
      logger.error("Ошибка при инициализации команд бота: {}", e.getMessage(), e);
    }
  }

  public void handleIncomingMessage(Update update) {

    if (!update.hasMessage() || !update.getMessage().hasText()) {
      return; // Игнорируем сообщения без текста
    }

    String messageText = update.getMessage().getText();
    long chatId = update.getMessage().getChatId();
    long userId = update.getMessage().getFrom().getId();

    logger.info("Получено сообщение: chatId={}, userId={}, text={}", chatId, userId, messageText);

    // Проверяем, что бот работает только в личных чатах
    if (!update.getMessage().getChat().isUserChat()) {
      logger.info("Сообщение из группы или супергруппы проигнорировано: chatId={}", chatId);
      return; // Игнорируем сообщения из групп и супергрупп
    }

    AccessControlHandler accessControlHandler =
        new AccessControlHandler(bot, "-1001234567890"); // Замените на реальный ID вашей группы

    //    if (!accessControlHandler.hasAccess(userId)) {
    //      bot.sendResponse(chatId, "У вас нет доступа к этому боту. Вы должны быть участником
    // группы.");
    //      return;
    //    }

    switch (messageText) {
      case "/start":
        bot.sendResponse(chatId, "Добро пожаловать!");
        bot.sendResponse(chatId, "Выберите опцию:", new BotMenuHandler().createInlineMenu());
        break;
      case "/help":
        bot.sendResponse(
            chatId,
            "Список команд:\n/start - Начать работу\n/help - Помощь\n/settings - Настройки");
        break;
      default:
        bot.sendResponse(chatId, "Команда не распознана.");
    }
  }
}
