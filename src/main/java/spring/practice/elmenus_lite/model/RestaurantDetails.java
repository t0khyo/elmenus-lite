package spring.practice.elmenus_lite.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import spring.practice.elmenus_lite.model.audit.Auditable;

import java.sql.Time;
import java.time.Duration;


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

    @Column(name = "estimated_delivery_time")
    private Duration estimatedDeliveryTime;

    @Column(name = "open_time", nullable = false)
    private Time openTime;

    @Column(name = "close_time", nullable = false)
    private Time closeTime;

    // Todo: add geolocation
}
