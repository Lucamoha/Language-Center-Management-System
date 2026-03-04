package com.model.financial;

import com.model.user.Student;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    Long invoiceID;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    Student student;

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    Payment payment;

    @Column(name = "total_amount")
    BigDecimal totalAmount;

    @CreationTimestamp
    @Column(name = "issued_at")
    LocalDateTime issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    InvoiceStatus status;
}
