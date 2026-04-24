package payment.api.controller;


import payment.api.dto.TransactionRequestDto;
import payment.api.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payment.api.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @PostMapping
    public ResponseEntity <Transaction> processTransaction(@RequestBody TransactionRequestDto transactionRequestDto){

        Transaction transaction = new Transaction();
        transaction.setGrossValue(transactionRequestDto.grossValue());
        transaction.setTransactionType(transactionRequestDto.type());

        Transaction response = transactionService.processing(transaction);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/batch")
    public ResponseEntity<String> processBatchTransaction(@RequestBody List<TransactionRequestDto> transactionRequestDtoList){

        transactionService.processingBatch(transactionRequestDtoList);

        return ResponseEntity.accepted().body("Batch processing initiated in parallel.");

    }


}
