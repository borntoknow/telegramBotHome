package org.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class AccessControlHandler {

    private static final Logger logger = LoggerFactory.getLogger(AccessControlHandler.class);

    private final TelegramLongPollingBot bot;
    private final String groupId; // ID группы, где проверяется членство

    public AccessControlHandler(TelegramLongPollingBot bot, String groupId) {
        this.bot = bot;
        this.groupId = groupId;
    }

    public boolean hasAccess(long userId) {
        try {
            // Логируем начало проверки доступа
            logger.info("Проверка доступа пользователя: userId={}, groupId={}", userId, groupId);

            // Запрос информации о пользователе в группе
            GetChatMember getChatMember = new GetChatMember();
            getChatMember.setChatId(groupId);
            getChatMember.setUserId(userId);

            ChatMember chatMember = bot.execute(getChatMember);

            // Логируем статус пользователя
            String status = chatMember.getStatus();
            logger.info("Получен статус пользователя: userId={}, status={}", userId, status);

            // Проверяем статус пользователя
            boolean hasAccess = status.equals("member") || status.equals("administrator") || status.equals("creator");

            if (hasAccess) {
                logger.info("Доступ разрешён: userId={}", userId);
            } else {
                logger.warn("Доступ запрещён: userId={}, status={}", userId, status);
            }

            return hasAccess;

        } catch (TelegramApiException e) {
            logger.error("Ошибка при проверке доступа: userId={}, groupId={}, error={}", userId, groupId, e.getMessage(), e);
            return false; // Ошибка API, доступ запрещён
        }
    }
}
