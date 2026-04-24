package payment.api.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import payment.api.enums.TransactionType;
import lombok.Data;


import java.math.BigDecimal;
import java.util.Date;
@Entity
@Data
public class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal grossValue;
    private BigDecimal netValue;
    private BigDecimal appliedRate;
    private TransactionType transactionType;
    private Date dateCreate;


}
