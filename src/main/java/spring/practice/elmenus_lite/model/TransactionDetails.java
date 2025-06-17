package spring.practice.elmenus_lite.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;
import spring.practice.elmenus_lite.model.audit.Auditable;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "transaction_details")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TransactionDetails extends Auditable {
    @Id
    @UuidGenerator
    @Column(name = "transaction_details_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
}
