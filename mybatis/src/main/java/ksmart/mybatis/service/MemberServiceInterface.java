package ksmart.mybatis.service;

import java.util.List;

import ksmart.mybatis.dto.Member;

public interface MemberServiceInterface {
	// 회원 목록 조회
	public List<Member> getMemberList();
}
