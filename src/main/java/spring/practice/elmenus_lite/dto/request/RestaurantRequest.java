package spring.practice.elmenus_lite.dto.request;

import java.sql.Time;

public record RestaurantRequest(
        String name,
        String description,
        String phone,
        Time openTime,
        Time closeTime
) {

}
