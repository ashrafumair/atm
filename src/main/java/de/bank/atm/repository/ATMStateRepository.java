package de.bank.atm.repository;

import de.bank.atm.entity.ATMState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ATMStateRepository extends JpaRepository<ATMState, Long> {

    Optional<ATMState> findOptionalFirstByOrderByChangeTimeDesc();
}
