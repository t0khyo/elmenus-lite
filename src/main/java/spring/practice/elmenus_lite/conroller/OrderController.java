package spring.practice.elmenus_lite.conroller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.dto.NewOrderRequest;
import spring.practice.elmenus_lite.dto.OrderSummary;
import spring.practice.elmenus_lite.service.OrderService;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // GET /orders/{orderId}/summary - Get order summary
    @GetMapping("/{orderId}/summary")
    public ResponseEntity<OrderSummary> getOrderSummary(@PathVariable Integer orderId) {
        OrderSummary summary = orderService.getOrderSummary(orderId);
        return ResponseEntity.ok(summary);
    }
}