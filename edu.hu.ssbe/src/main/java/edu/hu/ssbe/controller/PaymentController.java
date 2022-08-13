package edu.hu.ssbe.controller;

import edu.hu.ssbe.bean.Payment;
import edu.hu.ssbe.service.PaymentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ds/payments")
public class PaymentController {

    private static final Logger LOGGER = LogManager.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(
            path = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity getPayments() {
//        LOGGER.info("-----------------GET");
        List<Payment> payments = paymentService.getPayments();
        if (payments != null && !payments.isEmpty()) {
            return ResponseEntity.ok(payments);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity getPayment(@PathVariable String id) {
        Payment payment = paymentService.getPayment(id);
        if (payment != null) {
            return ResponseEntity.ok(payment);
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(
            path = "",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity postPayment(@RequestBody Payment payment) {
//        LOGGER.info("-----------------POST");
        Payment createdPayment = paymentService.addPayment(payment);
        if (createdPayment != null) {
            return ResponseEntity.ok(createdPayment);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity updatePayment(@PathVariable String id,
                                        @RequestBody Payment payment) {
        Payment updatedPayment = paymentService.editPayment(id, payment);
        if (updatedPayment != null) {
            return ResponseEntity.ok(updatedPayment);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePayment(@PathVariable String id) {
        if (paymentService.deletePayment(id)) {
            return ResponseEntity.ok("product deleted");
        }
        return ResponseEntity.badRequest().build();
    }
}
