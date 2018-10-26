package de.bank.atm.exception;

public class ATMWithdrawException extends RuntimeException {

    public ATMWithdrawException(String message) {
        super(message);
    }
}
