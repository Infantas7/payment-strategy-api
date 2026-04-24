package dto;

import enums.TransactionType;

import java.math.BigDecimal;

public record TransactionRequestDto (

        BigDecimal grossValue,
        TransactionType type

){

}



