package com.heydari.deposit.model.customModel;

import com.heydari.deposit.model.deposit.Deposit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DepositOperationBase {
    private Deposit sourceDeposit;
    private BigDecimal price;

}
