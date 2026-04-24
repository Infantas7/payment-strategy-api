package payment.api.strategy;

import payment.api.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BilletRateStrategy implements RateCalculatorStrategy{


    @Override
    public BigDecimal calculate (BigDecimal value){
        return value.subtract(new BigDecimal("2.5"));
    }

    @Override
    public TransactionType getTransactionType(){
        return TransactionType.BILLET;
    }

}
