package com.heydari.deposit.service;

import com.heydari.deposit.exception.DepositCreateException;
import com.heydari.deposit.exception.DepositInternalException;
import com.heydari.deposit.model.customer.Customer;
import com.heydari.deposit.model.deposit.Deposit;
import com.heydari.deposit.model.deposit.DepositChangeStatus;
import com.heydari.deposit.model.deposit.DepositStatus;
import com.heydari.deposit.model.customModel.DepositOperationBase;
import com.heydari.deposit.model.customModel.DepositOperationsTransfer;
import com.heydari.deposit.model.transaction.Transaction;
import com.heydari.deposit.model.transaction.TransactionStatus;
import com.heydari.deposit.model.transaction.TransactionsType;
import com.heydari.deposit.repository.DepositRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class DepositService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DepositService.class);

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private DepositService depositService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private WebClient webClient;


    @Autowired
    private DepositOperationsTransfer baseOperationsForWithDarw ;

    @Value("${customerUrl}")
    private String customerServiceUrl;

    @Value("${transactionUrl}")
    private String transactionServiceUrl;

//==============================================================================
private boolean validDeposit(Deposit deposit) {

   if (deposit== null)
      return  false;

    if (deposit.getNumber() == null)
        return  false;


 return true;
}
//==============================================================================
    private boolean ValidDepositForChange(Deposit deposit) {

        if (! validDeposit(deposit))
            return  false;

        if (deposit.getId() == null)
            return  false;

        return true;
    }
//======================================================================================
@Transactional(rollbackFor = {Exception.class})
    public Deposit createDeposit (Deposit deposit) throws DepositCreateException {
        LOGGER.debug("createDeposit INPUT PARAMET IS {} ",(deposit == null) ? " null ":deposit.toString());
        if (!validDeposit(deposit)) {
            LOGGER.error("Paramet Not Valid");
            throw new DepositCreateException("Paramet Not Valid");
        }
        if (deposit.getCustomer() == null || deposit.getCustomer().isEmpty()){
            LOGGER.error("Customer cannot be empty");
            throw new DepositCreateException("Customer cannot be empty");
        }

        Deposit retDeposit;

        try {
            retDeposit = depositRepository.save(deposit);
        }catch (Exception e){
            LOGGER.error("createDeposit Error :{}", e.getMessage());
            throw new DepositCreateException(e.getMessage(), e);
        }
        LOGGER.debug("createDeposit OutPut IS {} ",retDeposit.toString());
        return retDeposit;
    }
//======================================================================================
  public List<Deposit> getAllDeposit(){
    List<Deposit> depositList = depositRepository.findAll();
    LOGGER.debug("getAllDeposit depositList count id  {} ",depositList.size());
    return depositList;
  }
//======================================================================================
@Transactional(rollbackFor = {Exception.class})
    public Deposit changeDepositStatus(DepositChangeStatus deposit) throws  DepositInternalException {
         LOGGER.debug("changeDepositStatus INPUT PARAMET IS {} ",(deposit == null) ? " null ":deposit.toString());

        if (deposit == null) {
            LOGGER.error("changeDepositStatus Paramet Not Valid");
            throw new DepositInternalException("Paramet Not Valid");
        }

        Deposit changDeposit;
        changDeposit = getDepositById(deposit.getDepositId());

        if (!ValidDepositForChange(changDeposit)) {
            LOGGER.error("changeDepositStatus Paramet Not Valid");
            throw new DepositInternalException("Paramet Not Valid");
        }

        if (changDeposit.getStatus().equals(DepositStatus.ClOSE)){
                LOGGER.error("changeDepositStatus Depasit is closed Cannot changed");
                throw new DepositInternalException("changeDepositStatus Depasit is closed Cannot changed");
        }

        if (changDeposit.getStatus().equals(deposit.getStatus())){
            LOGGER.debug("changeDepositStatus Not Change Status");
            return changDeposit;
        }


        changDeposit.setStatus(deposit.getStatus());
        depositRepository.save(changDeposit);
        LOGGER.debug("changeDepositStatus chenge Deposit :{]", changDeposit.toString());
        return changDeposit;
    }
