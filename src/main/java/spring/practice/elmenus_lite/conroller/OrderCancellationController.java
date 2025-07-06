package spring.practice.elmenus_lite.conroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.dto.request.CancelOrderRequest;
import spring.practice.elmenus_lite.dto.response.CancellationEligibilityResponse;
import spring.practice.elmenus_lite.dto.response.CancellationResponse;
import spring.practice.elmenus_lite.model.enums.CancellationReason;
import spring.practice.elmenus_lite.service.OrderCancellationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/cancellation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Cancellation", description = "APIs for managing order cancellations and refunds")
public class OrderCancellationController {

    private final OrderCancellationService cancellationService;

    public OrderCancellationController(OrderCancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    @Operation(
            summary = "Check cancellation eligibility",
            description = "Checks if an order can be cancelled and shows the financial impact"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eligibility check completed"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/eligibility")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<CancellationEligibilityResponse> checkCancellationEligibility(
            @Parameter(description = "Order ID", required = true, example = "123")
            @PathVariable Long orderId,

            @Parameter(description = "Customer ID for security validation", required = true, example = "1")
            @RequestParam Integer customerId) {

        log.debug("API: Checking cancellation eligibility for order: {} customer: {}", orderId, customerId);

        CancellationEligibilityResponse response = cancellationService
                .checkCancellationEligibility(orderId, customerId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cancel order (Customer)",
            description = "Allows customer to cancel their order with appropriate refund processing"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Cancellation not allowed or invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied - can only cancel own orders"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order already cancelled or cannot be cancelled")
    })
    @PostMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<CancellationResponse> cancelOrderByCustomer(
            @Parameter(description = "Order ID", required = true, example = "123")
            @PathVariable Long orderId,

            @Parameter(description = "Customer ID for security validation", required = true, example = "1")
            @RequestParam Integer customerId,

            @Parameter(description = "Cancellation reason", required = true)
            @RequestParam CancellationReason reason,

            @Parameter(description = "Additional details", example = "Changed my mind")
            @RequestParam(required = false) String details) {

        log.info("API: Customer {} cancelling order: {} reason: {}", customerId, orderId, reason);

        if (!isValidCustomerReason(reason)) {
            throw new IllegalArgumentException("Invalid cancellation reason for customer: " + reason);
        }

        CancellationResponse response = cancellationService
                .cancelOrderByCustomer(orderId, customerId, reason, details);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cancel order (Restaurant)",
            description = "Allows restaurant to cancel order due to operational issues"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid cancellation reason"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied - restaurant role required"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order cannot be cancelled")
    })
    @PostMapping("/restaurant")
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('ADMIN')")
    public ResponseEntity<CancellationResponse> cancelOrderByRestaurant(
            @Parameter(description = "Order ID", required = true, example = "123")
            @PathVariable Long orderId,

            @Parameter(description = "Restaurant ID", required = true, example = "5")
            @RequestParam Long restaurantId,

            @Parameter(description = "Cancellation reason", required = true)
            @RequestParam CancellationReason reason,

            @Parameter(description = "Detailed explanation", required = true)
            @RequestParam String details) {

        log.info("API: Restaurant {} cancelling order: {} reason: {}", restaurantId, orderId, reason);

        if (!isValidRestaurantReason(reason)) {
            throw new IllegalArgumentException("Invalid cancellation reason for restaurant: " + reason);
        }

        if (details == null || details.trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant cancellation requires detailed explanation");
        }

        CancellationResponse response = cancellationService
                .cancelOrderByRestaurant(orderId, restaurantId, reason, details);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cancel order (Admin)",
            description = "Allows admin to cancel order for customer service or policy reasons"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied - admin role required"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CancellationResponse> cancelOrderByAdmin(
            @Parameter(description = "Order ID", required = true, example = "123")
            @PathVariable Long orderId,

            @Parameter(description = "Admin user ID", required = true, example = "admin123")
            @RequestParam String adminId,

            @Parameter(description = "Cancellation details", required = true)
            @RequestBody @Valid CancelOrderRequest request) {

        log.info("API: Admin {} cancelling order: {} reason: {}", adminId, orderId, request.getReason());

        if (request.getReasonDetails() == null || request.getReasonDetails().trim().isEmpty()) {
            throw new IllegalArgumentException("Admin cancellation requires detailed justification");
        }

        CancellationResponse response = cancellationService
                .cancelOrderByAdmin(orderId, adminId, request.getReason(), request.getReasonDetails());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get cancellation details",
            description = "Retrieves detailed information about order cancellation and refund status"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancellation details retrieved"),
            @ApiResponse(responseCode = "404", description = "Order not cancelled or not found")
    })
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN') or hasRole('RESTAURANT')")
    public ResponseEntity<CancellationResponse> getCancellationDetails(
            @Parameter(description = "Order ID", required = true, example = "123")
            @PathVariable Long orderId) {

        log.debug("API: Retrieving cancellation details for order: {}", orderId);

        CancellationResponse response = cancellationService.getCancellationDetails(orderId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get customer cancellation history",
            description = "Retrieves all cancellations for a specific customer"
    )
    @GetMapping("/customer/{customerId}/history")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #customerId == authentication.principal.id)")
    public ResponseEntity<List<CancellationResponse>> getCustomerCancellationHistory(
            @Parameter(description = "Customer ID", required = true, example = "1")
            @PathVariable Integer customerId) {

        log.debug("API: Retrieving cancellation history for customer: {}", customerId);

        List<CancellationResponse> cancellations = cancellationService
                .getCancellationsByCustomer(customerId);

        return ResponseEntity.ok(cancellations);
    }

    @Operation(
            summary = "Get restaurant cancellation analytics",
            description = "Retrieves cancellation statistics for restaurant analysis"
    )
    @GetMapping("/restaurant/{restaurantId}/analytics")
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('ADMIN')")
    public ResponseEntity<List<CancellationResponse>> getRestaurantCancellationAnalytics(
            @Parameter(description = "Restaurant ID", required = true, example = "5")
            @PathVariable Long restaurantId) {

        log.debug("API: Retrieving cancellation analytics for restaurant: {}", restaurantId);

        List<CancellationResponse> cancellations = cancellationService
                .getCancellationsByRestaurant(restaurantId);

        return ResponseEntity.ok(cancellations);
    }


    private boolean isValidCustomerReason(CancellationReason reason) {
        return switch (reason) {
            case CUSTOMER_CHANGED_MIND, CUSTOMER_EMERGENCY, CUSTOMER_DUPLICATE_ORDER,
                 CUSTOMER_WRONG_ADDRESS, CUSTOMER_PAYMENT_ISSUE -> true;
            default -> false;
        };
    }


    private boolean isValidRestaurantReason(CancellationReason reason) {
        return switch (reason) {
            case RESTAURANT_UNAVAILABLE, RESTAURANT_ITEM_UNAVAILABLE,
                 RESTAURANT_KITCHEN_ISSUE, RESTAURANT_TOO_BUSY -> true;
            default -> false;
        };
    }
}