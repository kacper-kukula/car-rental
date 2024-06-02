package com.carrental.service.impl;

import com.carrental.service.NotificationService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramNotificationService implements NotificationService {

    private static final String TELEGRAM_MESSAGE_URL =
            "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

    private final RestTemplate restTemplate = new RestTemplate();
    private final String botToken;
    private final String chatId;

    public TelegramNotificationService(Dotenv dotenv) {
        this.botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
        this.chatId = dotenv.get("TELEGRAM_CHAT_ID");
    }

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
