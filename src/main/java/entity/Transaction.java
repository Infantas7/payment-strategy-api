package entity;


import enums.TransactionType;
import lombok.Data;


import java.math.BigDecimal;
import java.util.Date;

@Data
public class Transaction {

    private Long id;
    private BigDecimal grossValue;
    private BigDecimal netValue;
    private BigDecimal appliedRate;
    private TransactionType transactionType;
    private Date dateCreate;


}
