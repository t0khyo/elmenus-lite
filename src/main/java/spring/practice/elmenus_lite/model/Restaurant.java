package spring.practice.elmenus_lite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import spring.practice.elmenus_lite.model.audit.Auditable;

import java.util.HashSet;
import java.util.Set;


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
    private String name;

    @OneToOne(mappedBy = "restaurant", optional = false, orphanRemoval = true)
    private RestaurantDetails details;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Menu> menus = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "restaurant_category",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    @Column(name = "active", nullable = false)
    private boolean active;

    public boolean getActive() {
        return active;
    }
}
