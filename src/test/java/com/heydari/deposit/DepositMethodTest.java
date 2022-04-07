package com.heydari.deposit;
import com.heydari.deposit.exception.DepositCreateException;
import com.heydari.deposit.exception.DepositInternalException;
import com.heydari.deposit.model.customModel.DepositOperationBase;
import com.heydari.deposit.model.deposit.Deposit;
import com.heydari.deposit.model.deposit.DepositCurrency;
import com.heydari.deposit.model.deposit.DepositStatus;
import com.heydari.deposit.model.deposit.DepositType;
import com.heydari.deposit.model.transaction.Transaction;
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
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class DepositMethodTest {
    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private DepositService depositService;

    @Mock
    private DepositService depositServiceMock;


    //===========================================================================
    @Test
    public void deposit_Method_Test_Happy_Scenario() throws  DepositInternalException {
        Calendar calendar = Calendar.getInstance();
        Deposit deposit = new Deposit(1L,"1","Test",DepositStatus.OPEN, DepositType.DEMAND_DEPOSIT , DepositCurrency.RIAL
                ,new BigDecimal(1000000), calendar.getTime(),null,null);
        DepositOperationBase depositOperationBase = new DepositOperationBase(deposit, new BigDecimal(10000));

        when(depositRepository.findById(Matchers.any())).thenAnswer(t -> {
            Optional<Deposit> loanOptional = Optional.of(deposit);
            return loanOptional;
        });

        Transaction transaction = new Transaction();

        when(depositRepository.save(Matchers.any())).thenReturn(deposit);

        doReturn(transaction).when(depositServiceMock).
                createTransactionWebRequest(Matchers.any());

        Deposit retDeposit = depositService.deposit (depositOperationBase, false);
        assertEquals(deposit.getId() , retDeposit.getId());

    }
    //===========================================================================
    @Test
    public void deposit_Method_Test_ByNull_Paramet()  {
        assertThrows( DepositInternalException.class, () -> depositService.deposit (null, false));
    }


    }
