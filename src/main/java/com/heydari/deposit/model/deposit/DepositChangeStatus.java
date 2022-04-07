package com.heydari.deposit.model.deposit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DepositChangeStatus {
    private Deposit deposit;
    private DepositStatus depositStatus;
}
