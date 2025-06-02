package spring.practice.elmenus_lite.dto;

import java.time.Duration;
import java.time.LocalDateTime;

public record TrackingInfo(
//        String currentLocation, //TODO : when complete Geolocation
        Duration estimatedTime
        // but will need to update database schema
            //        String deliveryPersonName, // Can be null if not assigned yet
            //        String deliveryPersonContact // Can be null
) {
}
