package de.bank.atm.service;

import de.bank.atm.domain.CurrencyNote;
import de.bank.atm.domain.Denomination;
import de.bank.atm.entity.ATMState;
import de.bank.atm.exception.ATMWithdrawException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ATMService {

    ATMState replenishATM(List<CurrencyNote> currencyNotes);

    String checkAccountBalance(String accountNumber);

    Map<Denomination, Long> withrawAccountBalance(String accountNumber, BigDecimal amount) throws ATMWithdrawException;
}
