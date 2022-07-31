package com.buezman.order_service.service;

import com.buezman.order_service.dto.InventoryResponse;
import com.buezman.order_service.dto.OrderItemDto;
import com.buezman.order_service.dto.OrderRequest;
import com.buezman.order_service.model.Order;
import com.buezman.order_service.model.OrderItem;
import com.buezman.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest orderRequest) {

        List<OrderItem> orderItems = orderRequest.getOrderItemDtoList()
                .stream()
                .map(this::mapToOrderItem).toList();

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderItems(orderItems)
                .build();

        List<String> skuCodes = order.getOrderItems()
                .stream()
                .map(OrderItem::getSkuCode)
                .toList();
        /*
        Call inventory service and place order if product is in stock
         */
       InventoryResponse[] inventoryResponseArray =  webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        assert inventoryResponseArray != null;
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

       if (allProductsInStock) {
           orderRepository.save(order);
           return "Order placed successfully";
       }
       else
           throw new RuntimeException("Product out of stock, please try again later");
    }

    private OrderItem mapToOrderItem(OrderItemDto orderItemDto) {
        return OrderItem.builder()
                .price(orderItemDto.getPrice())
                .quantity(orderItemDto.getQuantity())
                .skuCode(orderItemDto.getSkuCode())
                .build();
    }
}
