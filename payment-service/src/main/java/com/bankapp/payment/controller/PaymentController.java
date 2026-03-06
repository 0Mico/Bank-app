package com.bankapp.payment.controller;

import com.bankapp.common.dto.PaymentDTO;
import com.bankapp.payment.service.PaymentService;
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
    public ResponseEntity<PaymentDTO> processPayment(@RequestBody PaymentDTO dto) {
        PaymentDTO payment = paymentService.processPayment(dto);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable Long id) {
        PaymentDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getPayments(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            List<PaymentDTO> payments = paymentService.getPaymentsByUserId(userId);
            return ResponseEntity.ok(payments);
        }
        return ResponseEntity.ok(List.of());
    }
}
