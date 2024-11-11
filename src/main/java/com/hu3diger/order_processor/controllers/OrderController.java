package com.hu3diger.order_processor.controllers;

import com.hu3diger.order_processor.dtos.OrderDto;
import com.hu3diger.order_processor.dtos.OrderSummaryDto;
import com.hu3diger.order_processor.entities.OrderEntity;
import com.hu3diger.order_processor.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<List<OrderEntity>> createOrders(@RequestBody List<OrderDto> ordersDTO) {
        try {
            List<OrderEntity> savedOrders = orderService.createOrders(ordersDTO);
            return ResponseEntity.ok(savedOrders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderEntity> getOrderById(@PathVariable Long id) {
        var order = orderService.getOrderById(id);
        return (order != null) ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<List<OrderSummaryDto>> getAllOrdersSummary() {
        var summaries = orderService.getAllOrdersSummary();
        return ResponseEntity.ok(summaries);
    }
}



