package de.bank.atm.service;

import de.bank.atm.domain.AccountWithdrawTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface AccountService {

    BigDecimal checkBalance(String accountNumber);

    AccountWithdrawTO withdrawAmount(String accountNumber, BigDecimal withdrawAmount);
}
