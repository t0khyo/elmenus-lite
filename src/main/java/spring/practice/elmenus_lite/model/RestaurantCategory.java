package spring.practice.elmenus_lite.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import spring.practice.elmenus_lite.model.audit.Auditable;

import java.io.Serializable;

@Entity
@Table(name = "restaurant_category")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantCategory  extends Auditable {
    // Composite primary key class
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestaurantCategoryId implements Serializable {
        @Column(name = "restaurant_id")
        private Integer restaurantId;

        @Column(name = "category_id")
        private Integer categoryId;
    }

    @EmbeddedId
    private RestaurantCategoryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("restaurantId") // Maps the 'restaurantId' part of the composite key
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId") // Maps the 'categoryId' part of the composite key
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
    private Category category;


    // Convenience constructor for creating a new association.
    // The createdBy/updatedBy fields will be automatically set by the AuditingEntityListener
    public RestaurantCategory(Restaurant restaurant, Category category) {
        this.id = new RestaurantCategoryId(restaurant.getId(), category.getId());
        this.restaurant = restaurant;
        this.category = category;
    }
}
