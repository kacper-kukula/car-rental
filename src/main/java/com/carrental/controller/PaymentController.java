package com.carrental.controller;

import com.carrental.dto.payment.PaymentCreateRequestDto;
import com.carrental.dto.payment.PaymentPausedResponseDto;
import com.carrental.dto.payment.PaymentResponseDto;
import com.carrental.model.Payment;
import com.carrental.security.AuthenticationUtil;
import com.carrental.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Rentals payment management",
        description = "Endpoints for managing payments")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthenticationUtil authenticationUtil;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @Operation(summary = "Get payments",
            description = "Get payment for particular user, or for currently logged in customer")
    public List<PaymentResponseDto> findPayments(
            @RequestParam(name = "user_id", required = false) Long userId) {
        if (authenticationUtil.isManager()) {
            // Managers can view all payments or of a specific user if userId is provided
            // If null is passed by manager, service fetches all payments
            return paymentService.findPayments(userId);
        } else {
            // Customers can only view their own payments - exception thrown if userId is provided
            if (userId != null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Customers are not authorized to specify a user ID.");
            }

            // Customer passes null, so from context, service fetches only his payments
            return paymentService.findPayments(
                    authenticationUtil.getCurrentUserFromDb().getId());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    @Operation(summary = "Create payment",
            description = "Create a payment session for currently logged in customer")
    public PaymentResponseDto createPaymentSession(
            @RequestBody @Valid PaymentCreateRequestDto request) {
        return paymentService.createPaymentSession(request);
    }

    @Operation(summary = "Check successful payment",
            description = "Check if the payment succeeded based on ID and TYPE")
    @GetMapping("/success/{id}")
    public PaymentResponseDto checkSuccessfulPayment(
            @PathVariable Long id,
            @RequestParam Payment.Type type) {
        return paymentService.checkSuccessfulPayment(id, type);
    }

    @Operation(summary = "Get paused payment message",
            description = "Returns a message that the payment has been paused")
    @GetMapping("/cancel/{id}")
    public PaymentPausedResponseDto returnPausedPayment(@PathVariable Long id) {
        return paymentService.returnPausedPayment(id);
    }
}
