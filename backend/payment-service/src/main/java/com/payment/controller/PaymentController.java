package com.payment.controller;

import com.payment.assembler.PaymentModelAssembler;
import com.payment.dto.PaymentDTO;
import com.payment.entity.Payment;
import com.payment.models.PaymentModel;
import com.payment.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentModelAssembler paymentModelAssembler;

    public PaymentController(PaymentService paymentService, PaymentModelAssembler paymentModelAssembler) {
        this.paymentService = paymentService;
        this.paymentModelAssembler = paymentModelAssembler;
    }

    @PostMapping
    public ResponseEntity<PaymentModel> processPayment(@Valid @RequestBody PaymentDTO request) {
        Payment payment = paymentService.processPayment(request);
        return ResponseEntity.ok(paymentModelAssembler.toModel(payment));
    }
}

