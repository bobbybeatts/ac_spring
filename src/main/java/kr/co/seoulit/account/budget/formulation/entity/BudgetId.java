package kr.co.seoulit.account.budget.formulation.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BudgetId implements Serializable {

    private String accountInnerCode;
    private String accountPeriodNo;
    private String budgetingCode;
    private String deptCode;
    private String workplaceCode;

}
