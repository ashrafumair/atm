package de.bank.atm.entity;

import de.bank.atm.domain.Denomination;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
public class ATMState {

    public ATMState(Long id, BigDecimal atmBalance, long stackOfFive, long stackOfTen, long stackOfTwenty, long stackOfFifty, LocalDateTime changeTime) {
        this.id = id;
        this.atmBalance = atmBalance;
        this.stackOfFive = stackOfFive;
        this.stackOfTen = stackOfTen;
        this.stackOfTwenty = stackOfTwenty;
        this.stackOfFifty = stackOfFifty;
        this.changeTime = changeTime;
    }

    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal atmBalance;
    private long stackOfFive;
    private long stackOfTen;
    private long stackOfTwenty;
    private long stackOfFifty;
    private LocalDateTime changeTime;

    public long getStackByDenomination(Denomination denomination) {
        switch (denomination) {
            case FIVE:
                return stackOfFive;
            case TEN:
                return stackOfTen;
            case TWENTY:
                return stackOfTwenty;
            case FIFTY:
                return stackOfFifty;
            default:
                return 0;
        }
    }
}
