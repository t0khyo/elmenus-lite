package spring.practice.elmenus_lite.model;

import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import spring.practice.elmenus_lite.model.audit.Auditable;

import java.time.Duration;
import java.time.LocalTime;


@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "restaurant_details")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RestaurantDetails extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_details_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false, unique = true)
    private Restaurant restaurant;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "phone", length = 15)
    private String phone;

    @Type(PostgreSQLIntervalType.class)
    @Column(name = "estimated_delivery_time", columnDefinition = "interval")
    private Duration estimatedDeliveryTime;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Builder.Default
    @Column(name = "average_rating", nullable = false)
    private Double averageRating = 0.0;

    @Builder.Default
    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    // Todo: add geolocation

    public static RestaurantDetails buildRestaurantDetails(Restaurant existingRestaurant) {
        return RestaurantDetails.builder()
                .restaurant(existingRestaurant)
                .build();
    }
}
