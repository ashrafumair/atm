package de.bank.atm.domain;

import lombok.Data;

@Data
public class CurrencyNote {
    public CurrencyNote(Denomination denomination) {
        this.denomination = denomination;
    }

    private Denomination denomination;
}
