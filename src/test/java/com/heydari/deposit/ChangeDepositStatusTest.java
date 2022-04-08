package com.heydari.deposit;
import com.heydari.deposit.exception.DepositCreateException;
import com.heydari.deposit.exception.DepositInternalException;
import com.heydari.deposit.model.deposit.*;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)

public class ChangeDepositStatusTest {
    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private DepositService depositService;
    //===========================================================================
    @Test
    public void change_DepositStatus_Test_Happy_Scenario() throws DepositCreateException, DepositInternalException {
        Deposit deposit = new Deposit(1L,"1","Test",DepositStatus.OPEN, DepositType.DEMAND_DEPOSIT , DepositCurrency.RIAL
                ,new BigDecimal(1000000), null,null,null);

        DepositChangeStatus depositChangeStatus = new DepositChangeStatus(deposit.getId(), DepositStatus.ClOSE);

        when(depositRepository.findById(Matchers.any())).thenAnswer(t -> {
            Optional<Deposit> loanOptional =Optional.of(deposit);
            return  loanOptional;
        });

        when(depositRepository.save(Matchers.any())).thenAnswer(t -> {
            Deposit internalDeposit = new Deposit();
            internalDeposit.setStatus(DepositStatus.ClOSE);
            return  internalDeposit;
        });

       Deposit retDeposit  = depositService.changeDepositStatus(depositChangeStatus);
        assertEquals( DepositStatus.ClOSE, retDeposit.getStatus());
    }
    //===========================================================================
    @Test
    public void change_DepositStatus_Test_ByNull_Paramet()  {
        assertThrows( DepositInternalException.class, () -> depositService.changeDepositStatus(null));
    }
}
