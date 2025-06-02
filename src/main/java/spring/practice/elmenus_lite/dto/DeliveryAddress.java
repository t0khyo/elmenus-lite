package spring.practice.elmenus_lite.dto;

public record DeliveryAddress(
        String street,
        String city,
        String floor,
        String apartment
) {
}
