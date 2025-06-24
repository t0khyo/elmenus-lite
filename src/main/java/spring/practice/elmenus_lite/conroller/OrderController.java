package spring.practice.elmenus_lite.conroller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.dto.OrderSummaryResponse;
import spring.practice.elmenus_lite.service.OrderService;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // GET /orders/{orderId}/summary - Get order summary
    @GetMapping("/{orderId}/summary")
    public ResponseEntity<OrderSummaryResponse> getOrderSummary(@PathVariable Integer orderId) {
        OrderSummaryResponse summary = orderService.getOrderSummary(orderId);
        return ResponseEntity.ok(summary);
    }
}