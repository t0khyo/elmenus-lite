package spring.practice.elmenus_lite.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.dto.OrderRequest;
import spring.practice.elmenus_lite.dto.OrderSummaryResponse;
import spring.practice.elmenus_lite.dto.PaymentResult;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.model.*;
import spring.practice.elmenus_lite.model.enums.OrderStatusEnum;
import spring.practice.elmenus_lite.model.enums.TransactionStatusEnum;
import spring.practice.elmenus_lite.repostory.CartItemRepository;
import spring.practice.elmenus_lite.repostory.OrderItemRepository;
import spring.practice.elmenus_lite.repostory.OrderRepository;
import spring.practice.elmenus_lite.repostory.OrderStatusRepository;
import spring.practice.elmenus_lite.service.OrderService;
import spring.practice.elmenus_lite.util.CustomerUtils;
import spring.practice.elmenus_lite.util.OrderUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderUtils orderUtils;
    private final CustomerUtils customerUtils;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusRepository orderStatusRepository;

    @Override
    @Transactional
    public OrderSummaryResponse placeOrder(OrderRequest orderRequest) {
        //TODO: Logging

        // Step 1: Entity Fetching & Validation
        Customer customer = customerUtils.fetchCustomer(orderRequest.customerId());
        Cart cart = customer.getCart();

        Restaurant restaurant = orderUtils.fetchAndValidateRestaurant(orderRequest.restaurantId());

        Address address = orderUtils.fetchAndValidateAddress(orderRequest.addressId(), customer.getId());

        PreferredPaymentSetting paymentSetting = orderUtils.fetchAndValidatePaymentSetting(
                orderRequest.preferredPaymentSettingId(), customer.getId());

        // Step 2: Cart Validation
        List<CartItem> cartItems = orderUtils.validateCartItems(cart.getId(), restaurant.getId());

        // Step 3: Calculate Totals
        BigDecimal subtotal = orderUtils.calculateSubtotal(cartItems);
        Promotion promotion = orderUtils.fetchAndValidatePromotion(orderRequest.promotionCode());
        BigDecimal discountAmount = orderUtils.calculateDiscountAmount(subtotal, promotion);
        BigDecimal total = subtotal.subtract(discountAmount);


        //TODO:  Step 4: Process Payment
        //PaymentResult paymentResult = paymentService.processPayment(customer.getId(), total, paymentSetting.getPreferredPaymentSettingId());
        PaymentResult dummyPaymentResult = new PaymentResult(TransactionStatusEnum.SUCCESS, "Cash", UUID.randomUUID().toString());

        // Step 5: Create Order
        Order order = createOrder(customer, address, promotion, subtotal, discountAmount, total, dummyPaymentResult.status());

        // Step 6: Create Order Items
        createOrderItems(order, cartItems);

        // Step 7: Clear Cart (Only if Payment Successful)
        handleCartCleanup(cart.getId(), dummyPaymentResult.status());

        // Step 8: Return OrderSummary
        return buildOrderSummary(order, dummyPaymentResult);
    }

    //Helper Method
    private Order createOrder(Customer customer, Address address, Promotion promotion,
                              BigDecimal subtotal, BigDecimal discountAmount, BigDecimal total,
                              TransactionStatusEnum transactionStatus) {

        OrderStatusEnum orderStatus = mapTransactionStatusToOrderStatus(transactionStatus);

        OrderStatus orderStatusEntity = orderStatusRepository.findByName(orderStatus.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("Order status not found: " + orderStatus.getStatus()));

        Order order = Order.builder()
                .customer(customer)
                .address(address)
                .orderStatus(orderStatusEntity)
                .promotion(promotion)
                .discountAmount(discountAmount)
                .subtotal(subtotal)
                .total(total)
                .orderDate(LocalDateTime.now())
                .build();

        order = orderRepository.save(order);
        return order;
    }

    private void createOrderItems(Order order, List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            MenuItem menuItem = cartItem.getMenuItem(); // Already validated

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .unitPrice(menuItem.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();

            orderItemRepository.save(orderItem);
        }
    }

    private void handleCartCleanup(Integer cartId, TransactionStatusEnum transactionStatus) {
        if (transactionStatus == TransactionStatusEnum.SUCCESS)
            cartItemRepository.deleteByCartId(cartId);
    }

    private OrderStatusEnum mapTransactionStatusToOrderStatus(TransactionStatusEnum transactionStatus) {
        return switch (transactionStatus) {
            case SUCCESS -> OrderStatusEnum.CONFIRMED;
            case FAILED -> OrderStatusEnum.FAILED;
            case PENDING -> OrderStatusEnum.PENDING;
        };
    }

    private OrderSummaryResponse buildOrderSummary(Order order, PaymentResult paymentResult) {
        OrderStatusEnum orderStatus = mapTransactionStatusToOrderStatus(paymentResult.status());

        return OrderSummaryResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotal())
                .status(orderStatus.getStatus())
                .paymentType(paymentResult.paymentMethod())
                .build();
    }

}
