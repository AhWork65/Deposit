package com.heydari.deposit.model.transaction;

import com.heydari.deposit.model.deposit.Deposit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date todoDate;
    private BigDecimal price;
    private TransactionStatus status;
    private TransactionsType type;
    private String sourceNumber;
    private String destinationNumber;
    private String issueTracking;
    private String description;


}
