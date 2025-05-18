package spring.practice.elmenus_lite.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_tracking")
@Data
public class OrderTracking {
    @Id
    @Column(name = "order_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    // TODO add geolocation

    @Column(name = "estimated_time")
    private String estimatedTime;
}