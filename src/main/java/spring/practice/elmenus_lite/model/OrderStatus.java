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
@Table(name = "order_status")
@NoArgsConstructor
public class OrderStatus extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_status_id")
    private Integer id;

    @Column(name = "order_status_name", nullable = false, unique = true, length = 50)
    private String name;
}