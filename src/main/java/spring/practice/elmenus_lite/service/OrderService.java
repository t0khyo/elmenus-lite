package spring.practice.elmenus_lite.service;

import spring.practice.elmenus_lite.dto.NewOrderRequest;
import spring.practice.elmenus_lite.dto.OrderSummary;

public interface OrderService {

    OrderSummary placeOrder(NewOrderRequest newOrderRequest) ;

}
