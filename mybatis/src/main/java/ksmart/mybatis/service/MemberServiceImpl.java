package ksmart.mybatis.service;

import java.util.List;

import ksmart.mybatis.dto.Member;
import ksmart.mybatis.mapper.MemberMapper;

/**
 * @Service => 비즈니스 로직 구현
 * @Transactional => 트랙잭션 : 논리적인 작업의 한단위, 특징: A(원자성)C(일관성)I(고립성)D(영속성)  
 */
public class MemberServiceImpl implements MemberServiceInterface {
	
	// DI(의존성 주입)
	private final MemberMapper memberMapper;
	
	// 생성자메소드를 통한 DI
	public MemberServiceImpl(MemberMapper memberMapper) {
		this.memberMapper = memberMapper;
	}

	@Override
	public List<Member> getMemberList() {
		List<Member> memberList = memberMapper.getMemberList();
		return memberList;
	}

}








