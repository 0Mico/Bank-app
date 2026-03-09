package com.bankapp.payment.controller;

import com.bankapp.common.dto.PaymentResponse;
import com.bankapp.common.dto.PaymentRequest;
import com.bankapp.payment.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse payment = paymentService.processPayment(request);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            List<PaymentResponse> payments = paymentService.getPaymentsByUserId(userId);
            return ResponseEntity.ok(payments);
        }
        return ResponseEntity.ok(List.of());
    }
}
