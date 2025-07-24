package spring.practice.elmenus_lite.model;

import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import java.time.Duration;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "order_tracking")
@NoArgsConstructor
public class OrderTracking {
    @Id
    @Column(name = "order_tracking_id")
    private Integer id;

    // TODO add geolocation

    @Type(PostgreSQLIntervalType.class)
    @Column(name = "estimated_time", columnDefinition = "interval")
    private Duration estimatedTime;
}