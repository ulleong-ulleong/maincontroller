package ksmart.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import ksmart.mybatis.dto.Goods;
import ksmart.mybatis.dto.LoginHistory;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.dto.MemberLevel;

//추상메서드 선언
@Mapper
public interface MemberMapper {
	//로그인 이력 조회
	public List<LoginHistory> getLoginHistory(int startRowNum, int rowPerPage);
	//로그인 이력 전체 행의 개수
	public int getLoginHistoryCnt();
	
	//판매자 현황 조회, Member.dto 다음 순서
	public List<Member> getSellerList();
	
	//검색조건에 따른 회원목록 조회
	public List<Member> getMemberListBySearch(String searchKey, String searchValue);
	
	// 특정 회원 탈퇴
	public int removeMemberById(String memberId);
	
	//특정 회원의 로그인 이력삭제, -> MemberMapper.interface 작성 후 MemberMapper.xml에 작성
	public int removeLoginHistoryById(String memberId);
	
	//구매자가 구매한 이력 삭제
	public int removeOrderByOrderId(String orderId);
	
	//원래라면 goodsMapper 파일을 만들어서 관리 해야함.
	//1. 판매자가 등록한 상품을 주문한 이력 삭제
	public int removeOrderBySellerId(String sellerId);
	
	//2. 판매자 등록한 상품 삭제
	public int removeGoodsById(String memberId);
	
	
	//특정회원 수정(쿼리 : update, 동적)
	public int modifyMember(Member member);
	
	//특정회원 조회
	public Member getMemberInfoById(String memberId);
	
	// 회원가입
	public int addMember(Member member);
	
	// 회원아이디 중복체크
	public boolean idCheck(String memberId);
	
	// 회원 등급 조회
	public List<MemberLevel> getMemberLevelList();

	// 회원 목록 조회
	public List<Member> getMemberList();
	
	
}
