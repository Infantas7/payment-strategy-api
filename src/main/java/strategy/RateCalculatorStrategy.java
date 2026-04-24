package strategy;

import enums.TransactionType;

import java.math.BigDecimal;

public interface RateCalculatorStrategy {

    BigDecimal calculate (BigDecimal value);

    TransactionType getTransactionType();


}
