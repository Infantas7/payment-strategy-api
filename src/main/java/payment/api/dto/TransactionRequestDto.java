package payment.api.dto;

import payment.api.enums.TransactionType;

import java.math.BigDecimal;

public record TransactionRequestDto (

        BigDecimal grossValue,
        TransactionType type

){

}



