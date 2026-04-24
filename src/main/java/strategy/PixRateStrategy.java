package strategy;

import enums.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PixRateStrategy implements RateCalculatorStrategy {


    @Override
   public BigDecimal calculate (BigDecimal value){
        return  value;
    }

    @Override
    public TransactionType getTransactionType(){
          return TransactionType.PIX;
    }

}
