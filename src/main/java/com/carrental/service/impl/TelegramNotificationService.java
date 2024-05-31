package com.carrental.service.impl;

import com.carrental.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramNotificationService implements NotificationService {

    private static final String TELEGRAM_MESSAGE_URL =
            "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    @Override
    public void sendNotification(String message) {
        String url = String.format(
                TELEGRAM_MESSAGE_URL,
                botToken,
                chatId,
                message);

        restTemplate.getForObject(url, String.class);
    }
}
