package com.selection_point.controller;

import com.selection_point.entity.Payment;
import com.selection_point.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/verify")
    public Payment verifyPayment(@RequestBody Payment payment) {
        return paymentService.savePayment(payment);
    }
}
