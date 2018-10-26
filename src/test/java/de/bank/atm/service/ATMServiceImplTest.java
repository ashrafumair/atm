package de.bank.atm.service;

import de.bank.atm.TestData;
import de.bank.atm.domain.AccountWithdrawTO;
import de.bank.atm.domain.CurrencyNote;
import de.bank.atm.domain.Denomination;
import de.bank.atm.entity.ATMState;
import de.bank.atm.exception.ATMWithdrawException;
import de.bank.atm.repository.ATMStateRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static de.bank.atm.domain.Denomination.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ATMServiceImplTest {

    private ATMService atmService;

    @Mock
    private ATMStateRepository repository;
    @Mock
    private AccountService accountService;

    @Before
    public void setUp() {
        atmService = new ATMServiceImpl(repository, accountService);
    }

    @Test
    public void replenishTenPoundNoteOnNullState() {
        List<CurrencyNote> replenishNote = Collections.singletonList(new CurrencyNote(Denomination.TEN));
        ATMState expectedAtmStatus = TestData.atmState(10, 0, 0, 1, 0);
        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.empty());

        ATMState newAtmState = atmService.replenishATM(replenishNote);
        assertThat(newAtmState.getStackOfFifty()).isEqualTo(expectedAtmStatus.getStackOfFifty());
        assertThat(newAtmState.getStackOfFive()).isEqualTo(expectedAtmStatus.getStackOfFive());
        assertThat(newAtmState.getAtmBalance()).isEqualTo(expectedAtmStatus.getAtmBalance());
        verify(repository, times(1)).findOptionalFirstByOrderByChangeTimeDesc();
        verifyNoMoreInteractions(repository);
    }


    @Test
    public void replenishNoNote() {
        ATMState atmState = TestData.atmState(200, 2, 2, 4, 4);
        List<CurrencyNote> replenishNote = Collections.emptyList();
        ATMState expectedAtmStatus = TestData.atmState(200, 2, 4, 2, 4);
        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));

        ATMState newAtmState = atmService.replenishATM(replenishNote);
        assertThat(newAtmState.getStackOfFifty()).isEqualTo(expectedAtmStatus.getStackOfFifty());
        assertThat(newAtmState.getStackOfFive()).isEqualTo(expectedAtmStatus.getStackOfFive());
        assertThat(newAtmState.getAtmBalance()).isEqualTo(expectedAtmStatus.getAtmBalance());
        verify(repository, times(1)).findOptionalFirstByOrderByChangeTimeDesc();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void replenishOne5PoundNote() {
        ATMState atmState = TestData.atmState(200, 2, 2, 4, 4);
        List<CurrencyNote> replenishNote = Collections.singletonList(new CurrencyNote(FIVE));
        ATMState expectedAtmStatus = TestData.atmState(205, 2, 4, 2, 5);
        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));

        ATMState newAtmState = atmService.replenishATM(replenishNote);
        assertThat(newAtmState.getStackOfFifty()).isEqualTo(expectedAtmStatus.getStackOfFifty());
        assertThat(newAtmState.getStackOfFive()).isEqualTo(expectedAtmStatus.getStackOfFive());
        assertThat(newAtmState.getAtmBalance()).isEqualTo(expectedAtmStatus.getAtmBalance());
        verify(repository, times(1)).findOptionalFirstByOrderByChangeTimeDesc();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void replenishThree20PoundNote() {
        ATMState atmState = TestData.atmState(200, 2, 2, 4, 4);
        List<CurrencyNote> replenishNote = Arrays
                .asList(Denomination.TWENTY, Denomination.TWENTY, Denomination.TWENTY)
                .stream()
                .map(CurrencyNote::new)
                .collect(Collectors.toList());
        ATMState expectedAtmStatus = TestData.atmState(260, 2, 5, 4, 4);
        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));

        ATMState newAtmState = atmService.replenishATM(replenishNote);
        assertThat(newAtmState.getStackOfFifty()).isEqualTo(expectedAtmStatus.getStackOfFifty());
        assertThat(newAtmState.getStackOfFive()).isEqualTo(expectedAtmStatus.getStackOfFive());
        assertThat(newAtmState.getAtmBalance()).isEqualTo(expectedAtmStatus.getAtmBalance());
        verify(repository, times(1)).findOptionalFirstByOrderByChangeTimeDesc();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void replenishOne5two10one50PoundNotes() {
        ATMState atmState = TestData.atmState(200, 2, 2, 4, 4);
        List<CurrencyNote> replenishNote = Arrays
                .asList(FIVE, Denomination.TEN, Denomination.TEN, Denomination.FIFTY)
                .stream()
                .map(CurrencyNote::new)
                .collect(Collectors.toList());
        ATMState expectedAtmStatus = TestData.atmState(275, 3, 4, 4, 5);
        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));

        ATMState newAtmState = atmService.replenishATM(replenishNote);
        assertThat(newAtmState.getStackOfFifty()).isEqualTo(expectedAtmStatus.getStackOfFifty());
        assertThat(newAtmState.getStackOfFive()).isEqualTo(expectedAtmStatus.getStackOfFive());
        assertThat(newAtmState.getAtmBalance()).isEqualTo(expectedAtmStatus.getAtmBalance());
        verify(repository, times(1)).findOptionalFirstByOrderByChangeTimeDesc();
        verifyNoMoreInteractions(repository);
    }


    @Test
    public void checkBalanceFormatThousandSeparator() {
        final String accountNumber = "01001";
        final String expectedString = "2,738.59";
        when(accountService.checkBalance(accountNumber)).thenReturn(new BigDecimal("2738.59"));

        String balanceFormatStr = atmService.checkAccountBalance(accountNumber);
        assertThat(balanceFormatStr).isNotNull();
        assertThat(balanceFormatStr).isEqualTo(expectedString);
        assertThat(balanceFormatStr).isNotEqualTo("2738.59");
        verify(accountService, times(1)).checkBalance(accountNumber);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void checkBalanceFormatString() {
        final String accountNumber = "01002";
        final String expectedBalance = "23.00";
        when(accountService.checkBalance(accountNumber)).thenReturn(new BigDecimal("23"));

        String balanceFormatStr = atmService.checkAccountBalance(accountNumber);
        assertThat(balanceFormatStr).isNotNull();
        assertThat(balanceFormatStr).isEqualTo(expectedBalance);
        verify(accountService, times(1)).checkBalance(accountNumber);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void checkZeroBalanceFormatString() {
        final String accountNumber = "01003";
        final String expectedBalance = "0.00";
        when(accountService.checkBalance(accountNumber)).thenReturn(new BigDecimal("0"));

        String balanceFormatStr = atmService.checkAccountBalance(accountNumber);
        assertThat(balanceFormatStr).isEqualTo(expectedBalance);
        verify(accountService, times(1)).checkBalance(accountNumber);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void checkLargeBalanceFormatString() {
        final String accountNumber = "010041";
        final String expectedBalance = "123,456,789.90";
        when(accountService.checkBalance(accountNumber)).thenReturn(new BigDecimal("123456789.90"));

        String balanceFormatStr = atmService.checkAccountBalance(accountNumber);
        assertThat(balanceFormatStr).isEqualTo(expectedBalance);
        verify(accountService, times(1)).checkBalance(accountNumber);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void checkNullBalanceGivenAccountNotFound() {
        final String expectedBalance = "NOT_FOUND";
        when(accountService.checkBalance(anyString())).thenReturn(null);

        String balanceFormatStr = atmService.checkAccountBalance(anyString());
        assertThat(balanceFormatStr).isEqualTo(expectedBalance);
        verify(accountService, times(1)).checkBalance(anyString());
        verifyNoMoreInteractions(accountService);
    }

    @Test
    public void withdraw100FromAccountTest() {
        ATMState atmState = TestData.atmState(200, 2, 2, 4, 4);
        BigDecimal accountBalance = new BigDecimal("150");
        BigDecimal withdrawAmount = new BigDecimal("100");
        AccountWithdrawTO accountWithdrawTO = TestData.accountWithdrawTO(true, new BigDecimal("50"));

        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));
        when(accountService.checkBalance(anyString())).thenReturn(accountBalance);
        when(accountService.withdrawAmount(anyString(), any())).thenReturn(accountWithdrawTO);

        Map<Denomination, Long> disbursedNotes = atmService.withrawAccountBalance("00100", withdrawAmount);
        assertThat(disbursedNotes.keySet()).containsOnly(FIVE, TWENTY, FIFTY);
        assertThat(disbursedNotes.get(FIVE)).isEqualTo(2L);
        assertThat(disbursedNotes.get(TWENTY)).isEqualTo(2L);
        assertThat(disbursedNotes.get(FIFTY)).isEqualTo(1L);

        verify(repository, times(1)).findOptionalFirstByOrderByChangeTimeDesc();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void withdraw100FromAccount5NoteNotAvailableTest() {
        ATMState atmState = TestData.atmState(200, 2, 3, 4, 0);
        BigDecimal accountBalance = new BigDecimal("150");
        BigDecimal withdrawAmount = new BigDecimal("100");
        AccountWithdrawTO accountWithdrawTO = TestData.accountWithdrawTO(true, new BigDecimal("50"));

        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));
        when(accountService.checkBalance(anyString())).thenReturn(accountBalance);
        when(accountService.withdrawAmount(anyString(), any())).thenReturn(accountWithdrawTO);

        Map<Denomination, Long> disbursedNotes = atmService.withrawAccountBalance("00100", withdrawAmount);
        assertThat(disbursedNotes.keySet()).containsOnly(FIFTY);
        assertThat(disbursedNotes.get(FIFTY)).isEqualTo(2L);

        verify(repository, times(1)).findOptionalFirstByOrderByChangeTimeDesc();
        verifyNoMoreInteractions(repository);
    }


    @Test
    public void withdraw70FromAccountTest() {
        ATMState atmState = TestData.atmState(200, 2, 2, 4, 4);
        BigDecimal accountBalance = new BigDecimal("150");
        BigDecimal withdrawAmount = new BigDecimal("70");
        AccountWithdrawTO accountWithdrawTO = TestData.accountWithdrawTO(true, new BigDecimal("80"));

        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));
        when(accountService.checkBalance(anyString())).thenReturn(accountBalance);
        when(accountService.withdrawAmount(anyString(), any())).thenReturn(accountWithdrawTO);

        Map<Denomination, Long> disbursedNotes = atmService.withrawAccountBalance("00100", withdrawAmount);
        assertThat(disbursedNotes.keySet()).containsOnly(FIVE, TEN, FIFTY);
        assertThat(disbursedNotes.get(FIVE)).isEqualTo(2L);
        assertThat(disbursedNotes.get(TEN)).isEqualTo(1L);
        assertThat(disbursedNotes.get(FIFTY)).isEqualTo(1L);

        verify(repository, times(1)).findOptionalFirstByOrderByChangeTimeDesc();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void withdraw70FromAccount50NotAvailableInAtmTest() {
        ATMState atmState = TestData.atmState(200, 0, 5, 9, 2);
        BigDecimal accountBalance = new BigDecimal("150");
        BigDecimal withdrawAmount = new BigDecimal("70");
        AccountWithdrawTO accountWithdrawTO = TestData.accountWithdrawTO(true, new BigDecimal("80"));

        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));
        when(accountService.checkBalance(anyString())).thenReturn(accountBalance);
        when(accountService.withdrawAmount(anyString(), any())).thenReturn(accountWithdrawTO);

        Map<Denomination, Long> disbursedNotes = atmService.withrawAccountBalance("00100", withdrawAmount);
        assertThat(disbursedNotes.keySet()).containsOnly(FIVE, TWENTY);
        assertThat(disbursedNotes.get(FIVE)).isEqualTo(2L);
        assertThat(disbursedNotes.get(TWENTY)).isEqualTo(3L);

        verify(repository, times(1)).findOptionalFirstByOrderByChangeTimeDesc();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void withdraw75FromAccountTest() {
        ATMState atmState = TestData.atmState(200, 2, 2, 4, 4);
        BigDecimal accountBalance = new BigDecimal("150");
        BigDecimal withdrawAmount = new BigDecimal("75");
        AccountWithdrawTO accountWithdrawTO = TestData.accountWithdrawTO(true, new BigDecimal("75"));

        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));
        when(accountService.checkBalance(anyString())).thenReturn(accountBalance);
        when(accountService.withdrawAmount(anyString(), any())).thenReturn(accountWithdrawTO);

        Map<Denomination, Long> disbursedNotes = atmService.withrawAccountBalance("00100", withdrawAmount);
        assertThat(disbursedNotes.keySet()).containsOnly(FIVE, TWENTY, FIFTY);
        assertThat(disbursedNotes.get(FIVE)).isEqualTo(1L);
        assertThat(disbursedNotes.get(TWENTY)).isEqualTo(1L);
        assertThat(disbursedNotes.get(FIFTY)).isEqualTo(1L);

        verify(repository, times(1)).findOptionalFirstByOrderByChangeTimeDesc();
        verifyNoMoreInteractions(repository);
    }

    @Test(expected = ATMWithdrawException.class)
    public void withdraw75FromAccount5NotAvailableInATMExceptionTest() {
        ATMState atmState = TestData.atmState(200, 2, 3, 4, 0);
        BigDecimal accountBalance = new BigDecimal("150");
        BigDecimal withdrawAmount = new BigDecimal("75");
        AccountWithdrawTO accountWithdrawTO = TestData.accountWithdrawTO(true, new BigDecimal("75"));

        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));
        when(accountService.checkBalance(anyString())).thenReturn(accountBalance);
        when(accountService.withdrawAmount(anyString(), any())).thenReturn(accountWithdrawTO);

        atmService.withrawAccountBalance("00100", withdrawAmount);
    }

    @Test(expected = ATMWithdrawException.class)
    public void withdrawLessThan20FromAccountExceptionTest() {
        ATMState atmState = TestData.atmState(200, 2, 3, 4, 0);
        BigDecimal accountBalance = new BigDecimal("150");
        BigDecimal withdrawAmount = new BigDecimal("15");

        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));
        when(accountService.checkBalance(anyString())).thenReturn(accountBalance);
        atmService.withrawAccountBalance("00100", withdrawAmount);
    }

    @Test(expected = ATMWithdrawException.class)
    public void withdrawMoreThan250FromAccountExceptionTest() {
        ATMState atmState = TestData.atmState(300, 4, 3, 4, 0);
        BigDecimal accountBalance = new BigDecimal("300");
        BigDecimal withdrawAmount = new BigDecimal("255");

        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));
        when(accountService.checkBalance(anyString())).thenReturn(accountBalance);
        atmService.withrawAccountBalance("00100", withdrawAmount);
    }

    @Test(expected = ATMWithdrawException.class)
    public void withdrawMoreThanBalanceFromAccountExceptionTest() {
        ATMState atmState = TestData.atmState(400, 6, 3, 4, 0);
        BigDecimal accountBalance = new BigDecimal("200");
        BigDecimal withdrawAmount = new BigDecimal("250");

        when(repository.findOptionalFirstByOrderByChangeTimeDesc()).thenReturn(Optional.of(atmState));
        when(accountService.checkBalance(anyString())).thenReturn(accountBalance);
        atmService.withrawAccountBalance("00100", withdrawAmount);
    }

}
