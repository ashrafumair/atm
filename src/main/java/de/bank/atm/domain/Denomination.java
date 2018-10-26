package de.bank.atm.domain;

public enum Denomination {
    FIVE(5), TEN(10), TWENTY(20), FIFTY(50);

    private long value;
    Denomination(long val) {
        this.value = val;
    }

    public long getValue() {
        return this.value;
    }

    public static Denomination findByValue(long value) {
        for (Denomination denomination : values()) {
            if (denomination.getValue() == value) {
                return denomination;
            }
        }
        return null;
    }

}
