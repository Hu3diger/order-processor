package com.hu3diger.order_processor.services;

import com.hu3diger.order_processor.entities.OrderEntity;
import com.hu3diger.order_processor.entities.OrderItemEntity;
import com.hu3diger.order_processor.enuns.OrderStatus;
import com.hu3diger.order_processor.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderBatchService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderBatchService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public List<OrderEntity> saveOrdersInBatch(List<OrderEntity> orders) throws NoSuchAlgorithmException {
        int batchSize = 50;

        Set<String> externalOrderCodes = new HashSet<>();
        Set<String> orderHashes = new HashSet<>();

        orders = orders.stream()
                .map(order -> {
                    BigDecimal totalPrice = calculateTotalPrice(order.getItems());
                    order.setTotalPrice(totalPrice);

                    try {
                        String orderHash = generateOrderHash(order);
                        order.setOrderHash(orderHash);

                        if (order.getExternalOrderCode() == null && !orderHashes.add(orderHash)) {
                            return null;
                        }

                        if (order.getExternalOrderCode() != null && !externalOrderCodes.add(order.getExternalOrderCode())) {
                            return null;
                        }

                        //TODO: Implement a background service to update to processed (Something like RabbitMQ)
                        order.setStatus(OrderStatus.PROCESSED);
                        order.setCreatedAt(LocalDateTime.now());
                        return order;

                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (int i = 0; i < orders.size(); i += batchSize) {
            List<OrderEntity> batch = orders.subList(i, Math.min(i + batchSize, orders.size()));
            orderRepository.saveAll(batch);
            orderRepository.flush();
        }

        return orders;
    }

    public String generateOrderHash(OrderEntity order) throws NoSuchAlgorithmException {
        StringBuilder data = new StringBuilder();

        order.getItems().stream()
                .sorted(Comparator.comparing(OrderItemEntity::getProductCode))
                .forEach(item -> {
                    data.append(item.getProductCode());
                    data.append(item.getQuantity());
                    data.append(item.getUnitPrice());
                });

        data.append(order.getTotalPrice());

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(data.toString().getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    public BigDecimal calculateTotalPrice(List<OrderItemEntity> items) {
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
