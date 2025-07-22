package spring.practice.elmenus_lite.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import spring.practice.elmenus_lite.model.audit.Auditable;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "menu")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Menu extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Integer id;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "menu_name", length = 100)
    private String name;

    @OneToMany(mappedBy = "menu", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<MenuItem> menuItems = new HashSet<>();
}
