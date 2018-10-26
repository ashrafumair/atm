package de.bank.atm.service;

import de.bank.atm.domain.AccountWithdrawTO;
import de.bank.atm.entity.Account;
import de.bank.atm.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private AccountRepository accountRepo;

    @Autowired
    AccountServiceImpl(AccountRepository repository) {
        accountRepo = repository;
    }

    @Override
    public BigDecimal checkBalance(String accountNumber) {
        Optional<Account> account = accountRepo.findOptionalByAccountNumber(accountNumber);
        return account.map(Account::getAmount).orElse(null);
    }

    @Override
    @Transactional
    public AccountWithdrawTO withdrawAmount(String accountNumber, BigDecimal withdrawAmount) {
        Optional<Account> accountOptional = accountRepo.findOptionalByAccountNumber(accountNumber);
        if (!accountOptional.isPresent()) {
            return null;
        }
        Account account = accountOptional.get();
        AccountWithdrawTO accountWithdrawTO = null;

        if (isBalanceAvailable(account.getAmount(), withdrawAmount)) {
            logger.debug("AccountService.withdrawAmount: Amount={} available in account{}.", withdrawAmount, accountNumber);
            BigDecimal newBalance = account.getAmount().subtract(withdrawAmount);
            account.setAmount(newBalance);
            accountWithdrawTO = prepareResultObj(true, newBalance);
        } else {
            logger.debug("AccountService.withdrawAmount: Amount={} not available in account{}.", withdrawAmount, accountNumber);
            accountWithdrawTO = prepareResultObj(false, account.getAmount());
        }
        return accountWithdrawTO;
    }

    private AccountWithdrawTO prepareResultObj(boolean isSuccessful, BigDecimal newBalance) {
        return AccountWithdrawTO.builder()
                .success(isSuccessful)
                .newBalance(newBalance)
                .build();
    }

    private boolean isBalanceAvailable(BigDecimal accountBalance, BigDecimal withdrawAmount) {
        return accountBalance.compareTo(withdrawAmount) >= 0;
    }

}
