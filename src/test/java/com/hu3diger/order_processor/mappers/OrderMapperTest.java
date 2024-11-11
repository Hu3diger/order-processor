package com.hu3diger.order_processor.mappers;

import com.hu3diger.order_processor.dtos.OrderDto;
import com.hu3diger.order_processor.dtos.OrderItemDto;
import com.hu3diger.order_processor.dtos.OrderSummaryDto;
import com.hu3diger.order_processor.entities.OrderEntity;
import com.hu3diger.order_processor.entities.OrderItemEntity;
import com.hu3diger.order_processor.enuns.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderMapperTest {

    private final OrderMapper orderMapper = new OrderMapper();

    @Test
    void shouldConvertOrderDtoToOrderEntity() {
        OrderItemDto itemDto1 = new OrderItemDto("P001", 2, BigDecimal.valueOf(50.00));
        OrderItemDto itemDto2 = new OrderItemDto("P002", 1, BigDecimal.valueOf(100.50));
        OrderDto orderDto = new OrderDto("EXT123", List.of(itemDto1, itemDto2));

        OrderEntity orderEntity = orderMapper.toEntity(orderDto);

        assertNotNull(orderEntity);
        assertEquals(OrderStatus.PENDING, orderEntity.getStatus());
        assertEquals(orderDto.getExternalCode(), orderEntity.getExternalOrderCode());
        assertEquals(orderDto.getItems().size(), orderEntity.getItems().size());
        assertEquals(orderDto.getItems().get(0).getProductCode(), orderEntity.getItems().get(0).getProductCode());
        assertEquals(orderDto.getItems().get(0).getQuantity(), orderEntity.getItems().get(0).getQuantity());
        assertEquals(orderDto.getItems().get(0).getUnitPrice(), orderEntity.getItems().get(0).getUnitPrice());
    }

    @Test
    void shouldConvertOrderItemDtoToOrderItemEntity() {
        OrderItemDto itemDto = new OrderItemDto("P001", 2, BigDecimal.valueOf(50.00));

        OrderItemEntity itemEntity = orderMapper.toItemEntity(itemDto);

        assertNotNull(itemEntity);
        assertEquals(itemDto.getProductCode(), itemEntity.getProductCode());
        assertEquals(itemDto.getQuantity(), itemEntity.getQuantity());
        assertEquals(itemDto.getUnitPrice(), itemEntity.getUnitPrice());
    }

    @Test
    void shouldConvertOrderEntityToOrderSummaryDto() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setExternalOrderCode("EXT123");
        order.setTotalPrice(BigDecimal.valueOf(200.50));
        order.setOrderHash("HASH123");
        order.setItems(List.of(
                new OrderItemEntity("P001", 2, BigDecimal.valueOf(50.00)),
                new OrderItemEntity("P002", 1, BigDecimal.valueOf(100.50))
        ));

        OrderSummaryDto summaryDto = orderMapper.toOrderSummaryDto(order);

        assertEquals(order.getId(), summaryDto.getOrderId());
        assertEquals(order.getStatus().name(), summaryDto.getStatus());
        assertEquals(2, summaryDto.getItems());
        assertEquals(3, summaryDto.getTotalQuantity());
        assertEquals(order.getTotalPrice(), summaryDto.getTotalPrice());
        assertEquals(order.getExternalOrderCode(), summaryDto.getExternalCode());
        assertEquals(order.getOrderHash(), summaryDto.getOrderHash());
    }
}

