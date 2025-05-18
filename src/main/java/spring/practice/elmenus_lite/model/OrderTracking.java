package spring.practice.elmenus_lite.model;

import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.Duration;

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

    @Type(value = PostgreSQLIntervalType.class)
    @Column(name = "estimated_time")
    private Duration estimatedTime;
}