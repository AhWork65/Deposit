package com.heydari.deposit;

import com.heydari.deposit.exception.DepositInternalException;
import com.heydari.deposit.model.customModel.DepositOperationsTransfer;
import com.heydari.deposit.model.customer.Customer;
import com.heydari.deposit.model.deposit.Deposit;
import com.heydari.deposit.model.deposit.DepositCurrency;
import com.heydari.deposit.model.deposit.DepositStatus;
import com.heydari.deposit.model.deposit.DepositType;
import com.heydari.deposit.model.transaction.Transaction;
import com.heydari.deposit.model.transaction.TransactionStatus;
import com.heydari.deposit.model.transaction.TransactionsType;
import com.heydari.deposit.repository.DepositRepository;
import com.heydari.deposit.service.DepositService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class DepositRemainingServiceTest {
    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private DepositService depositService;

    @Mock
    private WebClient webClientMock;


    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @Mock
    private DepositService depositServiceMock;

    //===========================================================================
    @Test
    public void GetBalance_Test_Happy_Scenario() throws DepositInternalException {
        Deposit deposit = new  Deposit();
        deposit.setId(10L);
        deposit.setBalance(new BigDecimal(10000));

        when(depositRepository.findById(Matchers.any())).thenAnswer(t -> {
            Optional<Deposit> loanOptional = Optional.of(deposit);
            return loanOptional;
        });
        BigDecimal balance = depositService.getBalance(10L);
        assertEquals(deposit.getBalance() , balance);
    }
//===========================================================================
    @Test
    public void getAllDeposit_ByCustomer_Test_Happy_Scenario() throws DepositInternalException {
       Customer customer = new Customer();
       customer.setId(1L);

        List <Deposit> depositList = new ArrayList<>();
        depositList.add(new Deposit());
        depositList.add(new Deposit());

        when(depositRepository.findAllByCustomer(Matchers.any())).thenAnswer(t -> {
           return depositList;
        });

        List<Deposit> retDeposits = depositService.getAllDepositByCustomer(customer);
        assertEquals(2 , retDeposits.size());
    }
    //===========================================================================
    @Test
    public void checkExists_DepositByCustumer_Test_Happy_Scenario() throws DepositInternalException {
        Customer customer = new Customer();
        customer.setId(1L);

        when(depositRepository.existsByCustomer(Matchers.any())).thenAnswer(t -> {
            return true;
        });

        Boolean retValue = depositService.checkExistsDepositByCustumer(customer);
        assertEquals(true, retValue);
    }
    //===========================================================================
    @Test
    public void getCustomerList_ByDeposit_Test_Happy_Scenario()  {

        Deposit deposit = new Deposit();

        List<Customer> customerList =  Arrays.asList(new Customer(),new Customer());
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(Matchers.anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(Matchers.any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(new ParameterizedTypeReference<List<Customer>>() {})).thenReturn(Mono.just(customerList));

        List<Customer> retCustomerList = depositService.getCustomerListByDeposit(deposit);
        assertEquals(2, retCustomerList.size());
    }
    //===========================================================================
    @Test
    public void createTransaction_WebRequest_Test_Happy_Scenario()  {
       Transaction transaction = new Transaction();

        List<Customer> customerList =  Arrays.asList(new Customer(),new Customer());
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(Matchers.anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(Matchers.any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Transaction.class)).thenReturn(Mono.just(transaction));

        Transaction retTransaction = depositService.createTransactionWebRequest(transaction);
        assertEquals(transaction, retTransaction);
    }

    //===========================================================================
    @Test
    public void createTransaction_Test_Happy_Scenario()  {
        Calendar calendar = Calendar.getInstance();
        Deposit deposit = new Deposit(1L,"1","Test", DepositStatus.OPEN, DepositType.DEMAND_DEPOSIT , DepositCurrency.RIAL
                ,new BigDecimal(1000000), calendar.getTime(),null,null);
        DepositOperationsTransfer depositOperationsTransfer = new DepositOperationsTransfer(deposit);
        Transaction transaction = new Transaction();
        transaction.setId(10l);

        doReturn(transaction).when(depositServiceMock).
                createTransactionWebRequest(Matchers.any());

        Transaction retTransaction = depositService.createTransaction(depositOperationsTransfer, TransactionStatus.SUCCESSFUL, TransactionsType.DEPOSIT) ;
        assertEquals(retTransaction.getStatus(), TransactionStatus.SUCCESSFUL);
    }


    //===========================================================================
    @Test
    public void getDeposit_ByNumber_Test_Happy_Scenario() throws DepositInternalException {
        Deposit deposit = new Deposit();
        deposit.setId(1l);
        deposit.setNumber("1");
        when(depositRepository.findByNumber(Matchers.any())).thenReturn(deposit);
        Deposit retValue = depositService.getDepositByNumber("1");
        assertEquals("1", retValue.getNumber());
    }
    }
