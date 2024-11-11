package com.hu3diger.order_processor.entities;

import com.hu3diger.order_processor.enuns.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders", uniqueConstraints = {
        @UniqueConstraint(columnNames = "externalOrderCode"),
        @UniqueConstraint(columnNames = "orderHash")
})
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItemEntity> items;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false, unique = true)
    private String externalOrderCode;

    @Column(nullable = false, unique = true)
    private String orderHash;

    private BigDecimal totalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderEntity() {
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }
}

