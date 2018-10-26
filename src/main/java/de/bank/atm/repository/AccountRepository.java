package de.bank.atm.repository;

import de.bank.atm.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findOptionalByAccountNumber(String accountNumber);
}
