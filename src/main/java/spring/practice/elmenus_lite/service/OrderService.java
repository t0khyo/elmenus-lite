package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.NewOrderRequest;
import spring.practice.elmenus_lite.dto.OrderSummaryResponse;

public interface OrderService {

    OrderSummaryResponse placeOrder(NewOrderRequest newOrderRequest) ;

    OrderSummaryResponse getOrderSummary(Integer orderId);
}
