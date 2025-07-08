package spring.practice.elmenus_lite.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;
import spring.practice.elmenus_lite.model.audit.Auditable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "transactions")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Transaction extends Auditable {
    @Id
    @UuidGenerator
    @Column(name = "transaction_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "transaction_details_id")
    private TransactionDetails transactionDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_status_id")
    private TransactionStatus transactionStatus;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
}
