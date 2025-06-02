package spring.practice.elmenus_lite.conroller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.dto.NewOrderRequest;
import spring.practice.elmenus_lite.dto.OrderDetails;
import spring.practice.elmenus_lite.dto.OrderSummary;
import spring.practice.elmenus_lite.service.OrderService;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // POST api/v1/orders - Place new order
    @PostMapping
    public ResponseEntity<OrderSummary> placeOrder(@RequestBody NewOrderRequest newOrderRequest) {
        OrderSummary summary = orderService.placeOrder(newOrderRequest);
        return ResponseEntity.status(201).body(summary);
    }

//    // PUT /orders/{orderId}/cancel - Cancel order
//    @PutMapping("/{orderId}/cancel")
//    public ResponseEntity<OrderSummary> cancelOrder(
//            @PathVariable Long orderId,
//            @RequestBody CancelOrderRequest request
//    ) {
//        OrderSummary summary = orderService.cancelOrder(orderId, request);
//        return ResponseEntity.ok(summary);
//    }
//
//    // PUT /orders/{orderId}/status - Update order status
//    @PutMapping("/{orderId}/status")
//    public ResponseEntity<OrderSummary> updateOrderStatus(
//            @PathVariable Long orderId,
//            @RequestBody UpdateOrderStatusRequest request
//    ) {
//        OrderSummary summary = orderService.updateOrderStatus(orderId, request);
//        return ResponseEntity.ok(summary);
//    }
//
    // GET /orders/{orderId}/summary - Get order summary
    @GetMapping("/{orderId}/summary")
    public ResponseEntity<OrderSummary> getOrderSummary(@PathVariable Integer orderId) {
        OrderSummary summary = orderService.getOrderSummary(orderId);
        return ResponseEntity.ok(summary);
    }

    // GET /orders/{orderId}/details - Get full order details
    @GetMapping("/{orderId}/details")
    public ResponseEntity<OrderDetails> getOrderDetails(@PathVariable Integer orderId) {
        OrderDetails details = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(details);
    }
}