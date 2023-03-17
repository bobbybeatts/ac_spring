package kr.co.seoulit.account.budget.formulation.controller;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import kr.co.seoulit.account.budget.formulation.entity.BudgetEntity;
import kr.co.seoulit.account.budget.formulation.service.JpaBudgetService;
import kr.co.seoulit.account.budget.formulation.to.ComparisonBudgetBean;
import kr.co.seoulit.account.sys.common.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import kr.co.seoulit.account.sys.common.util.BeanCreator;

import kr.co.seoulit.account.budget.formulation.service.FormulationService;

import kr.co.seoulit.account.budget.formulation.to.BudgetBean;
import kr.co.seoulit.account.budget.formulation.to.BudgetStatusBean;

import net.sf.json.JSONObject;

@CrossOrigin("*")
@RestController
@RequestMapping("/budget")
public class FormulationController {

    @Autowired
    private FormulationService formulationService;

    @Autowired
    private JpaBudgetService jpaBudgetService;

    ModelMap map = null;
    BeanCreator beanCreator = BeanCreator.getInstance();

//    @GetMapping("/budget")
//    public HashMap<String ,Object> findBudget(
//            @RequestParam("deptCode") String deptCode,
//            @RequestParam("workplaceCode") String workplaceCode,
//            @RequestParam("accountPeriodNo") String accountPeriodNo,
//            @RequestParam("accountInnerCode") String accountInnerCode
//    ) {
//        HashMap<String , Object> map =new HashMap<>();
//        ArrayList<BudgetBean> preBudgetList = formulationService.findBudget(deptCode,workplaceCode,accountPeriodNo,accountInnerCode);
//        map.put("preBudgetList" , preBudgetList);
//        return map;
//    }

    // JPA로 구현 ======== 조회 =========
//    @GetMapping("/budget")
//    public HashMap<String ,Object> findBudget(
//            @RequestParam("deptCode") String deptCode,
//            @RequestParam("workplaceCode") String workplaceCode,
//            @RequestParam("accountPeriodNo") String accountPeriodNo,
//            @RequestParam("accountInnerCode") String accountInnerCode,
//            @RequestParam("budgetingCode") String budgetingCode
//    ) {
//        HashMap<String , Object> map =new HashMap<>();
//        ArrayList<BudgetEntity> preBudgetList = jpaBudgetService.findBudget(deptCode,workplaceCode,accountPeriodNo,accountInnerCode,budgetingCode);
//        map.put("currentBudgetList" , preBudgetList);
//        return map;
//    }

    // JPA로 구현 ======== 조회 =========
    // 위와 다르게 BudgetEntity 바로 맵핑을 시켜서 받아왔음. 그러나 결국 마지막 쿼리메소드 부분에서 String자료형이 아닌 객체타입을 넣어주니 팩토리 오류가 발생함.
    @GetMapping("/budget")
    public HashMap<String ,Object> findBudget(BudgetEntity budgetEntity) {
        HashMap<String , Object> map =new HashMap<>();
        ArrayList<BudgetEntity> preBudgetList = jpaBudgetService.findBudget(budgetEntity);
        map.put("currentBudgetList" , preBudgetList);
        return map;
    }

    @GetMapping("/budgetorganization")
    public BudgetBean findBudgetorganization(@RequestParam String budgetObj) {

        JSONObject budgetJsonObj = JSONObject.fromObject(budgetObj); //예산
        BudgetBean budgetBean = beanCreator.create(budgetJsonObj, BudgetBean.class);


        return formulationService.findBudgetorganization(budgetBean);
    }

    @GetMapping("/budgetlist")
    public void findBudgetList(@RequestParam String budgetObj) {
        JSONObject budgetJsonObj = JSONObject.fromObject(budgetObj); //예산
        BudgetBean budgetBean = beanCreator.create(budgetJsonObj, BudgetBean.class);
        formulationService.findBudgetList(budgetBean);
    }

//    @GetMapping("/budgetlist")
//    public void findBudgetList(@RequestParam String budgetObj) {
//        System.out.println("참조+++++"+budgetObj);
//    }


    //    @PostMapping("/budgetlist")
//    public ArrayList<BudgetBean> registerBudget(@RequestParam("deptCode") String deptCode,
//                                                @RequestParam("workplaceCode") String workplaceCode,
//                                                @RequestParam("accountPeriodNo") String accountPeriodNo,
//                                                @RequestParam("accountInnerCode") String accountInnerCode,
//                                                @RequestParam("m1Budget") String m1Budget,
//                                                @RequestParam("m2Budget") String m2Budget,
//                                                @RequestParam("m3Budget") String m3Budget,
//                                                @RequestParam("m4Budget") String m4Budget,
//                                                @RequestParam("m5Budget") String m5Budget,
//                                                @RequestParam("m6Budget") String m6Budget,
//                                                @RequestParam("m7Budget") String m7Budget,
//                                                @RequestParam("m8Budget") String m8Budget,
//                                                @RequestParam("m9Budget") String m9Budget,
//                                                @RequestParam("m10Budget") String m10Budget,
//                                                @RequestParam("m11Budget") String m11Budget,
//                                                @RequestParam("m12Budget") String m12Budget
//            ) {
//        ArrayList<BudgetBean> budgetBean = formulationService.registerBudget(
//                deptCode,workplaceCode,accountPeriodNo,accountInnerCode,m1Budget,m2Budget,m3Budget,m4Budget,m5Budget
//            ,m6Budget,m7Budget,m8Budget,m9Budget,m10Budget,m11Budget,m12Budget
//        );
//        return budgetBean;
//
//    };
    //================================================================================

//    @PostMapping("/budgetlist")
//    public void registerBudget(@RequestBody JSONObject budgetlist
//    ) {
//        JSONObject budgetJsonObj = JSONObject.fromObject(budgetlist.get("budgetlist"));
//        System.out.println(budgetJsonObj.getClass().getName());
//        BudgetBean budgetBean = beanCreator.create(budgetJsonObj, BudgetBean.class);
//        formulationService.registerBudget(budgetBean);
//    };

