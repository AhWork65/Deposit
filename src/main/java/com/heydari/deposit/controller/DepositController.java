package com.heydari.deposit.controller;

import com.heydari.deposit.exception.DepositBadRequestException;
import com.heydari.deposit.exception.DepositCreateException;
import com.heydari.deposit.exception.DepositInternalException;
import com.heydari.deposit.model.customer.Customer;
import com.heydari.deposit.model.deposit.Deposit;
import com.heydari.deposit.model.customModel.DepositOperationBase;
import com.heydari.deposit.model.customModel.DepositOperationsTransfer;
import com.heydari.deposit.model.deposit.DepositChangeStatus;
import com.heydari.deposit.service.DepositService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/depositservice")
public class DepositController {

    private final static Logger LOGGER = LoggerFactory.getLogger(DepositController.class);

    @Autowired
    private DepositService depositService;

//======================================================================================

    @PostMapping("/create")
    public Deposit createDeposit(@RequestBody Deposit deposit){
        Deposit reDeposit;
        try {
            LOGGER.info("createDeposit INPUT PARAMET IS {} ",deposit.toString());
            reDeposit = depositService.createDeposit(deposit);
        } catch (Exception e) {
            LOGGER.error("createDeposit Exception IS {} ",e.getMessage());
            throw new DepositBadRequestException(e.getMessage());
        }
        LOGGER.info("createDeposit OutPut  IS {} ",deposit.toString());
        return reDeposit;
    }
//======================================================================================

    @GetMapping("/getalldeposit")
    public List<Deposit> getAllDeposit()  {
        List<Deposit> depositList = depositService.getAllDeposit();
        LOGGER.info("getalldeposit Output  {} Deposit ",depositList.size());
        return depositList;
    }
//======================================================================================

    @PutMapping ("/changestatus")
    public void changeDepositStatus(@RequestBody DepositChangeStatus deposit){
        LOGGER.info("changeDepositStatus Input Status is  {} ",deposit.toString());
        try {
            depositService.changeDepositStatus(deposit);
        } catch (Exception e) {
            LOGGER.error("changeDepositStatus Exception IS {} ",e.getMessage());
            throw new DepositBadRequestException(e.getMessage());
        }
    }
//======================================================================================

    @PostMapping("/deposit")
    public void deposit(@RequestBody DepositOperationBase depositOperationBase){
        LOGGER.info("deposit Input Parametr is  {} ",depositOperationBase.toString());
        try {
            depositService.deposit (depositOperationBase, false);
        } catch (Exception e) {
            LOGGER.error("deposit Exception IS {} ",e.getMessage());
            throw new DepositBadRequestException(e.getMessage());
        }
    }
//======================================================================================

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody DepositOperationBase depositOperationBase){
        LOGGER.info("withdraw Input Parametr is  {} ",depositOperationBase.toString());
        try {
            depositService.withdraw (depositOperationBase, false);
        } catch (Exception e) {
            LOGGER.error("withdraw Exception IS {} ",e.getMessage());
            throw new DepositBadRequestException(e.getMessage());
        }
    }
//======================================================================================

    @PostMapping("/transfer")
    public void transfer(@RequestBody DepositOperationsTransfer depositOperationsTransfer){
        LOGGER.info("transfer Input Parametr is  {} ",depositOperationsTransfer.toString());
        try {
            depositService.transfer (depositOperationsTransfer);
        } catch (Exception e) {
            LOGGER.error("transfer Exception IS {} ",e.getMessage());
            throw new DepositBadRequestException(e.getMessage());
        }
    }
//======================================================================================
  @GetMapping("/getbalance/{depositId}")
    public BigDecimal getBalance(@PathVariable("depositId") Long id){
      LOGGER.info("getBalance Input Parametr is  {} ",id.toString());
        try {
            return depositService.getBalance (id);
        } catch (Exception e) {
            LOGGER.error("getBalance Exception IS {} ",e.getMessage());
            throw new DepositBadRequestException(e.getMessage());
        }
    }
//======================================================================================
@PostMapping("/getdepositsbycustomer")
    public List<Deposit> getDepositsByCustomer (@RequestBody Customer customer){
    LOGGER.info("getBalansByCustomer Input Parametr is  {} ",customer.toString());
    List<Deposit> depositList = null;
    try {
        depositList = depositService.getAllDepositByCustomer(customer);
    } catch (Exception e) {
        LOGGER.error("getBalansByCustomer Exception IS {} ",e.getMessage());
        throw new DepositBadRequestException(e.getMessage());
    }
    LOGGER.info("getBalansByCustomer OutPut  {} Deposit ",depositList.size());
    return depositList;
    }
//======================================================================================
@PostMapping("/existsdepositbycustomer")
    public Boolean checkExistsDepositByCustumer (@RequestBody Customer customer){
        LOGGER.info("checkExistsDepositByCustumer Input Parametr is  {} ",customer.toString());
    Boolean resBoolean = null;
    try {
        resBoolean = depositService.checkExistsDepositByCustumer(customer);
    } catch (Exception e) {
        LOGGER.error("checkExistsDepositByCustumer Exception IS {} ",e.getMessage());
        throw new DepositBadRequestException(e.getMessage());
    }
    LOGGER.info("checkExistsDepositByCustumer Output Parametr is  {} ",resBoolean.toString());
        return resBoolean;
    }
//======================================================================================
@PostMapping("/getcustomersbydeposit")
public List<Customer> getCustomerListByDeposit(@RequestBody Deposit deposit) {
    LOGGER.info("getCustomerListByDeposit Input Parametr is  {} ",deposit.toString());
    List<Customer> customerList = depositService.getCustomerListByDeposit(deposit);
    LOGGER.info("getCustomerListByDeposit OutPut  {} Deposit ",customerList.size());
    return customerList;

}
//======================================================================================
 @GetMapping("/getdepositbyid/{depositId}")
    public Deposit getDepositById(@PathVariable("depositId") Long id){
        LOGGER.info("getDepositById Input Parametr is  {} ",id.toString());
        Deposit deposit;
        try {
            deposit =  depositService.getDepositById (id);
        } catch (Exception e) {
            LOGGER.error("getDepositById Exception IS {} ",e.getMessage());
            throw new DepositBadRequestException(e.getMessage());
        }
        LOGGER.info("getDepositById OutPut  {}  ",deposit.toString());
        return deposit;
    }
//======================================================================================
@GetMapping("/getdepositbynumber/{depositNumber}")
public void getDepositByNumber(@PathVariable("depositNumber") String depositNumber){
      LOGGER.info("getDepositByNumber Input Parametr is  {} ",depositNumber);
      depositService.getDepositByNumber (depositNumber);
}
//======================================================================================

}

