package TransactionTest;

import dto.TransactionRequestDto;
import entity.Transaction;
import enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.TransactionRepository;
import service.TransactionService;
import strategy.BilletRateStrategy;
import strategy.CreditRateStrategy;
import strategy.PixRateStrategy;
import strategy.RateCalculatorStrategy;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {


    @InjectMocks
    private TransactionService service;

    @Mock
    private TransactionRepository repository;


    @Mock
    private PixRateStrategy pixStrategy;
    @Mock
    private BilletRateStrategy billetStrategy;
    @Mock
    private CreditRateStrategy creditStrategy;

    @BeforeEach
    void setup() {

        List<RateCalculatorStrategy> strategies = Arrays.asList(pixStrategy, billetStrategy, creditStrategy);
        service = new TransactionService(strategies, repository);
    }

    @Test
    @DisplayName("must apply the correct PIX rate.")
    void mustProcessPixSuccessfully() {

        Transaction transaction = new Transaction();
        transaction.setGrossValue(new BigDecimal("100.00"));
        transaction.setTransactionType(TransactionType.PIX);


        when(pixStrategy.getTransactionType()).thenReturn(TransactionType.PIX);
        when(pixStrategy.calculate(any())).thenReturn(new BigDecimal("100.00"));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));


        Transaction resultado = service.processing(transaction);


        assertNotNull(resultado);
        assertEquals(new BigDecimal("100.00"), resultado.getNetValue());
        verify(pixStrategy, times(1)).calculate(any());
        verify(billetStrategy, never()).calculate(any());
    }

    @Test
    @DisplayName("must apply the correct BILLET rate.")
    void mustProcessBilletSuccessfully() {

        Transaction transaction = new Transaction();
        transaction.setGrossValue(new BigDecimal("50.00"));
        transaction.setTransactionType(TransactionType.BILLET);


        when(billetStrategy.getTransactionType()).thenReturn(TransactionType.BILLET);
        when(billetStrategy.calculate(any())).thenReturn(new BigDecimal("47.50"));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));


        Transaction resultado = service.processing(transaction);


        assertEquals(new BigDecimal("47.50"), resultado.getNetValue());
        verify(billetStrategy).calculate(any());
        verify(pixStrategy, never()).calculate(any());
    }

    @Test
    @DisplayName("must apply the correct CREDIT rate.")
    void mustProcessCreditSuccessfully() {
        Transaction transaction = new Transaction();
        transaction.setGrossValue(new BigDecimal("200.00"));
        transaction.setTransactionType(TransactionType.CREDIT);


        when(creditStrategy.getTransactionType()).thenReturn(TransactionType.CREDIT);
        when(creditStrategy.calculate(any())).thenReturn(new BigDecimal("190.00"));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));


        Transaction resultado = service.processing(transaction);


        assertEquals(new BigDecimal("190.00"), resultado.getNetValue());
        verify(creditStrategy).calculate(any());
        verify(pixStrategy, never()).calculate(any());
        verify(billetStrategy, never()).calculate(any());
    }


    @Test
    @DisplayName("It should return a failure when the strategy is not found")
    void shouldReturnFailureSadPath() {

        Transaction transaction = new Transaction();
        transaction.setGrossValue(new BigDecimal("100.00"));
        transaction.setTransactionType(TransactionType.CREDIT);


        List<RateCalculatorStrategy> incompleteStrategies = Arrays.asList(pixStrategy, billetStrategy);
        TransactionService serviceNoCredit = new TransactionService(incompleteStrategies, repository);


        when(pixStrategy.getTransactionType()).thenReturn(TransactionType.PIX);
        when(billetStrategy.getTransactionType()).thenReturn(TransactionType.BILLET);


        assertThrows(RuntimeException.class, () -> serviceNoCredit.processing(transaction));


        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Must successfully process batch of transactions.")
    void mustProcessBatchSuccessfully() {

        TransactionRequestDto req1 = new TransactionRequestDto(new BigDecimal("100.00"), TransactionType.PIX);
        TransactionRequestDto req2 = new TransactionRequestDto(new BigDecimal("50.00"), TransactionType.BILLET);
        List<TransactionRequestDto> lote = Arrays.asList(req1, req2);


        when(pixStrategy.getTransactionType()).thenReturn(TransactionType.PIX);
        when(pixStrategy.calculate(any())).thenReturn(new BigDecimal("100.00")); // <--- ADICIONE ISSO

        when(billetStrategy.getTransactionType()).thenReturn(TransactionType.BILLET);
        when(billetStrategy.calculate(any())).thenReturn(new BigDecimal("47.50"));


        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));


        service.processingBatch(lote);


        verify(repository, times(2)).save(any());
        verify(pixStrategy, times(1)).calculate(any());
        verify(billetStrategy, times(1)).calculate(any());
    }

    @Test
    @DisplayName("You should continue processing the batch even if an item fails due to a lack of strategy.")
    void mustContinueProcessingLotEvenWithErrorInAnItem() {

        TransactionRequestDto itemValido = new TransactionRequestDto(new BigDecimal("100.00"), TransactionType.PIX);
        TransactionRequestDto itemInvalido = new TransactionRequestDto(new BigDecimal("50.00"), TransactionType.CREDIT);
        List<TransactionRequestDto> lote = Arrays.asList(itemValido, itemInvalido);


        lenient().when(pixStrategy.getTransactionType()).thenReturn(TransactionType.PIX);
        lenient().when(pixStrategy.calculate(any())).thenReturn(new BigDecimal("100.00"));


        lenient().when(billetStrategy.getTransactionType()).thenReturn(TransactionType.BILLET);


        lenient().when(creditStrategy.getTransactionType()).thenReturn(null);

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));


        service.processingBatch(lote);


        verify(repository, times(1)).save(any());

        verify(pixStrategy, times(1)).calculate(any());


        verify(creditStrategy, never()).calculate(any());
    }

}
