package com.hu3diger.order_processor.services;

import com.hu3diger.order_processor.dtos.OrderDto;
import com.hu3diger.order_processor.dtos.OrderSummaryDto;
import com.hu3diger.order_processor.entities.OrderEntity;
import com.hu3diger.order_processor.mappers.OrderMapper;
import com.hu3diger.order_processor.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderBatchService orderBatchService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrdersSuccessfully() throws NoSuchAlgorithmException {
        OrderDto orderDto = new OrderDto();
        OrderEntity orderEntity = new OrderEntity();
        List<OrderDto> orderDtoList = List.of(orderDto);
        List<OrderEntity> orderEntityList = List.of(orderEntity);

        when(orderMapper.toEntity(orderDto)).thenReturn(orderEntity);
        when(orderBatchService.saveOrdersInBatch(orderEntityList)).thenReturn(orderEntityList);

        List<OrderEntity> result = orderService.createOrders(orderDtoList);

        assertEquals(orderEntityList, result);
        verify(orderMapper, times(1)).toEntity(orderDto);
        verify(orderBatchService, times(1)).saveOrdersInBatch(orderEntityList);
    }

    @Test
    void shouldGetAllOrders() {
        OrderEntity orderEntity = new OrderEntity();
        List<OrderEntity> orderEntityList = List.of(orderEntity);

        when(orderRepository.findAll()).thenReturn(orderEntityList);

        List<OrderEntity> result = orderService.getAllOrders();

        assertEquals(orderEntityList, result);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void shouldGetOrderByIdWhenOrderExists() {
        Long orderId = 1L;
        OrderEntity orderEntity = new OrderEntity();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));

        OrderEntity result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderEntity, result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void shouldReturnNullWhenOrderByIdDoesNotExist() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderEntity result = orderService.getOrderById(orderId);

        assertNull(result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void shouldGetAllOrdersSummary() {
        OrderEntity orderEntity = new OrderEntity();
        OrderSummaryDto summaryDto = new OrderSummaryDto();
        List<OrderEntity> orderEntityList = List.of(orderEntity);
        List<OrderSummaryDto> orderSummaryDtoList = List.of(summaryDto);

        when(orderRepository.findAll()).thenReturn(orderEntityList);
        when(orderMapper.toOrderSummaryDto(orderEntity)).thenReturn(summaryDto);

        List<OrderSummaryDto> result = orderService.getAllOrdersSummary();

        assertEquals(orderSummaryDtoList, result);
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).toOrderSummaryDto(orderEntity);
    }
}

