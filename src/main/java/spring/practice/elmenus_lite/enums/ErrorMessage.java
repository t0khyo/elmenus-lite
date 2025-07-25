package spring.practice.elmenus_lite.enums;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

@RequiredArgsConstructor
public enum ErrorMessage {

    CART_ITEM_NOT_FOUND("Cart Item not found with id: {0}"),
    CART_NOT_FOUND("Cart not found with id: {0}"),
    CART_NOT_FOUND_FOR_CUSTOMER("Cart Not found with customer id: {0}"),
    CUSTOMER_NOT_FOUND("Customer not found with id: {0}"),
    MENU_ITEM_NOT_FOUND("Menu item not found with id: {0}"),
    ORDER_STATUS_NOT_FOUND("Order status not found: {0}"),
    RESTAURANT_NOT_FOUND("Restaurant not found with ID: {0}"),
    ADDRESS_NOT_FOUND("Address not found with ID: {0}"),
    PAYMENT_SETTING_NOT_FOUND("Payment setting not found with ID: {0}"),
    PROMOTION_NOT_FOUND("Promotion not found with code: {0}"),
    ENTITY_OWNERSHIP_VIOLATION("{0} does not belong to the specified customer"),
    RESTAURANT_INACTIVE("Restaurant is currently inactive"),
    PROMOTION_EXPIRED("Promotion code is not valid or has expired"),
    CART_EMPTY("Cart is empty"),
    MENU_ITEM_WRONG_RESTAURANT("Menu item {0} does not belong to the specified restaurant"),
    MENU_ITEM_UNAVAILABLE("Menu item {0} is currently unavailable"),
    ORDER_TRANSACTION_NOT_FOUND("Transaction not found with order id: {0}"),
    ORDER_NOT_FOUND("Order not found with id: {0}"),
    CART_ITEM_DOES_NOT_BELONG_TO_CART("CartItem with id: {0} does not belong to Cart with id {1}."),
    MENU_NOT_FOUND("Menu not found with id: {0}"),
    EMAIL_NOT_REGISTERED("Email is not registered."),
    INVALID_TOKEN_SIGNATURE("Invalid token signature."),
    TOKEN_EXPIRED("Token expired."),
    EMAIL_NOT_FOUND("No user found with the given email."),
    EMAIL_ALREADY_EXISTS("Email already exists."),
    ROLE_NOT_FOUND("Role not found: {0}"),
    USER_TYPE_NOT_FOUND("User type not found: {0}"),
    JWT_GENERATION_FAILED("Failed to generate access token");


    private final String messageTemplate;

    public String getFinalMessage(Object... params) {
        return MessageFormat.format(messageTemplate, params);
    }
}