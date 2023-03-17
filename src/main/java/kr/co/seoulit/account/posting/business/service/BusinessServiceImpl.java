package kr.co.seoulit.account.posting.business.service;

import kr.co.seoulit.account.posting.business.mapper.JournalMapper;
import kr.co.seoulit.account.posting.business.mapper.SlipApprovalAndReturnMapper;
import kr.co.seoulit.account.posting.business.mapper.SlipMapper;
import kr.co.seoulit.account.posting.business.to.JournalBean;
import kr.co.seoulit.account.posting.business.to.JournalDetailBean;
import kr.co.seoulit.account.posting.business.to.SlipBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;

@Service
@Transactional
public class BusinessServiceImpl implements BusinessService {

	@Autowired
	private JournalMapper journalDAO;
	@Autowired
	private SlipMapper slipDAO;
	@Autowired
	private SlipApprovalAndReturnMapper slipApprovalAndReturnDAO;

	@Override
	public String modifyJournalDetail(JournalDetailBean journalDetailBean) {

		String dName = null;
		Boolean findSelect;
		Boolean findSearch;

		String journalDetailNo = journalDetailBean.getJournalDetailNo();
		String accountControlType = journalDetailBean.getAccountControlType();
		// "==" 비교 연산자는 주소값을 비교하고
		// equals() 메소드는 내용 자체 즉 데이터 값을 비교한다
		findSelect = accountControlType.equals("SELECT");
		findSearch = accountControlType.equals("SEARCH");

		System.out.println("accountControlType: " + accountControlType);
		System.out.println("findSelect: " + findSelect);
		System.out.println("findSearch: " + findSearch);

		journalDAO.updateJournalDetailList(journalDetailBean); // 분개번호로 내용만 업데이트함
		if (findSelect || findSearch)
			dName = journalDAO.selectJournalDetailDescriptionName(journalDetailNo);

		return dName;
	}

	@Override
	public ArrayList<JournalDetailBean> findJournalDetailList(String journalNo) {

		ArrayList<JournalDetailBean> journalDetailBeans = null;
		journalDetailBeans = journalDAO.selectJournalDetailList(journalNo);

		return journalDetailBeans;
	}

	@Override
	public ArrayList<JournalBean> findSingleJournalList(String slipNo) {

		ArrayList<JournalBean> journalList = null;
		journalList = journalDAO.selectJournalList(slipNo);

		return journalList;
	}

//	@Override
//	public ArrayList<JournalBean> findRangedJournalList(String fromDate, String toDate) {
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("fromDate", fromDate);
//		map.put("toDate", toDate);
//		ArrayList<JournalBean> journalList = journalDAO.selectRangedJournalList(map);
//
//		return journalList;
//	}
	@Override
	public ArrayList<JournalBean> findRangedJournalList(HashMap<String, Object> map) {
		ArrayList<JournalBean> journalList = journalDAO.selectRangedJournalList(map);

		return journalList;
	}

	@Override
	public void removeJournal(String journalNo) {

		journalDAO.deleteJournal(journalNo);
		journalDAO.deleteJournalDetailByJournalNo(journalNo);
	}

	@Override
	public void modifyJournal(String slipNo, ArrayList<JournalBean> journalBeanList) {
		System.out.println("modifyJournal 서비스 임플 시작");
		for (JournalBean journalBean : journalBeanList) {
//            System.out.println("journal status:" + journalBean.getStatus());
//            if (journalBean.getStatus().equals("insert"))
			journalDAO.insertJournal(journalBean);
//            else if (journalBean.getStatus().equals("update")) {
//                boolean isChangeAccountCode = journalDAO.updateJournal(journalBean);  // boolean 반환형 필요없음. 항상 false 반환됨. 예전코드에서 수정된듯(dong)

			/*
			 * 항상 false로 불필요 , 전표에 분개가없을경우 분개삭제하는 코드임, 업데이트 부분이기때문에 이걸처리하고싶다면 따른데서 처리되어야됨
			 * (dong)
			 *
			 * if (isChangeAccountCode) {
			 * journalDetailDAO.deleteJournalDetailByJournalNo(journalBean.getJournalNo());
			 * for(JournalDetailBean journalDetailBean: journalBean.getJournalDetailList())
			 * { journalDetailBean.setJournalNo(journalBean.getJournalNo());
			 * journalDetailDAO.insertJournalDetailList(journalDetailBean); }
			 *
			 * }
			 */
		}
	}
	// }

	@Override
	public void registerSlip(SlipBean slipBean) {
		System.out.println("AppServiceImpl_addSlip 시작");
		StringBuffer slipNo = new StringBuffer();
		int sum = 0;

		String slipNoDate = slipBean.getReportingDate().replace("-", ""); // 2021-10-27 -> 20211027
		// 처음에 빈값
		slipNo.append(slipNoDate); // 20200118
		slipNo.append("SLIP"); // 20200118SLIP
		String code = "0000" + (slipDAO.selectSlipCount(slipNoDate) + 1) + ""; // 00001 //오늘 작성한 전표의 카운터 +1
		slipNo.append(code.substring(code.length() - 5)); // 00001 10이상 넘어가는숫자들 처리

		slipBean.setSlipNo(slipNo.toString()); // 20200118SLIP00001
		
		slipDAO.insertSlip(slipBean);
		for (JournalBean journalBean : slipBean.getJournalBean()) {
			String journalNo = journalDAO.selectJournalName(slipBean.getSlipNo());
			journalBean.setJournalNo(journalNo);
			journalBean.setSlipNo(slipNo.toString());

			if (journalBean.getLeftDebtorPrice() == "") {
				journalBean.setLeftDebtorPrice("0");
			} else if (journalBean.getRightCreditsPrice() == "") {
				journalBean.setRightCreditsPrice("0");
			}
			
			journalDAO.insertJournal(journalBean);

			if (journalBean.getJournalDetailList() != null) {
				for (JournalDetailBean journalDetailBean : journalBean.getJournalDetailList()) {
					journalDetailBean.setJournalNo(journalNo);
					System.out.println(journalDetailBean);

					journalDAO.insertJournalDetailList(journalDetailBean);
				}
			}
		}
	}

