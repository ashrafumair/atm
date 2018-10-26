package de.bank.atm.service;

import de.bank.atm.TestData;
import de.bank.atm.domain.AccountWithdrawTO;
import de.bank.atm.repository.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest {

    private AccountService accountService;

    @Mock
    private AccountRepository repository;

    @Before
    public void setUp() {
        accountService = new AccountServiceImpl(repository);
    }

    @Test
    public void checkBalanceAccountOneTest() {
        // TODO: See why the annotation @Mock worked, but method mock() didnot
        //AccountRepository repository = mock(AccountRepository.class);
        final String accountNumber = "01001";
        BigDecimal expectedAmount = new BigDecimal("2738.59");
        when(repository.findOptionalByAccountNumber(accountNumber)).thenReturn(TestData.account1());
        BigDecimal amount = accountService.checkBalance(accountNumber);
        assertThat(amount).isNotNull();
        assertThat(amount).isEqualTo(expectedAmount);
        verify(repository).findOptionalByAccountNumber(accountNumber); // Double check, used only for mocks
    }

    @Test
    public void checkBalanceAccountTwoTest() {
        final String accountNumber = "01002";
        BigDecimal expectedAmount = new BigDecimal("23.00");
        when(repository.findOptionalByAccountNumber(accountNumber)).thenReturn(TestData.account2());
        BigDecimal amount = accountService.checkBalance(accountNumber);
        assertThat(amount).isNotNull();
        assertThat(amount).isEqualTo(expectedAmount);
        verify(repository).findOptionalByAccountNumber(accountNumber);
    }

    @Test
    public void checkBalanceAccountThreeTest() {
        final String accountNumber = "01003";
        BigDecimal expectedAmount = new BigDecimal("0.00");
        when(repository.findOptionalByAccountNumber(accountNumber)).thenReturn(TestData.account3());
        BigDecimal amount = accountService.checkBalance(accountNumber);
        assertThat(amount).isNotNull();
        assertThat(amount).isEqualTo(expectedAmount);
        verify(repository).findOptionalByAccountNumber(accountNumber);
    }

    @Test
    public void checkBalanceNoDataFound() {
        final String accountNumber = "123456";
        when(repository.findOptionalByAccountNumber(accountNumber)).thenReturn(Optional.empty());
        BigDecimal amount = accountService.checkBalance(accountNumber);
        assertThat(amount).isNull();
        verify(repository).findOptionalByAccountNumber(accountNumber);
    }

    @Test
    public void withdraw1000FromAccount1Test() {
        final String accountNumber = "01001";
        BigDecimal expectedNewBalance = new BigDecimal("1738.59");
        when(repository.findOptionalByAccountNumber(accountNumber)).thenReturn(TestData.account1());

        AccountWithdrawTO accountWithdrawTO = accountService.withdrawAmount(accountNumber, new BigDecimal("1000"));
        assertThat(accountWithdrawTO).isNotNull();
        assertThat(accountWithdrawTO.isSuccess()).isTrue();
        assertThat(accountWithdrawTO.getNewBalance()).isEqualTo(expectedNewBalance);
        verify(repository).findOptionalByAccountNumber(accountNumber);

    }

    @Test
    public void withdraw23FromAccount2Test() {
        final String accountNumber = "01002";
        BigDecimal expectedNewBalance = new BigDecimal("0.00");
        when(repository.findOptionalByAccountNumber(accountNumber)).thenReturn(TestData.account2());

        AccountWithdrawTO accountWithdrawTO = accountService.withdrawAmount(accountNumber, new BigDecimal("23.00"));
        assertThat(accountWithdrawTO).isNotNull();
        assertThat(accountWithdrawTO.isSuccess()).isTrue();
        assertThat(accountWithdrawTO.getNewBalance()).isEqualTo(expectedNewBalance);
        verify(repository).findOptionalByAccountNumber(accountNumber);
    }

    @Test
    public void cannotWithdrawMoreThan23FromAccount2Test() {
        final String accountNumber = "01002";
        BigDecimal expectedNewBalance = new BigDecimal("23.00");
        when(repository.findOptionalByAccountNumber(accountNumber)).thenReturn(TestData.account2());

        AccountWithdrawTO accountWithdrawTO = accountService.withdrawAmount(accountNumber, new BigDecimal("23.01"));
        assertThat(accountWithdrawTO).isNotNull();
        assertThat(accountWithdrawTO.isSuccess()).isFalse();
        assertThat(accountWithdrawTO.getNewBalance()).isEqualTo(expectedNewBalance);
        verify(repository).findOptionalByAccountNumber(accountNumber);
    }

    @Test
    public void cannotWithdraw10FromAccount3Test() {
        final String accountNumber = "01003";
        BigDecimal expectedNewBalance = new BigDecimal("0.00");
        when(repository.findOptionalByAccountNumber(accountNumber)).thenReturn(TestData.account3());

        AccountWithdrawTO accountWithdrawTO = accountService.withdrawAmount(accountNumber, new BigDecimal("10.00"));
        assertThat(accountWithdrawTO).isNotNull();
        assertThat(accountWithdrawTO.isSuccess()).isFalse();
        assertThat(accountWithdrawTO.getNewBalance()).isEqualTo(expectedNewBalance);
        verify(repository).findOptionalByAccountNumber(accountNumber);
    }

}
