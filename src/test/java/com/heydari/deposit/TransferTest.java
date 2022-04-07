package com.heydari.deposit;
import com.heydari.deposit.exception.DepositCreateException;
import com.heydari.deposit.exception.DepositInternalException;
import com.heydari.deposit.model.customModel.DepositOperationBase;
import com.heydari.deposit.model.customModel.DepositOperationsTransfer;
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

public class TransferTest {
    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private DepositService depositService;

    @Mock
    private DepositService depositServiceMock;

    //===========================================================================
    @Test
    public void transfer_Test_Happy_Scenario() throws DepositInternalException {

        Calendar calendar = Calendar.getInstance();
        Deposit depositSource = new Deposit(1L,"1","Test",DepositStatus.OPEN, DepositType.DEMAND_DEPOSIT , DepositCurrency.RIAL
                ,new BigDecimal(1000000), calendar.getTime(),null,null);
        Deposit depositDestination = new Deposit(1L,"1","Test",DepositStatus.OPEN, DepositType.DEMAND_DEPOSIT , DepositCurrency.RIAL
                ,new BigDecimal(1000000), calendar.getTime(),null,null);
        DepositOperationsTransfer depositOperationsTransfer = new DepositOperationsTransfer();
        depositOperationsTransfer.setPrice(new BigDecimal(1000));
        depositOperationsTransfer.setSourceDeposit(depositSource);
        depositOperationsTransfer.setDestinationDeposit(depositDestination);

        when(depositRepository.findDepositByNumber(Matchers.any())).thenReturn(depositSource);
        when(depositRepository.findDepositByNumber(Matchers.any())).thenReturn(depositDestination);

        doReturn(new Deposit()).when(depositServiceMock).
                deposit(Matchers.any(),Matchers.any());

        doReturn(new Deposit()).when(depositServiceMock).
                withdraw(Matchers.any(),Matchers.any());

        DepositOperationsTransfer returnTransfer = depositService.transfer (depositOperationsTransfer);
        assertEquals(returnTransfer.getSourceDeposit() , depositOperationsTransfer.getSourceDeposit());
    }

    //===========================================================================
    @Test
    public void Transfer_Test_ByNull_Paramet()  {
        assertThrows( DepositInternalException.class, () -> depositService.transfer (null));
    }
    }
