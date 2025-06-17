package spring.practice.elmenus_lite.conroller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.practice.elmenus_lite.dto.NewOrderRequest;
import spring.practice.elmenus_lite.dto.OrderSummaryResponse;
import spring.practice.elmenus_lite.service.OrderService;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderSummaryResponse> placeOrder(@RequestBody NewOrderRequest newOrderRequest) {
        OrderSummaryResponse summary = orderService.placeOrder(newOrderRequest);
        return ResponseEntity.status(201).body(summary);
    }
}