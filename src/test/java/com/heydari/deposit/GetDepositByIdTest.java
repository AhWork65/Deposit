package com.heydari.deposit;
import com.heydari.deposit.exception.DepositCreateException;
import com.heydari.deposit.exception.DepositInternalException;
import com.heydari.deposit.model.deposit.Deposit;
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

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)

public class GetDepositByIdTest {

    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private DepositService depositService;
    //===========================================================================
    @Test
    public void GetDeposit_ById_Test_Happy_Scenario() throws DepositCreateException, DepositInternalException {
        when(depositRepository.findById(Matchers.any())).thenAnswer(t -> {
            Deposit deposit = new  Deposit();
            deposit.setId(10L);
            Optional<Deposit> loanOptional = Optional.of(deposit);
            return loanOptional;
        });

       Deposit retDeposit=  depositService.getDepositById(10L);
       assertEquals(10L, retDeposit.getId());
    }
    }
