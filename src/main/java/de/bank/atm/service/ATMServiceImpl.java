package de.bank.atm.service;

import de.bank.atm.domain.AccountWithdrawTO;
import de.bank.atm.domain.CurrencyNote;
import de.bank.atm.domain.Denomination;
import de.bank.atm.entity.ATMState;
import de.bank.atm.exception.ATMWithdrawException;
import de.bank.atm.repository.ATMStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

public class ATMServiceImpl implements ATMService {
    Logger logger = LoggerFactory.getLogger(ATMServiceImpl.class);

    private ATMStateRepository atmRepository;
    private AccountService accountService;

    @Autowired
    ATMServiceImpl(ATMStateRepository repository, AccountService accountService) {
        this.atmRepository = repository;
        this.accountService = accountService;
    }

    @Override
    @Transactional
    public ATMState replenishATM(List<CurrencyNote> currencyNotes) {
        ATMState atmState = atmRepository.findOptionalFirstByOrderByChangeTimeDesc().orElse(initATMState());
        // collect the number of currency notes replenished, grouping by denomination
        Map<Denomination, Long> denominationListMap = currencyNotes.stream()
                .collect(Collectors.groupingBy(CurrencyNote::getDenomination, counting()));

        atmState.setStackOfFive(calcNewStackOfNotes(denominationListMap, atmState, Denomination.FIVE));
        atmState.setStackOfTen(calcNewStackOfNotes(denominationListMap, atmState, Denomination.TEN));
        atmState.setStackOfTwenty(calcNewStackOfNotes(denominationListMap, atmState, Denomination.TWENTY));
        atmState.setStackOfFifty(calcNewStackOfNotes(denominationListMap, atmState, Denomination.FIFTY));
        atmState.setAtmBalance(new BigDecimal(calcNewATMBalance(atmState)));
        atmState.setChangeTime(LocalDateTime.now());

        return atmState;
    }

    @Override
    public String checkAccountBalance(String accountNumber) {
        BigDecimal accountBalance = accountService.checkBalance(accountNumber);
        if (accountBalance != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            return decimalFormat.format(accountBalance);
        }
        return "NOT_FOUND";
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public Map<Denomination, Long> withrawAccountBalance(String accountNumber, BigDecimal amountBigDecimal) {
        long amount = amountBigDecimal.longValue();
        Optional<ATMState> atmStateOptional = atmRepository.findOptionalFirstByOrderByChangeTimeDesc();
        BigDecimal accountBalance = accountService.checkBalance(accountNumber);

        Map<Denomination, Long> notesDispensed = new HashMap<>();
        if (isAmountInRange(amount) && isAmountAvailable(amount, atmStateOptional, accountBalance)) {
            AccountWithdrawTO accountWithdrawTO = accountService.withdrawAmount(accountNumber, amountBigDecimal);
            if (accountWithdrawTO.isSuccess()) {
                if (atmStateOptional.get().getStackByDenomination(Denomination.FIVE) > 0) {
                    amount = disburseAtleastOne5Note(notesDispensed, amount);
                }
                for (Long noteValue : sortDenominationsMaxFirst()) {
                    if (amount >= noteValue) {
                        long numberOfNotes = amount / noteValue;
                        Denomination relevantDenomination = Denomination.findByValue(noteValue);
                        // if required number of currency notes available in stack
                        if (atmStateOptional.get().getStackByDenomination(relevantDenomination) >= numberOfNotes) {
                            amount = amount - (numberOfNotes * noteValue);
                            notesDispensed.put(relevantDenomination, numberOfNotes);
                        }
                    }
                }
            }
        }
        if (amount != 0) {
            logger.debug("Exception ocured; Amount={}, notesDispensed={}", amount, notesDispensed.keySet());
            throw new ATMWithdrawException("Could not process request; ATM out of cash/notes or Amount not in 20-250 range");
        }
        return notesDispensed;
    }

    private List<Long> sortDenominationsMaxFirst() {
        return Arrays.stream(Denomination.values())
                .map(Denomination::getValue)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    private long calcNewATMBalance(ATMState atmCurrentState) {
        return atmCurrentState.getStackOfFive() * 5L
                + atmCurrentState.getStackOfTen() * 10L
                + atmCurrentState.getStackOfTwenty() * 20L
                + atmCurrentState.getStackOfFifty() * 50L;
    }

    private long calcNewStackOfNotes(Map<Denomination, Long> denominationListMap, ATMState atmState, Denomination denomination) {
        long nrOfNotesReplenished = denominationListMap.getOrDefault(denomination, 0L);
        return atmState.getStackByDenomination(denomination) + nrOfNotesReplenished;
    }

    private boolean isAmountInRange(long amount) {
        return amount >= 20 && amount <= 250;
    }

    private boolean isAmountAvailable(long amount, Optional<ATMState> atmStateOptional, BigDecimal accountBalance) {
        return atmStateOptional.isPresent() && atmStateOptional.get().getAtmBalance().longValue() > amount
                && accountBalance != null && accountBalance.longValue() > amount;
    }

    private long disburseAtleastOne5Note(Map<Denomination, Long> notesDispensed, long amount) {
        long noOfFives = amount / 5L;

        if (noOfFives % 2 == 0) {
            // disburse two 5 notes
            amount = amount - 10L;
            notesDispensed.put(Denomination.FIVE, 2L);
        } else {
            amount = amount - 5L;
            notesDispensed.put(Denomination.FIVE, 1L);
        }
        return amount;
    }

    private ATMState initATMState() {
        return new ATMState(null, BigDecimal.ZERO, 0, 0, 0, 0, LocalDateTime.now());
    }
}