package spring.practice.elmenus_lite.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.dto.*;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.mapper.OrderMapper;
import spring.practice.elmenus_lite.model.*;
import spring.practice.elmenus_lite.model.OrderItem;
import spring.practice.elmenus_lite.model.enums.OrderStatusEnum;
import spring.practice.elmenus_lite.model.enums.TransactionStatusEnum;
import spring.practice.elmenus_lite.repostory.*;
import spring.practice.elmenus_lite.service.OrderCalculation;
import spring.practice.elmenus_lite.service.OrderService;
import spring.practice.elmenus_lite.service.OrderValidation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderValidation orderValidation;
    private final OrderCalculation orderCalculation;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final TransactionRepository transactionRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderSummary placeOrder(NewOrderRequest newOrderRequest) {
        //TODO: Logging

        // Step 1: Entity Fetching & Validation
    
        Customer customer = orderValidation.fetchCustomer(newOrderRequest.customerId());
        Cart cart = orderValidation.fetchAndValidateCart(newOrderRequest.cartId(), customer.getId());
        Restaurant restaurant = orderValidation.fetchAndValidateRestaurant(newOrderRequest.restaurantId());
        Address address = orderValidation.fetchAndValidateAddress(newOrderRequest.addressId(), customer.getId());
        PreferredPaymentSetting paymentSetting = orderValidation.fetchAndValidatePaymentSetting(
                newOrderRequest.preferredPaymentSettingId(), customer.getId());

        // Step 2: Cart Validation
        List<CartItem> cartItems = orderValidation.validateCartItems(cart.getId(), restaurant.getId());


        // Step 3: Calculate Totals
        BigDecimal subtotal = orderCalculation.calculateSubtotal(cartItems);
        Promotion promotion = orderValidation.fetchAndValidatePromotion(newOrderRequest.promotionCode());
        BigDecimal discountAmount = orderCalculation.calculateDiscountAmount(subtotal, promotion);
        BigDecimal total = subtotal.subtract(discountAmount);


        //TODO:  Step 4: Process Payment
        //PaymentResult paymentResult = paymentService.processPayment(customer.getId(), total, paymentSetting.getPreferredPaymentSettingId());
        PaymentResult dummyPaymentResult=new PaymentResult(TransactionStatusEnum.SUCCESS,"Cash","31398913");

        // Step 5: Create Order
        Order order = createOrder(customer, address, promotion, subtotal, discountAmount, total, dummyPaymentResult.status());

        // Step 6: Create Order Items
        createOrderItems(order, cartItems);

        // Step 7: Clear Cart (Only if Payment Successful)
        handleCartCleanup(cart.getId(), dummyPaymentResult.status());

        // Step 8: Return OrderSummary
        return buildOrderSummary(order, dummyPaymentResult);
    }

    @Override
    public OrderSummary getOrderSummary(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        Transaction transaction=transactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with OrderID: " + orderId));
        return orderMapper.toOrderSummary(order, transaction.getPaymentMethod().getPaymentType());
    }

    @Override
    public OrderDetails getOrderDetails(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        DeliveryAddress deliveryAddress = orderMapper.toDeliveryAddress(order.getAddress());
        List<OrderItemDTO> orderItems = order.getOrderItems().stream().map(orderMapper::toOrderItemDTO).toList();
        Transaction transaction=transactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with OrderID: " + orderId));
        String paymentType = transaction.getPaymentMethod().getPaymentType();
        UUID id = transaction.getId();
        BigDecimal amount = transaction.getAmount();
        String transactionStatusName = transaction.getTransactionStatus().getTransactionStatusName();
        PaymentDetails paymentDetails=new PaymentDetails(paymentType,id,amount,transactionStatusName);
        String promotion = order.getPromotion() != null ? order.getPromotion().getName() : "";
        TrackingInfo trackingInfo = orderMapper.toTrackingInfo(order.getOrderTracking());
        return new OrderDetails(order.getId(),order.getOrderDate(),order.getOrderStatus().getName(),orderItems,paymentDetails,deliveryAddress,trackingInfo,promotion);
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
    private OrderSummary buildOrderSummary(Order order, PaymentResult paymentResult) {
        OrderStatusEnum orderStatus = mapTransactionStatusToOrderStatus(paymentResult.status());

        return OrderSummary.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotal())
                .status(orderStatus.getStatus())
                .paymentType(paymentResult.paymentMethod())
                .build();
    }

}
