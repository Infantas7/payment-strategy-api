package strategy;

import enums.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreditRateStrategy implements RateCalculatorStrategy{




    @Override
    public BigDecimal calculate (BigDecimal value){
        BigDecimal rate = value.multiply(new BigDecimal("0,05"));
        return value.subtract(rate) ;
    }

    @Override
    public TransactionType getTransactionType(){
        return TransactionType.CREDIT;
    }
}
