package com.hu3diger.order_processor.mappers;

import com.hu3diger.order_processor.dtos.OrderDto;
import com.hu3diger.order_processor.dtos.OrderItemDto;
import com.hu3diger.order_processor.dtos.OrderSummaryDto;
import com.hu3diger.order_processor.entities.OrderEntity;
import com.hu3diger.order_processor.entities.OrderItemEntity;
import com.hu3diger.order_processor.enuns.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderEntity toEntity(OrderDto orderDTO) {
        OrderEntity order = new OrderEntity();
        List<OrderItemEntity> items = orderDTO.getItems().stream().map(this::toItemEntity).collect(Collectors.toList());
        order.setItems(items);
        order.setExternalOrderCode(orderDTO.getExternalCode());
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    public OrderItemEntity toItemEntity(OrderItemDto itemDTO) {
        OrderItemEntity item = new OrderItemEntity();
        item.setProductCode(itemDTO.getProductCode());
        item.setQuantity(itemDTO.getQuantity());
        item.setUnitPrice(itemDTO.getUnitPrice());
        return item;
    }

    public OrderSummaryDto toOrderSummaryDto(OrderEntity order) {
        int totalItems = order.getItems().stream()
                .mapToInt(OrderItemEntity::getQuantity)
                .sum();

        return new OrderSummaryDto(
                order.getId(),
                order.getStatus().name(),
                order.getItems().size(),
                totalItems,
                order.getTotalPrice(),
                order.getExternalOrderCode(),
                order.getOrderHash()
        );
    }
}
