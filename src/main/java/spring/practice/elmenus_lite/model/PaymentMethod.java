package spring.practice.elmenus_lite.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "payment_method",
        uniqueConstraints = @UniqueConstraint(columnNames = "payment_type"))
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_method_id")
    private Integer id;

    @Column(name = "payment_type", nullable = false, length = 50, unique = true)
    private String paymentType;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "provider_data", columnDefinition = "TEXT")
    private String providerData;
}
