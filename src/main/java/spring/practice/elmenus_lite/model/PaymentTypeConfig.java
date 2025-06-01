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
@Table(name = "payment_type_config")
@NoArgsConstructor
public class PaymentTypeConfig extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_type_config_id")
    private Integer paymentTypeConfigId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_integration_type_id", nullable = false)
    private PaymentIntegrationType paymentIntegrationType;

    @Column(name = "config_details",columnDefinition = "TEXT")
    private String configDetails;

    @Column(name = "config_key", nullable = false, length = 100)
    private String configKey;

    @Column(name = "config_value", nullable = false, length = 255)
    private String configValue;

    @OneToMany(mappedBy = "paymentTypeConfig")
    private Set<PreferredPaymentSetting> preferredPaymentSettings = new HashSet<>();
}
