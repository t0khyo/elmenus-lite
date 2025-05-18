package spring.practice.elmenus_lite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import spring.practice.elmenus_lite.model.audit.Auditable;


@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "restaurant")
@NoArgsConstructor
public class Restaurant extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Integer id;

    @Column(name = "restaurant_name", nullable = false, length = 100, unique = true)
    private String restaurantName;

    @OneToOne(mappedBy = "restaurant", optional = false, orphanRemoval = true)
    private RestaurantDetails details;

    @Column(name = "active", nullable = false)
    private boolean active;
}
