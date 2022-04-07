package com.heydari.deposit;
import com.heydari.deposit.exception.DepositCreateException;
import com.heydari.deposit.model.customer.Customer;
import com.heydari.deposit.model.deposit.Deposit;
import com.heydari.deposit.model.deposit.DepositCurrency;
import com.heydari.deposit.model.deposit.DepositStatus;
import com.heydari.deposit.model.deposit.DepositType;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class CreateDepositTest {
    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private DepositService depositService;
    //===========================================================================
    @Test
    public void create_Deposit_Test_Happy_Scenario() throws DepositCreateException {
        List<Customer> customerList = Arrays.asList(new Customer(),new Customer());
        Calendar calendar = Calendar.getInstance();
        Deposit deposit = new Deposit(1L,"1","Test",DepositStatus.OPEN, DepositType.DEMAND_DEPOSIT , DepositCurrency.RIAL
        ,new BigDecimal(1000000), calendar.getTime(),null,customerList);

        when(depositRepository.save(Matchers.any())).thenAnswer(t -> {
              return  deposit;
        });

        Deposit retDeposit = depositService.createDeposit(deposit);
        assertEquals( deposit, retDeposit);

    }

    //===========================================================================
    @Test
    public void create_Deposit_Test_ByNull_Paramet()  {
        assertThrows( DepositCreateException.class, () -> depositService.createDeposit(null));
    }
    //===========================================================================
    @Test
    public void create_Deposit_Test_Bad_Paramet()  {
        Deposit deposit = new Deposit();
        assertThrows( DepositCreateException.class, () -> depositService.createDeposit(deposit));
    }
}
