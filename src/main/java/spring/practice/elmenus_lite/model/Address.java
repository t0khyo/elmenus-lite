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
@Table(name = "address")
@NoArgsConstructor
public class Address extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "label", length = 50)
    private String label;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "city", length = 50, nullable = false)
    private String city;

    @Column(name = "floor", length = 50)
    private String floor;

    @Column(name = "apartment", length = 50)
    private String apartment;

    @Column(name = "additional_direction", columnDefinition = "TEXT")
    private String additionalDirection;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    // TODO: add geolocation column
}
