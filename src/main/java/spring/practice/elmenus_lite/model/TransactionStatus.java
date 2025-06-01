package spring.practice.elmenus_lite.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import spring.practice.elmenus_lite.model.audit.Auditable;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "transaction_status")
@NoArgsConstructor
public class TransactionStatus extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_status_id")
    private Integer id;

    @Column(name = "transaction_status_name", nullable = false, length = 50, unique = true)
    private String transactionStatusName;

}
