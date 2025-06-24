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
@Table(name = "payment_integration_type")
@NoArgsConstructor
public class PaymentIntegrationType extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_integration_type_id")
    private Integer paymentIntegrationTypeId;

    @Column(name = "payment_integration_type_name", nullable = false, length = 50, unique = true)
    private String paymentIntegrationTypeName;

    @OneToMany(mappedBy = "paymentIntegrationType", fetch = FetchType.LAZY)
    private Set<PaymentTypeConfig> paymentTypeConfigs = new HashSet<>();
}
