// model/OrderTracking.java
package com.javaeats.model;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;

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
    
    @Column(name = "current_location", columnDefinition = "geography(Point,4326)")
    private Point currentLocation;
    
    @Column(name = "estimated_time")
    private String estimatedTime;
}