package com.hu3diger.order_processor.services;

import com.hu3diger.order_processor.entities.OrderEntity;
import com.hu3diger.order_processor.entities.OrderItemEntity;
import com.hu3diger.order_processor.enuns.OrderStatus;
import com.hu3diger.order_processor.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderBatchServiceTest {

    @InjectMocks
    private OrderBatchService orderBatchService;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveUniqueOrdersInBatch() throws NoSuchAlgorithmException {
        OrderEntity order1 = new OrderEntity();
        order1.setExternalOrderCode("EXT001");
        order1.setItems(List.of(new OrderItemEntity("P001", 2, BigDecimal.valueOf(50))));

        OrderEntity order2 = new OrderEntity();
        order2.setExternalOrderCode("EXT002");
        order2.setItems(List.of(new OrderItemEntity("P002", 1, BigDecimal.valueOf(100))));

        List<OrderEntity> orders = List.of(order1, order2);

        when(orderRepository.saveAll(anyList())).thenReturn(orders);

        List<OrderEntity> savedOrders = orderBatchService.saveOrdersInBatch(orders);

        verify(orderRepository, times(1)).saveAll(anyList());
        assertEquals(2, savedOrders.size());
        assertNotNull(savedOrders.get(0).getOrderHash());
        assertNotNull(savedOrders.get(1).getOrderHash());
        assertEquals(OrderStatus.PROCESSED, savedOrders.get(0).getStatus());
    }

    @Test
    void shouldIgnoreDuplicateExternalOrderCodes() throws NoSuchAlgorithmException {
        OrderEntity order1 = new OrderEntity();
        order1.setExternalOrderCode("EXT001");
        order1.setItems(List.of(new OrderItemEntity("P001", 2, BigDecimal.valueOf(50))));

        OrderEntity duplicateOrder = new OrderEntity();
        duplicateOrder.setExternalOrderCode("EXT001");
        duplicateOrder.setItems(List.of(new OrderItemEntity("P003", 3, BigDecimal.valueOf(20))));

        List<OrderEntity> orders = List.of(order1, duplicateOrder);

        List<OrderEntity> savedOrders = orderBatchService.saveOrdersInBatch(orders);

        verify(orderRepository, times(1)).saveAll(anyList());
        assertEquals(1, savedOrders.size());
        assertEquals("EXT001", savedOrders.get(0).getExternalOrderCode());
    }

    @Test
    void shouldIgnoreDuplicateOrderHashesWhenNoExternalOrderCode() throws NoSuchAlgorithmException {
        OrderEntity order1 = new OrderEntity();
        order1.setItems(List.of(new OrderItemEntity("P001", 2, BigDecimal.valueOf(50))));
        order1.setTotalPrice(BigDecimal.valueOf(100));

        OrderEntity duplicateOrder = new OrderEntity();
        duplicateOrder.setItems(List.of(new OrderItemEntity("P001", 2, BigDecimal.valueOf(50))));
        duplicateOrder.setTotalPrice(BigDecimal.valueOf(100));

        List<OrderEntity> orders = List.of(order1, duplicateOrder);

        List<OrderEntity> savedOrders = orderBatchService.saveOrdersInBatch(orders);

        verify(orderRepository, times(1)).saveAll(anyList());
        assertEquals(1, savedOrders.size());
    }

    @Test
    void shouldCalculateTotalPrice() {
        OrderItemEntity item1 = new OrderItemEntity("P001", 2, BigDecimal.valueOf(50));
        OrderItemEntity item2 = new OrderItemEntity("P002", 1, BigDecimal.valueOf(100));

        BigDecimal totalPrice = orderBatchService.calculateTotalPrice(List.of(item1, item2));

        assertEquals(BigDecimal.valueOf(200), totalPrice);
    }

    @Test
    void shouldGenerateOrderHash() throws NoSuchAlgorithmException {
        OrderEntity order = new OrderEntity();
        order.setItems(List.of(new OrderItemEntity("P001", 2, BigDecimal.valueOf(50))));
        order.setTotalPrice(BigDecimal.valueOf(100));

        String hash1 = orderBatchService.generateOrderHash(order);

        assertNotNull(hash1);
        assertFalse(hash1.isEmpty());

        String hash2 = orderBatchService.generateOrderHash(order);
        assertEquals(hash1, hash2);
    }

    @Test
    void shouldSetCreatedAtAndStatusWhenSavingOrders() throws NoSuchAlgorithmException {
        OrderEntity order = new OrderEntity();
        order.setItems(List.of(new OrderItemEntity("P001", 2, BigDecimal.valueOf(50))));

        List<OrderEntity> savedOrders = orderBatchService.saveOrdersInBatch(List.of(order));

        verify(orderRepository, times(1)).saveAll(anyList());
        assertNotNull(savedOrders.get(0).getCreatedAt());
        assertEquals(OrderStatus.PROCESSED, savedOrders.get(0).getStatus());
    }
}