//======================================================================================
   public Deposit getDepositById(Long id) throws DepositInternalException {
       LOGGER.debug("getDepositById INPUT PARAMET IS {} ",id.toString());
       Optional<Deposit> depositOptional;
       depositOptional = depositRepository.findById(id);

       depositOptional.orElseThrow(()->  { LOGGER.error("Deposit Not Found");
           return new DepositInternalException("Deposit Not Found");});
       return depositOptional.orElse(null);
   }
//======================================================================================

    private boolean vallidateForDeposit(Deposit deposit) {

        if (deposit.getStatus().equals(DepositStatus.OPEN)  )
            return true ;

        if (deposit.getStatus().equals(DepositStatus.BlOCKED_WITHDRAW))
            return true;

        return  false;
    }
//======================================================================================

@Transactional(rollbackFor = {Exception.class})
  public Deposit deposit (DepositOperationBase depositOperationBase, Boolean byTransfer) throws DepositInternalException {

    LOGGER.error("deposit Input Paramet is :{}" ,(depositOperationBase == null) ? " null ":depositOperationBase.toString() );

    if (depositOperationBase == null ||(depositOperationBase.getSourceDeposit().getId() == null)){
        LOGGER.error("Bad Parametr Deposit Id Is Null");
        throw new DepositInternalException("Bad Parametr");
    }
    Deposit getDeposit =  getDepositById(depositOperationBase.getSourceDeposit().getId());
    DepositOperationsTransfer operations = new DepositOperationsTransfer();
    if (!vallidateForDeposit(getDeposit)){
        if (!byTransfer) {
            operations.setPrice(depositOperationBase.getPrice());
            operations.setSourceDeposit(getDeposit);
            createTransaction(operations, TransactionStatus.UNSUCCESSFUL, TransactionsType.DEPOSIT);
        }
        LOGGER.error("Cannot be deposit to this account...");
        throw new DepositInternalException("Cannot be deposit to this account...");
    }

    getDeposit.setBalance(getDeposit.getBalance().add(depositOperationBase.getPrice()));
    depositRepository.save(getDeposit);
    if (!byTransfer) {
        operations.setPrice(depositOperationBase.getPrice());
        operations.setSourceDeposit(getDeposit);
        createTransaction(operations, TransactionStatus.SUCCESSFUL, TransactionsType.DEPOSIT);
    }
    LOGGER.debug("deposit chenge  :{}", getDeposit.toString());
    return getDeposit;
  }

//======================================================================================
    @Transactional(rollbackFor = {Exception.class})
    public Deposit withdraw (DepositOperationBase depositOperationBase, Boolean byTransfer) throws DepositInternalException {
        LOGGER.error("withdraw Input Paramet is :{}" , (depositOperationBase == null) ? " null ":depositOperationBase.toString() );
        if (depositOperationBase == null  || (depositOperationBase.getSourceDeposit().getId() == null)){
            LOGGER.error("Bad Parametr Deposit Id Is Null");
            throw new DepositInternalException("Bad Parametr");
        }

        Deposit getDeposit =  getDepositById(depositOperationBase.getSourceDeposit().getId());
        DepositOperationsTransfer operations = new DepositOperationsTransfer();
        if (!vallidateForWithdraw(getDeposit, depositOperationBase.getPrice())){
            if (! byTransfer){
            operations.setPrice(depositOperationBase.getPrice());
            operations.setSourceDeposit(getDeposit);
                createTransaction(operations,TransactionStatus.UNSUCCESSFUL, TransactionsType.WITHDRAW );
            }
            LOGGER.error("Cannot be Withdraw From this account...");
            throw new DepositInternalException("Cannot be Withdraw From this account...");

        }

        getDeposit.setBalance(getDeposit.getBalance().subtract(depositOperationBase.getPrice()));
        depositRepository.save(getDeposit);
        if (! byTransfer) {
            operations.setPrice(depositOperationBase.getPrice());
            operations.setSourceDeposit(getDeposit);
            createTransaction(operations, TransactionStatus.SUCCESSFUL, TransactionsType.WITHDRAW);
        }
        LOGGER.debug("Withdraw chenge  :{}", getDeposit.toString());
        return getDeposit;
    }

