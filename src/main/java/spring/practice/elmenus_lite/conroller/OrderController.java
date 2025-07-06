package spring.practice.elmenus_lite.conroller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.dto.OrderDetails;
import spring.practice.elmenus_lite.dto.OrderRequest;
import spring.practice.elmenus_lite.dto.OrderSummaryResponse;
import spring.practice.elmenus_lite.service.OrderService;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderSummaryResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        OrderSummaryResponse summary = orderService.placeOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(summary);
    }

    // GET /orders/{orderId}/summary - Get order summary
    @GetMapping("/{orderId}/summary")
    public ResponseEntity<OrderSummaryResponse> getOrderSummary(@PathVariable Integer orderId) {
        OrderSummaryResponse summary = orderService.getOrderSummary(orderId);
        return ResponseEntity.ok(summary);
    }

    // GET /orders/{orderId}/details - Get full order details
    @GetMapping("/{orderId}/details")
    public ResponseEntity<OrderDetails> getOrderDetails(@PathVariable Integer orderId) {
        OrderDetails details = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(details);
    }
}