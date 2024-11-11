package com.hu3diger.order_processor.services;

import com.hu3diger.order_processor.dtos.OrderDto;
import com.hu3diger.order_processor.dtos.OrderSummaryDto;
import com.hu3diger.order_processor.entities.OrderEntity;
import com.hu3diger.order_processor.mappers.OrderMapper;
import com.hu3diger.order_processor.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {


    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final OrderBatchService orderBatchService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderMapper orderMapper,
                        OrderBatchService orderBatchService) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.orderBatchService = orderBatchService;
    }

    public List<OrderEntity> createOrders(List<OrderDto> ordersDTO) throws NoSuchAlgorithmException {
        List<OrderEntity> orders = ordersDTO.stream().map(orderMapper::toEntity).collect(Collectors.toList());
        return orderBatchService.saveOrdersInBatch(orders);
    }

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    public OrderEntity getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<OrderSummaryDto> getAllOrdersSummary() {
        List<OrderEntity> orders = this.getAllOrders();
        return orders.stream()
                .map(orderMapper::toOrderSummaryDto)
                .collect(Collectors.toList());
    }
}