	@Override
	public void removeSlip(String slipNo) {

		ArrayList<JournalBean> list = journalDAO.selectJournalList(slipNo);

		for (JournalBean journal : list) {
			journalDAO.deleteJournalDetail(journal.getJournalNo());
		}
		journalDAO.deleteJournalAll(slipNo);

		slipDAO.deleteSlip(slipNo);

	}

	@Override
	public String modifySlip(SlipBean slipBean, ArrayList<JournalBean> journalBeans) {

		slipDAO.updateSlip(slipBean);
		for (JournalBean journalBean : journalBeans) {
			System.out.println(journalBean.getJournalNo() + "@@@@@@#");
			journalDAO.updateJournal(journalBean);

			if (journalBean.getJournalDetailList() != null) {

				for (JournalDetailBean journalDetailBean : journalBean.getJournalDetailList()) {

					journalDAO.updateJournalDetailList(journalDetailBean);
				}
			}
		}

		return slipBean.getSlipNo();
	}

	@Override
	public void modifyapproveSlip(SlipBean slipBean) {

//		for (SlipBean slipBean : slipBeans) {
//			slipBean.setSlipStatus(slipBean.getSlipStatus().equals("true") ? "승인완료" : "작성중(반려)");
		slipApprovalAndReturnDAO.updateapproveSlip(slipBean);

	}

	@Override
	public ArrayList<SlipBean> findRangedSlipList(HashMap<String, Object> map) {

		return slipDAO.selectRangedSlipList(map);
	}

	@Override
	public ArrayList<SlipBean> findDisApprovalSlipList() {

		ArrayList<SlipBean> disApprovalSlipList = null;
		disApprovalSlipList = slipApprovalAndReturnDAO.selectDisApprovalSlipList();

		return disApprovalSlipList;
	}

	@Override
	public ArrayList<SlipBean> findSlipDataList(String slipDate) {

		ArrayList<SlipBean> slipList = null;
		slipList = slipDAO.selectSlipDataList(slipDate);

		return slipList;
	}

	@Override
	public HashMap<String, Object> findAccountingSettlementStatus(HashMap<String, Object> params) {
		// TODO Auto-generated method stub

		return slipDAO.selectAccountingSettlementStatus(params);

	}

	@Override
	public ArrayList<SlipBean> findSlip(String slipNo) {

		ArrayList<SlipBean> slip = null;
		slip = slipDAO.selectSlip(slipNo);

		return slip;
	}

	public ArrayList<SlipBean> findApprovalSlipList(HashMap<String, Object> map) {

		return slipDAO.selectApprovalSlipList(map);
	}

	@Override
	public void updateJournal(String slipNo, ArrayList<JournalBean> journalBeanList) {
		// TODO Auto-generated method stub
		for (JournalBean journalBean : journalBeanList) {
//          System.out.println("journal status:" + journalBean.getStatus());
//          if (journalBean.getStatus().equals("insert"))
			journalDAO.updateJournal(journalBean);
		}
	}

	@Override
	public void updateSlip(SlipBean slipBean) {
		// TODO Auto-generated method stub
		System.out.println("AppServiceImpl_addSlip 시작");

		slipDAO.updateSlip(slipBean);
		for (JournalBean journalBean : slipBean.getJournalBean()) {
			journalDAO.updateJournal(journalBean);
			if (journalBean.getJournalDetailList() != null)
				for (JournalDetailBean journalDetailBean : journalBean.getJournalDetailList()) { // 분개상세항목들
					journalDAO.updateJournalDetailList(journalDetailBean);
				}
		}
	}

	@Override
	public void approvalSlipRequest(SlipBean slipBean) {
		// TODO Auto-generated method stub
		System.out.println("AppServiceImp_approvalSlipRequest 시작");
		slipDAO.updateSlipApproval(slipBean);

	} 

	@Override
	public ArrayList<JournalBean> findApprovalJournalList(String slipNo) {
		// TODO Auto-generated method stub
		return slipApprovalAndReturnDAO.selectApprovalJournalList(slipNo);
	}

	@Override
	public ArrayList<JournalDetailBean> addJournalDetailList(String accountCode) {
		// TODO Auto-generated method stub
		return journalDAO.addJournalDetailList(accountCode);
	}

	@Override
	public void tempModifyJournalDetail(ArrayList<JournalDetailBean> journalDetailBean) {
		// TODO Auto-generated method stub
		for (JournalDetailBean journalDetail : journalDetailBean) {
			journalDetail.setJournalNo("temp");
			journalDAO.insertJournalDetailTemp(journalDetail);
		}
	}
}
