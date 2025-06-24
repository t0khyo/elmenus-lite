package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.OrderRequest;
import spring.practice.elmenus_lite.dto.OrderSummaryResponse;

public interface OrderService {

    OrderSummaryResponse placeOrder(OrderRequest orderRequest) ;

    OrderSummaryResponse getOrderSummary(Integer orderId);
}
