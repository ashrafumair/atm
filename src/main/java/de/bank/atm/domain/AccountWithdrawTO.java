package de.bank.atm.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountWithdrawTO {
    private boolean success;
    private BigDecimal newBalance;
}
