package ksmart.mybatis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ksmart.mybatis.dto.LoginHistory;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.dto.MemberLevel;
import ksmart.mybatis.mapper.MemberMapper;

/**
 * @Service => 비즈니스 로직 구현
 * @Transactional => 트랙잭션 : 논리적인 작업의 한단위, 특징: A(원자성)C(일관성)I(고립성)D(영속성)  
 */
@Service("MemberService")
@Transactional
public class MemberService {
	// DI(의존성 주입)
	private final MemberMapper memberMapper;
	
	// 생성자메소드를 통한 DI
	public MemberService(MemberMapper memberMapper) {
		this.memberMapper = memberMapper;
	}
	
	public Map<String, Object> getLoginHistory(int currentPage){
		int rowPerPage = 5;
		//보여줄 행의 시작점
		int startRowNum = (currentPage -1) * rowPerPage;
		//전체 행의 개수
		double rowCnt = memberMapper.getLoginHistoryCnt();
		//마지막 페이지 : 전체 행의 개수 / 보여줄 행의 갯수 ) 올림
		int lastPage = (int) Math.ceil(rowCnt/rowPerPage);
		//보여줄 페이지 번호 초기값:1
		int startPageNum = 1;
		//마지막 페이지 번호 초기값:10, 9페이지까지 있다면 9페이지를 보여준다는 의미
		int endPageNum = (lastPage<10) ? lastPage : 10;
		
		/*동적으로 페이지 번호 구성, 
		내가 위치한 페이지위치에 따른 아래 페이징 넘버가 ex) 기준 7page -> 페이징 2~10, 8page-> 페이징 3~11
		아래 로직은 현재 페이지가 6이상이여야만 작동.
		*/
		if(currentPage > 6 && lastPage > 9) {
			startPageNum = currentPage - 5;
			endPageNum = currentPage + 4;
			if(endPageNum >= lastPage) {
				startPageNum = lastPage - 9;
				endPageNum = lastPage;
			}
		}
		
		List<LoginHistory> loginHistory=memberMapper.getLoginHistory(startRowNum, rowPerPage);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("loginHistory", loginHistory);
		resultMap.put("lastPage", lastPage);
		
		return resultMap;
		
	}
	
	/*회원탈퇴처리
	 * @param memberId(회원아이디), memberLevel(회원등급)
	 * 완성이되면 Controller로 이동
	 * */
	public void removeMember(String memberId, int memberLevel) {
		switch (memberLevel) {
		case 2:
			memberMapper.removeOrderBySellerId(memberId);
			memberMapper.removeGoodsById(memberId);
			break;
		case 3:
			memberMapper.removeOrderByOrderId(memberId);
			break;
		}
		memberMapper.removeLoginHistoryById(memberId);
		memberMapper.removeMemberById(memberId);
	}
	
	/*회원정보 일치 여부 확인
	 * @param memberId, memberPw
	 * @return 일치여부(boolean 형태), 일치하지 않는다면 ("msg(String 형태)")값을 전달
	 * return 타입은 String의 Object 형태.
	 * Map : key와 value로 이루어진 자료구조
	 * key는 문자열로 하는게 가장 좋다.
	 * 그리고 그외 다른 걸 담고 싶어 최상위 객체인 object를 불러옴.*/
	public Map<String, Object> checkMemberInfo(String memberId, String memberPw){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean isChecked = false;
		Member memberInfo = memberMapper.getMemberInfoById(memberId);
		if(memberInfo != null) {
			//회원정보가 null이 아니라면
			String checkPw = memberInfo.getMemberPw();
			if(checkPw.equals(memberPw)) {
				isChecked = true;
				resultMap.put("memberLevel", memberInfo.getMemberLevel());
				
			}
		}
		//검수했는데 비밀번호가 일치하지 않는다면
		if(!isChecked) resultMap.put("msg", "일치하는 회원의 정보가 없습니다.");
		resultMap.put("isChecked", isChecked);
		return resultMap;
	}
	
	/*회원 수정
	 * @param member (수정정보가 담긴 MemberDTO)
	 * */
	public void modifyMember(Member member) {
		memberMapper.modifyMember(member);
	}
	
	/*특정회원 조회
	 * @param memberId (회원아이디)
	 * @return Member memberInfo(회원 DTO) 회원 정보 전송 객체 : 회원 정보를 전송하기 위해 생성된 객체
	 * */
	public Member getMemberInfoById(String memberId) {
		Member memberInfo = memberMapper.getMemberInfoById(memberId);
		return memberInfo;
	}
	/*회원가입
	 * @param member(회원정보)
	 * @return
	 * */
	public int addMember(Member member) {
		int result = memberMapper.addMember(member);
		return result;
		//service에서 data를 반환.
	}
	/**
	 * 아이디 중복체크
	 * @param memberId (회원아이디)
	 * @return 중복: true, 중복x: false (boolean)
	 */
	public boolean idCheck(String memberId) {
		boolean isDuplicate = memberMapper.idCheck(memberId);
		return isDuplicate;
	}
	
	/**
	 * 회원 등급 조회
	 * @return 회원등급리스트 List<MemberLevel>
	 */
	public List<MemberLevel> getMemberLevelList(){
		
		List<MemberLevel> memberLevelList = memberMapper.getMemberLevelList();
		
		return memberLevelList;
	}
	
	/*검색어에 따른 회원의 목록 조회
	 * MemberController에서의 회원목록조회에 대한 오버로딩*/
	public List<Member> getMemberList(String searchKey, String searchValue){
		switch (searchKey) {
		case "memberId":
			searchKey="m.m_id";
			break;
		case "memberName":
			searchKey="m.m_name";
			break;
			//Level은 int이기 떄문에 회원이름 DB의 컬럼명과 맞춰준다.
		case "memberLevel":
			searchKey="l.level_name";
			break;
		case "memberEmail":
			searchKey="m.m_email";
			break;
		}
		/*xml 기준으로 보면 id는 유일해야 한다. 인터페이스에서 id가 method. 메소드 오버로딩 불가, java단까지는 괜찮다.
		 * MemberMapper.interface에서 하나의 메서드를 추가로 작성해야 한다.*/
		List<Member> memberList = memberMapper.getMemberListBySearch(searchKey, searchValue);
		return memberList;
	}
	/**
	 * 회원 목록 조회
	 * @return 회원목록 List<Member>
	 */
	public List<Member> getMemberList() {
		List<Member> memberList = memberMapper.getMemberList();
		/*
		if(memberList != null) {
			for(Member member : memberList) {
				int memberLevel = member.getMemberLevel();
				String memberLevelName = "test";
				switch (memberLevel) {
					case 1:
						memberLevelName = "관리자";
						break;
					case 2:
						memberLevelName = "판매자";
						break;
					case 3:
						memberLevelName = "구매자";
						break;
				}
				member.setMemberLevelName(memberLevelName);
			}
		}
		*/
		return memberList;
	}
}






