package de.bank.atm;

import de.bank.atm.domain.AccountWithdrawTO;
import de.bank.atm.entity.ATMState;
import de.bank.atm.entity.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public class TestData {

    public static Optional<Account> account1() {
        return Optional.of(Account.builder()
                .accountNumber("01001")
                .amount(new BigDecimal("2738.59"))
                .build());
    }

    public static Optional<Account> account2() {
        return Optional.of(Account.builder()
                .accountNumber("01002")
                .amount(new BigDecimal("23.00"))
                .build());
    }

    public static Optional<Account> account3() {
        return Optional.of(Account.builder()
                .accountNumber("01003")
                .amount(new BigDecimal("0.00"))
                .build());
    }

    public static ATMState atmState(long balance, long fifties, long twenties, long tens, long fives) {
        return ATMState.builder()
                .id(null)
                .atmBalance(new BigDecimal(balance))
                .stackOfFifty(fifties)
                .stackOfTwenty(twenties)
                .stackOfTen(tens)
                .stackOfFive(fives)
                .changeTime(LocalDateTime.now())
                .build();
    }

    public static AccountWithdrawTO accountWithdrawTO(boolean success, BigDecimal newBalance) {
        return AccountWithdrawTO.builder()
                .success(success)
                .newBalance(newBalance)
                .build();
    }
}