//======================================================================================

    private boolean vallidateForWithdraw(Deposit getDeposit, BigDecimal price) {

        if (getDeposit.getBalance().compareTo(price) < 0){
            return false ;
        }
        if (getDeposit.getStatus().equals(DepositStatus.OPEN)  )
            return true ;

        if (getDeposit.getStatus().equals(DepositStatus.BlOCKED_DEPOSIT))
            return true;


      return  false;
   }


//======================================================================================
  public Boolean checkVallidat(Deposit deposit) {

   if (deposit ==  null)
       return false;

   if (deposit.getId()==null)
       return false;

 return true;
}
//======================================================================================
@Transactional(rollbackFor = {Exception.class})
    public DepositOperationsTransfer transfer (DepositOperationsTransfer depositOperationsTransfer) throws DepositInternalException {

    if (depositOperationsTransfer== null) {
        LOGGER.error("Bad Parametr SourceDeposit Id Is Null");
        throw new DepositInternalException("Bad Parameter...");
    }
    if (depositOperationsTransfer.getSourceDeposit().getId()== null) {
        LOGGER.error("Bad Parametr SourceDeposit Id Is Null");
        throw new DepositInternalException("Bad Parameter...");
    }

    if (depositOperationsTransfer.getDestinationDeposit().getId()== null) {
        LOGGER.error("Bad Parametr DestinationDeposit Id Is Null");
        throw new DepositInternalException("Bad Parameter...");
    }

    Deposit getSourceDeposit = getDepositById(depositOperationsTransfer.getSourceDeposit().getId());
    Deposit getDestinationDeposit = getDepositById(depositOperationsTransfer.getDestinationDeposit().getId());
    depositOperationsTransfer.setSourceDeposit(getSourceDeposit);
    depositOperationsTransfer.setDestinationDeposit(getDestinationDeposit);

    if (!checkVallidat(getSourceDeposit)) {
        createTransaction(depositOperationsTransfer,TransactionStatus.UNSUCCESSFUL, TransactionsType.TRANSFER );
        LOGGER.error("Source Deposit Not Valid...");
        throw new DepositInternalException("Source Deposit Not Valid...");
    }

    if (!checkVallidat(getDestinationDeposit)){
        createTransaction(depositOperationsTransfer,TransactionStatus.UNSUCCESSFUL, TransactionsType.TRANSFER );
        LOGGER.error("Destination Deposit Not Valid...");
        throw new DepositInternalException("Destination Deposit Not Valid...");
    };
    DepositOperationsTransfer baseOperationsForDeposit = new DepositOperationsTransfer();
    baseOperationsForDeposit.setSourceDeposit(getSourceDeposit);
    baseOperationsForDeposit.setPrice(depositOperationsTransfer.getPrice());
    baseOperationsForDeposit.setDestinationDeposit(getDestinationDeposit);

    try {
        depositService.withdraw(baseOperationsForDeposit ,true);
    } catch (DepositInternalException e) {
        createTransaction(depositOperationsTransfer,TransactionStatus.UNSUCCESSFUL, TransactionsType.TRANSFER );
        LOGGER.error("In the transmission operation failed to withdraw");
        throw new DepositInternalException("In the transmission operation failed to withdraw" ,e);
    }

    baseOperationsForDeposit.setSourceDeposit(getDestinationDeposit);

    try {
        depositService.deposit( baseOperationsForDeposit, true);
    } catch (DepositInternalException e) {
        createTransaction(depositOperationsTransfer,TransactionStatus.UNSUCCESSFUL, TransactionsType.TRANSFER );
        LOGGER.error("In the transmission operation failed to Deposit");
        throw new DepositInternalException("Destination Deposit Not Valid..." ,e);
    }
    createTransaction(depositOperationsTransfer,TransactionStatus.SUCCESSFUL, TransactionsType.TRANSFER );
    return depositOperationsTransfer;
}
//======================================================================================
public BigDecimal getBalance(Long id) throws DepositInternalException {
    LOGGER.debug("getBalance input parameters is : {}",id .toString());
    Deposit getDeposit =  getDepositById(id);

    if (!checkVallidat(getDeposit)){
        LOGGER.error("Deposit Not Found...");
        throw new DepositInternalException("Deposit Not Found...");
    }
    return  getDeposit.getBalance();

}
//======================================================================================
public List<Deposit> getAllDepositByCustomer(Customer customer) throws DepositInternalException {
    LOGGER.debug("getAllDepositByCustomer input parameters is : {}",(customer == null) ? " null ":customer.toString());
    if (customer== null || customer.getId()==null) {
        LOGGER.error("Bad Parametr customer Is Null");
        throw new DepositInternalException("Bad Parameter...");
    }
    return depositRepository.findAllByCustomer(customer);
    }
