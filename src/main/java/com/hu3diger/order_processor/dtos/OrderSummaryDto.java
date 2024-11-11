package com.hu3diger.order_processor.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {

    private Long orderId;
    private String status;
    private Integer items;
    private Integer totalQuantity;
    private BigDecimal totalPrice;
    private String externalCode;
    private String orderHash;
}

