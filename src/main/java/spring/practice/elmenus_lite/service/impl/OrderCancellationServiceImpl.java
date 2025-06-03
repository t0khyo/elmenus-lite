package spring.practice.elmenus_lite.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.dto.request.CancelOrderRequest;
import spring.practice.elmenus_lite.dto.response.CancellationEligibilityResponse;
import spring.practice.elmenus_lite.dto.response.CancellationResponse;
import spring.practice.elmenus_lite.exception.*;
import spring.practice.elmenus_lite.mapper.CancellationMapper;
import spring.practice.elmenus_lite.model.*;
import spring.practice.elmenus_lite.model.enums.*;
import spring.practice.elmenus_lite.repository.*;
import spring.practice.elmenus_lite.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCancellationServiceImpl implements OrderCancellationService {

    // Repository dependencies
    private final OrderRepository orderRepository;
    private final OrderCancellationRepository cancellationRepository;
    private final CustomerRepository customerRepository;

    // Service dependencies
    private final PaymentProcessingService paymentProcessingService;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    private final RestaurantNotificationService restaurantNotificationService;
    private final OrderStatusService orderStatusService;

    // Mapping
    private final CancellationMapper cancellationMapper;


    @Override
    public CancellationEligibilityResponse checkCancellationEligibility(Long orderId, Integer customerId) {
        log.debug("Checking cancellation eligibility for order: {} customer: {}", orderId, customerId);

        // Validate order exists and belongs to customer
        Order order = orderRepository.findByIdAndCustomerIdAndIsDeletedFalse(orderId, customerId)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Order not found with id: %d for customer: %d", orderId, customerId)));

        // Check if already cancelled
        if (order.isCancelled()) {
            return CancellationEligibilityResponse.builder()
                    .orderId(orderId)
                    .eligible(false)
                    .reason("Order is already cancelled")
                    .cancellationFee(BigDecimal.ZERO)
                    .refundAmount(BigDecimal.ZERO)
                    .build();
        }

        // Check business rules
        boolean eligible = order.isCancellable();
        String reason = null;
        BigDecimal cancellationFee = BigDecimal.ZERO;
        BigDecimal refundAmount = BigDecimal.ZERO;

        if (!eligible) {
            reason = determineCancellationIneligibilityReason(order);
        } else {
            // Calculate financial impact
            refundAmount = order.calculateRefundAmount();
            cancellationFee = order.getTotal().subtract(refundAmount);
        }

        return CancellationEligibilityResponse.builder()
                .orderId(orderId)
                .eligible(eligible)
                .reason(reason)
                .cancellationFee(cancellationFee)
                .refundAmount(refundAmount)
                .timeLimit(calculateTimeLimit(order))
                .build();
    }


    @Override
    @Transactional
    public CancellationResponse cancelOrder(Long orderId, CancelOrderRequest request, CancellationInitiator initiator) {
        log.info("Processing cancellation for order: {} by: {} reason: {}",
                orderId, initiator, request.getReason());

        try {

            Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

            if (order.isCancelled()) {
                throw new OrderAlreadyCancelledException("Order is already cancelled");
            }


            validateCancellationRules(order, request, initiator);


            CancellationFinancialImpact impact = calculateFinancialImpact(order, request.getReason(), initiator);


            OrderCancellation cancellation = createCancellationRecord(order, request, initiator, impact);
            cancellationRepository.save(cancellation);


            orderStatusService.updateOrderStatus(order, OrderStatusEnum.CANCELLED);
            order.setCancellation(cancellation);
            orderRepository.save(order);


            if (!order.getOrderItems().isEmpty()) {
                inventoryService.releaseReservation(order.getOrderItems());
                log.debug("Released inventory for cancelled order: {}", orderId);
            }


            if (impact.getRefundAmount().compareTo(BigDecimal.ZERO) > 0) {
                processRefundAsync(cancellation);
            }


            if (impact.getRestaurantCompensation().compareTo(BigDecimal.ZERO) > 0) {
                processRestaurantCompensationAsync(cancellation);
            }


            sendCancellationNotifications(order, cancellation);

            log.info("Order cancellation completed successfully: orderId={}, refund={}",
                    orderId, impact.getRefundAmount());

            return cancellationMapper.toCancellationResponse(cancellation);

        } catch (Exception e) {
            log.error("Failed to cancel order: {}", orderId, e);


            if (e instanceof OrderManagementException) {
                throw e;
            } else {
                throw new OrderCancellationException("Failed to cancel order: " + e.getMessage(), e);
            }
        }
    }


    @Override
    @Transactional
    public CancellationResponse cancelOrderByCustomer(Long orderId, Integer customerId,
                                                      CancellationReason reason, String details) {
        log.info("Customer {} cancelling order: {} reason: {}", customerId, orderId, reason);

        // Additional customer-specific validation
        Order order = orderRepository.findByIdAndCustomerIdAndIsDeletedFalse(orderId, customerId)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Order not found with id: %d for customer: %d", orderId, customerId)));

        validateCustomerCancellationRules(order, reason);

        CancelOrderRequest request = CancelOrderRequest.builder()
                .reason(reason)
                .reasonDetails(details)
                .requestedBy(customerId.toString())
                .build();

        return cancelOrder(orderId, request, CancellationInitiator.CUSTOMER);
    }

        @Override
    @Transactional
    public CancellationResponse cancelOrderByRestaurant(Long orderId, Long restaurantId,
                                                        CancellationReason reason, String details) {
        log.info("Restaurant {} cancelling order: {} reason: {}", restaurantId, orderId, reason);

        // Validate restaurant owns this order
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        validateRestaurantOwnership(order, restaurantId);

        CancelOrderRequest request = CancelOrderRequest.builder()
                .reason(reason)
                .reasonDetails(details)
                .requestedBy("restaurant_" + restaurantId)
                .build();

        return cancelOrder(orderId, request, CancellationInitiator.RESTAURANT);
    }


    @Override
    @Transactional
    public CancellationResponse cancelOrderBySystem(Long orderId, CancellationReason reason, String details) {
        log.info("System cancelling order: {} reason: {}", orderId, reason);

        CancelOrderRequest request = CancelOrderRequest.builder()
                .reason(reason)
                .reasonDetails(details)
                .requestedBy("SYSTEM")
                .build();

        return cancelOrder(orderId, request, CancellationInitiator.SYSTEM);
    }

    @Override
    @Transactional
    public CancellationResponse cancelOrderByAdmin(Long orderId, String adminId,
                                                   CancellationReason reason, String details) {
        log.info("Admin {} cancelling order: {} reason: {}", adminId, orderId, reason);

        CancelOrderRequest request = CancelOrderRequest.builder()
                .reason(reason)
                .reasonDetails(details)
                .requestedBy("admin_" + adminId)
                .build();

        return cancelOrder(orderId, request, CancellationInitiator.ADMIN);
    }

    @Override
    public CancellationResponse getCancellationDetails(Long orderId) {
        OrderCancellation cancellation = cancellationRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CancellationNotFoundException("No cancellation found for order: " + orderId));

        return cancellationMapper.toCancellationResponse(cancellation);
    }

    @Override
    @Async
    public void processPendingRefunds() {
        log.info("Processing pending refunds");

        List<OrderCancellation> pendingRefunds = cancellationRepository.findPendingRefunds();

        for (OrderCancellation cancellation : pendingRefunds) {
            try {
                processRefund(cancellation);
            } catch (Exception e) {
                log.error("Failed to process refund for cancellation: {}", cancellation.getId(), e);
            }
        }
    }

    @Override
    @Async
    public void processRestaurantCompensations() {
        log.info("Processing restaurant compensations");

        List<OrderCancellation> pendingCompensations =
                cancellationRepository.findPendingRestaurantCompensation();

        for (OrderCancellation cancellation : pendingCompensations) {
            try {
                processRestaurantCompensation(cancellation);
            } catch (Exception e) {
                log.error("Failed to process restaurant compensation for cancellation: {}",
                        cancellation.getId(), e);
            }
        }
    }

    @Override
    @Async
    public void cleanupExpiredOrders() {
        log.info("Cleaning up expired orders");

        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<Order> expiredOrders = orderRepository.findExpiredCancellableOrders(cutoffTime);

        for (Order order : expiredOrders) {
            try {
                cancelOrderBySystem(order.getId(),
                        CancellationReason.SYSTEM_TECHNICAL_ERROR,
                        "Order expired - no payment received");
            } catch (Exception e) {
                log.error("Failed to cleanup expired order: {}", order.getId(), e);
            }
        }
    }

    @Override
    public List<CancellationResponse> getCancellationsByCustomer(Integer customerId) {
        List<OrderCancellation> cancellations = cancellationRepository.findByCustomerId(customerId);
        return cancellations.stream()
                .map(cancellationMapper::toCancellationResponse)
                .toList();
    }

    @Override
    public List<CancellationResponse> getCancellationsByRestaurant(Long restaurantId) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        List<OrderCancellation> cancellations = cancellationRepository
                .findByRestaurantIdAndDateRange(restaurantId, startDate, endDate);

        return cancellations.stream()
                .map(cancellationMapper::toCancellationResponse)
                .toList();
    }

    private String determineCancellationIneligibilityReason(Order order) {
        if (order.isCancelled()) {
            return "Order is already cancelled";
        }

        String statusName = order.getOrderStatus().getOrderStatusName();
        if ("DELIVERED".equals(statusName)) {
            return "Order has already been delivered";
        }

        LocalDateTime cutoffTime = order.getOrderDate().plusMinutes(30);
        if (LocalDateTime.now().isAfter(cutoffTime)) {
            return "Cancellation time limit exceeded (30 minutes)";
        }

        return "Order cannot be cancelled in current status: " + statusName;
    }

    private LocalDateTime calculateTimeLimit(Order order) {
        return order.getOrderDate().plusMinutes(30);
    }

    private void validateCancellationRules(Order order, CancelOrderRequest request, CancellationInitiator initiator) {
        // Different initiators have different rules
        switch (initiator) {
            case CUSTOMER -> validateCustomerCancellationRules(order, request.getReason());
            case RESTAURANT -> validateRestaurantCancellationRules(order, request.getReason());
            case SYSTEM -> validateSystemCancellationRules(order, request.getReason());
            case ADMIN -> validateAdminCancellationRules(order, request.getReason());
        }
    }

    private void validateCustomerCancellationRules(Order order, CancellationReason reason) {
        if (!order.isCancellable()) {
            throw new OrderCancellationNotAllowedException("Order cannot be cancelled in current status");
        }

        // Customer-specific time limits
        if ("PROCESSING".equals(order.getOrderStatus().getOrderStatusName())) {
            LocalDateTime cutoffTime = order.getOrderDate().plusMinutes(15);
            if (LocalDateTime.now().isAfter(cutoffTime)) {
                throw new OrderCancellationTimeExpiredException(
                        "Customer cancellation not allowed after 15 minutes when order is being processed");
            }
        }
    }

    private void validateRestaurantCancellationRules(Order order, CancellationReason reason) {
        // Restaurant can cancel until shipped
        String statusName = order.getOrderStatus().getOrderStatusName();
        if ("SHIPPED".equals(statusName) || "DELIVERED".equals(statusName)) {
            throw new OrderCancellationNotAllowedException(
                    "Restaurant cannot cancel order after shipping");
        }
    }

    private void validateSystemCancellationRules(Order order, CancellationReason reason) {
        // System can cancel any non-delivered order
        if ("DELIVERED".equals(order.getOrderStatus().getOrderStatusName())) {
            throw new OrderCancellationNotAllowedException("Cannot cancel delivered order");
        }
    }

    private void validateAdminCancellationRules(Order order, CancellationReason reason) {
        // Admin has most flexibility but should provide good reason
        if (reason == null) {
            throw new IllegalArgumentException("Admin cancellation requires a reason");
        }
    }

    private void validateRestaurantOwnership(Order order, Long restaurantId) {
        // Check if restaurant owns this order
        boolean restaurantOwnsOrder = order.getOrderItems().stream()
                .anyMatch(item -> item.getMenuItem().getMenu().getRestaurant().getId().equals(restaurantId));

        if (!restaurantOwnsOrder) {
            throw new UnauthorizedCancellationException(
                    "Restaurant does not own this order: " + order.getId());
        }
    }

    private CancellationFinancialImpact calculateFinancialImpact(Order order, CancellationReason reason,
                                                                 CancellationInitiator initiator) {
        BigDecimal refundAmount = BigDecimal.ZERO;
        BigDecimal cancellationFee = BigDecimal.ZERO;
        BigDecimal restaurantCompensation = BigDecimal.ZERO;

        // Calculate refund based on reason and initiator
        if (order.hasSuccessfulPayment()) {
            switch (initiator) {
                case CUSTOMER -> {
                    refundAmount = order.calculateRefundAmount();
                    cancellationFee = order.getTotal().subtract(refundAmount);

                    // Restaurant compensation for late customer cancellations
                    if (reason.requiresRestaurantCompensation()) {
                        restaurantCompensation = order.getTotal().multiply(new BigDecimal("0.1"));
                    }
                }
                case RESTAURANT, SYSTEM -> {
                    // Full refund for restaurant/system cancellations
                    refundAmount = order.getTotal();
                }
                case ADMIN -> {
                    // Admin decides refund amount based on reason
                    refundAmount = reason.allowsFullRefund() ? order.getTotal() : order.calculateRefundAmount();
                }
            }
        }

        return CancellationFinancialImpact.builder()
                .refundAmount(refundAmount)
                .cancellationFee(cancellationFee)
                .restaurantCompensation(restaurantCompensation)
                .build();
    }

    private OrderCancellation createCancellationRecord(Order order, CancelOrderRequest request,
                                                       CancellationInitiator initiator,
                                                       CancellationFinancialImpact impact) {
        return OrderCancellation.builder()
                .order(order)
                .initiatedBy(initiator)
                .cancellationReason(request.getReason())
                .reasonDetails(request.getReasonDetails())
                .refundAmount(impact.getRefundAmount())
                .restaurantCompensation(impact.getRestaurantCompensation())
                .cancellationFee(impact.getCancellationFee())
                .processingStatus(CancellationProcessingStatus.PENDING)
                .cancelledAt(LocalDateTime.now())
                .createdBy(request.getRequestedBy())
                .build();
    }

    @Async
    private void processRefundAsync(OrderCancellation cancellation) {
        try {
            processRefund(cancellation);
        } catch (Exception e) {
            log.error("Async refund processing failed for cancellation: {}", cancellation.getId(), e);
        }
    }

    private void processRefund(OrderCancellation cancellation) {
        log.info("Processing refund for cancellation: {} amount: {}",
                cancellation.getId(), cancellation.getRefundAmount());

        try {
            paymentProcessingService.processRefund(
                    cancellation.getOrder(),
                    cancellation.getRefundAmount(),
                    "Order cancellation: " + cancellation.getCancellationReason().getDisplayName()
            );

            cancellation.setRefundProcessedAt(LocalDateTime.now());
            cancellation.setProcessingStatus(CancellationProcessingStatus.REFUND_COMPLETED);
            cancellationRepository.save(cancellation);

            log.info("Refund processed successfully for cancellation: {}", cancellation.getId());

        } catch (Exception e) {
            log.error("Refund processing failed for cancellation: {}", cancellation.getId(), e);
            cancellation.setProcessingStatus(CancellationProcessingStatus.FAILED);
            cancellationRepository.save(cancellation);
            throw e;
        }
    }

    @Async
    private void processRestaurantCompensationAsync(OrderCancellation cancellation) {
        try {
            processRestaurantCompensation(cancellation);
        } catch (Exception e) {
            log.error("Async restaurant compensation failed for cancellation: {}", cancellation.getId(), e);
        }
    }

    private void processRestaurantCompensation(OrderCancellation cancellation) {
        log.info("Processing restaurant compensation for cancellation: {} amount: {}",
                cancellation.getId(), cancellation.getRestaurantCompensation());

        try {
            // Notify restaurant about compensation
            restaurantNotificationService.notifyRestaurantCompensation(cancellation);

            cancellation.setRestaurantNotifiedAt(LocalDateTime.now());
            cancellationRepository.save(cancellation);

            log.info("Restaurant compensation notification sent for cancellation: {}", cancellation.getId());

        } catch (Exception e) {
            log.error("Restaurant compensation notification failed for cancellation: {}",
                    cancellation.getId(), e);
            throw e;
        }
    }

    private void sendCancellationNotifications(Order order, OrderCancellation cancellation) {
        try {
            // Customer notification
            notificationService.sendOrderCancellation(order, cancellation.getReasonDetails());

            // Restaurant notification (if not restaurant-initiated)
            if (cancellation.getInitiatedBy() != CancellationInitiator.RESTAURANT) {
                restaurantNotificationService.notifyOrderCancellation(order, cancellation);
            }

        } catch (Exception e) {
            log.warn("Failed to send cancellation notifications for order: {}", order.getId(), e);
            // Don't fail the cancellation for notification failures
        }
    }

    // Helper class for financial calculations
    @Builder
    @Data
    private static class CancellationFinancialImpact {
        private BigDecimal refundAmount;
        private BigDecimal cancellationFee;
        private BigDecimal restaurantCompensation;
    }
}