//======================================================================================
public Boolean checkExistsDepositByCustumer(Customer customer) throws DepositInternalException {
    LOGGER.debug("checkExistsDepositByCustumer input parameters is : {}",(customer == null) ? " null ":customer.toString());
    if (customer== null) {
        LOGGER.error("Bad Parametr SourceDeposit Id Is Null");
        throw new DepositInternalException("Bad Parameter...");
    }    return depositRepository.existsByCustomer(customer);
}
//======================================================================================
public List<Customer> getCustomerListByDeposit(Deposit deposit){
    LOGGER.info("getCustomerListByDeposit For Service input parameters is : {}",(deposit == null) ? " null ":deposit.toString());
    List<Customer> customerList = webClient
            .post()
            .uri(customerServiceUrl+"/customerservice/getcustomersbydeposit")
            .bodyValue(deposit)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<Customer>>() {})
            .block();
    LOGGER.info("getCustomerListByDeposit Return Servis is customerList count : {}", customerList.size());
    return  customerList;

}
//======================================================================================
@Transactional(rollbackFor = {Exception.class})
public Transaction createTransactionWebRequest(Transaction transaction){
    LOGGER.info("createTransaction For Service input parameters is : {}",(transaction == null) ? " null ":transaction.toString());
    Transaction transactionCreate = webClient
            .post()
            .uri(transactionServiceUrl+"/transactionservice/create")
            .bodyValue(transaction)
            .retrieve()
            .bodyToMono(Transaction.class)
            .block();
    LOGGER.info("createTransaction Return Servis Result: {}", (transaction == null) ? " null ":transaction.toString());
    return transactionCreate;
}
//======================================================================================
@Transactional(rollbackFor = {Exception.class})
public Transaction createTransaction(DepositOperationsTransfer depositOperationsTransfer, TransactionStatus transactionStatus, TransactionsType transactionsType){
    LOGGER.info("createTransaction For Service input parameters is : {}",(depositOperationsTransfer == null) ? " null ":depositOperationsTransfer.toString());
        Transaction transaction = new Transaction();
        Calendar calendar = Calendar.getInstance();
        transaction.setTodoDate(calendar.getTime());
        transaction.setPrice(depositOperationsTransfer.getPrice());
        transaction.setStatus(transactionStatus);
        transaction.setType(transactionsType);

        if (depositOperationsTransfer.getSourceDeposit() == null)
            transaction.setSourceNumber("");
        else
            transaction.setSourceNumber(depositOperationsTransfer.getSourceDeposit().getNumber());

        if (depositOperationsTransfer.getDestinationDeposit() == null)
            transaction.setDestinationNumber("");
        else
            transaction.setDestinationNumber(depositOperationsTransfer.getDestinationDeposit().getNumber());
        depositService.createTransactionWebRequest(transaction);
        return transaction;
    }
//======================================================================================
public Deposit getDepositByNumber(String number)  {
    LOGGER.debug("getDepositByNumber parameter is :{}", number);
    return depositRepository.findByNumber(number);
}
//======================================================================================

}
