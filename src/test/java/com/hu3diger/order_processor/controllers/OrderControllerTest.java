package com.hu3diger.order_processor.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hu3diger.order_processor.dtos.OrderDto;
import com.hu3diger.order_processor.dtos.OrderSummaryDto;
import com.hu3diger.order_processor.entities.OrderEntity;
import com.hu3diger.order_processor.mappers.OrderMapper;
import com.hu3diger.order_processor.services.JwtService;
import com.hu3diger.order_processor.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private OrderMapper orderMapper;

    // Helper method to convert an object to JSON string
    public static String asJsonString(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    @WithMockUser(username = "user")
    public void testCreateOrders_Success() throws Exception {
        List<OrderDto> orderDtos = new ArrayList<>();
        orderDtos.add(new OrderDto());

        OrderEntity orderEntity = new OrderEntity();
        List<OrderEntity> savedOrders = new ArrayList<>();
        savedOrders.add(orderEntity);

        when(orderService.createOrders(anyList())).thenReturn(savedOrders);

        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(asJsonString(orderDtos))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(savedOrders)));

        verify(orderService, times(1)).createOrders(anyList());
    }

    @Test
    @WithMockUser(username = "user")
    public void testGetOrderById_Success() throws Exception {
        Long id = 1L;
        OrderEntity order = new OrderEntity();

        when(orderService.getOrderById(id)).thenReturn(order);

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(order)));

        verify(orderService, times(1)).getOrderById(id);
    }

    @Test
    @WithMockUser(username = "user")
    public void testGetOrderById_NotFound() throws Exception {
        Long id = 1L;

        when(orderService.getOrderById(id)).thenReturn(null);

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById(id);
    }

    @Test
    @WithMockUser(username = "user")
    public void testGetAllOrdersSummary() throws Exception {
        List<OrderSummaryDto> summaries = new ArrayList<>();
        OrderSummaryDto summary = new OrderSummaryDto();
        summaries.add(summary);

        when(orderService.getAllOrdersSummary()).thenReturn(summaries);

        mockMvc.perform(get("/api/orders/summary"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(summaries)));

        verify(orderService, times(1)).getAllOrdersSummary();
    }
}
