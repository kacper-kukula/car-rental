package com.carrental.service.impl;

import com.carrental.dto.payment.PaymentResponseDto;
import com.carrental.model.Car;
import com.carrental.model.Rental;
import com.carrental.repository.RentalRepository;
import com.carrental.service.NotificationService;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.LocalDate;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramNotificationService implements NotificationService {

    private static final String TELEGRAM_MESSAGE_URL =
            "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    private static final String RENTAL_CREATED_MESSAGE = "New rental created\n\n%s\n\n%s";
    private static final String PAYMENT_CREATED_MESSAGE = """
            New payment created:
                                    
            Payment ID: %d
            Rental ID: %d
            Total: $%s
            Session ID: %s
            Status: %s
            Type: %s""";
    private static final String PAYMENT_PAID_MESSAGE = """
            Payment paid:
                                        
            Payment ID: %d
            Rental ID: %d
            Total: $%s
            Status: %s
            Type: %s""";

    private final RentalRepository rentalRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String botToken;
    private final String chatId;

    public TelegramNotificationService(RentalRepository rentalRepository, Dotenv dotenv) {
        this.rentalRepository = rentalRepository;
        this.botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
        this.chatId = dotenv.get("TELEGRAM_CHAT_ID");
    }

    @Override
    public void createRentalMessage(Rental rental, Car car) {
        sendNotification(String.format(RENTAL_CREATED_MESSAGE, rental, car));
    }

    @Override
    public void createPaymentMessage(PaymentResponseDto dto) {
        sendNotification(String.format(PAYMENT_CREATED_MESSAGE, dto.id(), dto.rentalId(),
                dto.amountToPay(), dto.sessionId(), dto.status(), dto.type()));
    }

    @Override
    public void paidPaymentMessage(PaymentResponseDto dto) {
        sendNotification(String.format(PAYMENT_PAID_MESSAGE, dto.id(), dto.rentalId(),
                dto.amountToPay(), dto.status(), dto.type()));
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
    public void checkOverdueRentals() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        List<Rental> overdueRentals = rentalRepository.findAllByReturnDateBetweenAndStatus(
                today, tomorrow, Rental.Status.ACTIVE);

        if (overdueRentals.isEmpty()) {
            sendNotification("No rentals overdue today or tomorrow!");
        } else {
            for (Rental rental : overdueRentals) {
                String message = String.format("Overdue rental:\n\n%s", rental);
                sendNotification(message);
            }
        }
    }

    private void sendNotification(String message) {
        String url = String.format(
                TELEGRAM_MESSAGE_URL,
                botToken,
                chatId,
                message);

        restTemplate.getForObject(url, String.class);
    }
}
