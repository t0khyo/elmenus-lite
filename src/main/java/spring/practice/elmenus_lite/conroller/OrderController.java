package spring.practice.elmenus_lite.conroller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.dto.OrderRequest;
import spring.practice.elmenus_lite.dto.OrderSummaryResponse;
import spring.practice.elmenus_lite.model.User;
import spring.practice.elmenus_lite.service.OrderService;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderSummaryResponse> placeOrder(@Valid @RequestBody OrderRequest orderRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Integer userID = user.getId();
        OrderSummaryResponse orderSummary = orderService.placeOrder(orderRequest,userID);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderSummary);
    }

    // GET /orders/{orderId}/summary - Get order summary
    @GetMapping("/{orderId}/summary")
    public ResponseEntity<OrderSummaryResponse> getOrderSummary(@PathVariable Integer orderId) {
        OrderSummaryResponse summary = orderService.getOrderSummary(orderId);
        return ResponseEntity.ok(summary);
    }
}