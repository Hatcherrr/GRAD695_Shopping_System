package edu.hu.ssbe.controller;

import edu.hu.ssbe.bean.Order;
import edu.hu.ssbe.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ds/orders")
public class OrderController {

    private static final Logger LOGGER = LogManager.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @RequestMapping(
            path = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity getOrders() {
//        LOGGER.info("-----------------GET");
        List<Order> orders = orderService.getOrders();
        if (orders != null && !orders.isEmpty()) {
            return ResponseEntity.ok(orders);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity getOrder(@PathVariable String id) {
        Order order = orderService.getOrder(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(
            path = "",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity postOrder(@RequestBody Order order) {
//        LOGGER.info("-----------------POST");
        Order createdOrder = orderService.addOrder(order);
        if (createdOrder != null) {
            return ResponseEntity.ok(createdOrder);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity updateOrder(@PathVariable String id,
                                      @RequestBody Order order) {
        Order updatedOrder = orderService.editOrder(id, order);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteOrder(@PathVariable String id) {
        if (orderService.deleteOrder(id)) {
            return ResponseEntity.ok("product deleted");
        }
        return ResponseEntity.badRequest().build();
    }
}