    // JPA ++ 예산신청 ++ (save insert)
    @PostMapping("/budgetlist")
    public void registerBudget(@RequestBody BudgetEntity budgetEntity){
        jpaBudgetService.save(budgetEntity);
    };

    //================================================================================
    @PutMapping("/budgetlist")
    public ModelMap modifyBudget(@RequestParam(value = "budgetObj") String budgetObj) {
        JSONObject budgetJsonObj = JSONObject.fromObject(budgetObj); //예산
        BudgetBean budgetBean = beanCreator.create(budgetJsonObj, BudgetBean.class);
        map = new ModelMap();
        try {
            formulationService.modifyBudget(budgetBean);
            map.put("errorCode", 1);
            map.put("errorMsg", "성공!");
        } catch (Exception e) {
            map.put("errorCode", -1);
            map.put("exceptionClass", e.getClass());
        }
        return map;
    }


//    @GetMapping("/budgetstatus")
//    public HashMap<String, Object> findBudgetStatus(@RequestParam String budgetObj) {
//
//        HashMap<String, Object> params = new HashMap<>();
//        JSONObject budgetJsonObj = JSONObject.fromObject(budgetObj); //예산
//        BudgetBean budgetBean = beanCreator.create(budgetJsonObj, BudgetBean.class);
//        params.put("accountPeriodNo", budgetBean.getAccountPeriodNo());
//        params.put("deptCode", budgetBean.getDeptCode());
//        params.put("workplaceCode", budgetBean.getWorkplaceCode());
//        formulationService.findBudgetStatus(params);
//
//        return params;
//    }
@PostMapping("/budgetstatus")
public HashMap<String, Object> searchBudgetStatus(@RequestBody BudgetBean budgetBean) {
    System.out.println("야ㅡㅡ");
    System.out.println(budgetBean);
    System.out.println(budgetBean.getAccountPeriodNo()+","+budgetBean.getDeptCode()+","+budgetBean.getWorkplaceCode());
    HashMap<String, Object> params = new HashMap<>();
    params.put("accountPeriodNo", budgetBean.getAccountPeriodNo());
    params.put("deptCode", budgetBean.getDeptCode());
    params.put("workplaceCode", budgetBean.getWorkplaceCode());
    formulationService.findBudgetStatus(params);

    return params;
}

    @PostMapping("/comparisonBudget")
    public HashMap<String, Object> findComparisonBudget(@RequestBody BudgetBean budgetBean) {
        System.out.println("요청옴? ");
        HashMap<String, Object> params = new HashMap<>();
        System.out.println("버짓빈:" + budgetBean);

        params.put("accountPeriodNo", budgetBean.getAccountPeriodNo());
        params.put("deptCode", budgetBean.getDeptCode());
        params.put("workplaceCode", budgetBean.getWorkplaceCode());
        params.put("accountInnerCode", budgetBean.getAccountInnerCode());

        params = formulationService.findComparisonBudget(params);

        System.out.println("params = " + params);
        System.out.println(params);
        return params;
    }

    @GetMapping("/budgetappl")
    public BudgetBean findBudgetAppl(@RequestParam String budgetObj) {

        JSONObject budgetJsonObj = JSONObject.fromObject(budgetObj); //예산
        BudgetBean budgetBean = beanCreator.create(budgetJsonObj, BudgetBean.class);

        return formulationService.findBudgetAppl(budgetBean);
    }

    @GetMapping("/comparisonBudget")
    public HashMap<String, Object> findComparisonBudget(@RequestParam String budgetObj) {

        System.out.println("budgetObj = " + budgetObj);
        HashMap<String, Object> params = new HashMap<>();
        JSONObject budgetJsonObj = JSONObject.fromObject(budgetObj); //예산
        BudgetBean budgetBean = beanCreator.create(budgetJsonObj, BudgetBean.class);

        params.put("accountPeriodNo", budgetBean.getAccountPeriodNo());
        params.put("deptCode", budgetBean.getDeptCode());
        params.put("workplaceCode", budgetBean.getWorkplaceCode());
        params.put("accountInnerCode", budgetBean.getAccountInnerCode());

        params = formulationService.findComparisonBudget(params);

        System.out.println("params = " + params);
        return params;
    }

    /*@GetMapping("/currentbudget")
    public BudgetBean findCurrentBudget(@RequestParam String budgetObj) {

        JSONObject budgetJsonObj = JSONObject.fromObject(budgetObj); //예산
        BudgetBean budgetBean = beanCreator.create(budgetJsonObj, BudgetBean.class);


        return formulationService.findCurrentBudget(budgetBean);
    }*/

    @GetMapping("/currentbudget")
    public HashMap<String, Object> findCurrentBudget(
            @RequestParam("deptCode") String deptCode,
            @RequestParam("workplaceCode") String workplaceCode,
            @RequestParam("accountPeriodNo") String accountPeriodNo,
            @RequestParam("accountInnerCode") String accountInnerCode
    ){
        HashMap<String ,Object> map =new HashMap<>();
        ArrayList<BudgetBean> currentBudgetList = formulationService.findCurrentBudget(deptCode,workplaceCode,accountPeriodNo,accountInnerCode);
        map.put("currentBudgetList", currentBudgetList);
        return map;
    }
}