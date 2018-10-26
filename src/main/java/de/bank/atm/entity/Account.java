package de.bank.atm.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
@Builder
public class Account {

    @Id
    @GeneratedValue
    private Long id;
    private String accountNumber;
    private BigDecimal amount;
}
