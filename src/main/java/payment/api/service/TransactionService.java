package payment.api.service;

import payment.api.dto.TransactionRequestDto;
import payment.api.entity.Transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import payment.api.repository.TransactionRepository;
import payment.api.strategy.RateCalculatorStrategy;

import java.math.BigDecimal;
import java.util.List;
@Slf4j
@Service
public class TransactionService {


   private final  List<RateCalculatorStrategy> rateCalculatorStrategyList;
   private final TransactionRepository transactionRepository;

    public TransactionService(List<RateCalculatorStrategy> rateCalculatorStrategyList, TransactionRepository transactionRepository) {
        this.rateCalculatorStrategyList = rateCalculatorStrategyList;
        this.transactionRepository = transactionRepository;
    }


    public Transaction processing (Transaction transaction){
        log.info("Starting rate processing for type: {}", transaction.getTransactionType());

        RateCalculatorStrategy strategy = rateCalculatorStrategyList.stream()
                .filter(s-> transaction.getTransactionType().equals(s.getTransactionType()))
                .findFirst()
                .orElseThrow(()-> {
                    log.error("Failed to find strategy for type: {}", transaction.getTransactionType());
                    return new RuntimeException("Strategy not found");
                });


        BigDecimal netValue = strategy.calculate(transaction.getGrossValue());

        transaction.setNetValue(netValue);
        transaction.setAppliedRate(transaction.getGrossValue().subtract(netValue));

        log.info("Calculation completed. Net Value: R$ {}", netValue);
        return transactionRepository.save(transaction);
    }

     @Async("batchExecutor")
     public void processingBatch(List<TransactionRequestDto> transactionList){
        log.info("Batch processing started. Total items: {}",transactionList.size());

         transactionList.stream().forEach(transaction -> {
             try {
                 Transaction t = new Transaction();
                 t.setGrossValue(transaction.grossValue());
                 t.setTransactionType(transaction.type());

                 this.processing(t);
             } catch (Exception e) {

                 log.error("Critical error processing batch item. Type: {} | Error: {}",
                         transaction.type(), e.getMessage());
             }
         });

         log.info("Asynchronous batch processing completed.");


     }

}
