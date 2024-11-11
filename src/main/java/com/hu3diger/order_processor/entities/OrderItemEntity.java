package com.hu3diger.order_processor.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productCode;

    private Integer quantity;

    private BigDecimal unitPrice;

    public OrderItemEntity(String productCode, Integer quantity, BigDecimal unitPrice) {
        this.productCode = productCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal calculateTotalPrice() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }
}

