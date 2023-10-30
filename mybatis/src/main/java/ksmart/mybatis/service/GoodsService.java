package ksmart.mybatis.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ksmart.mybatis.controller.GoodsController;
import ksmart.mybatis.dto.Goods;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.mapper.GoodsMapper;
import ksmart.mybatis.mapper.MemberMapper;

@Service("GoodsService")
@Transactional
public class GoodsService {
	//로그확인
	private static final Logger log = LoggerFactory.getLogger(GoodsController.class);
	
	//DI 의존성 주입
	private final GoodsMapper goodsMapper;
	private final MemberMapper memberMapper;
	
	//생성자 메소드를 통한 DI 생성
	public GoodsService(GoodsMapper goodsMapper, MemberMapper memberMapper) {
		this.goodsMapper = goodsMapper;
		this.memberMapper = memberMapper;
	}
	// 주문이 일어나면 여러 주문 상세데이터가 있는데 여러번 동일 코드로 insert를 해야하는데 그러면 복잡한 작업이니 select code를 이용하면 그대로 가져와 새로운 데이터에 넣으면 됨. 그러면 제약조건에 위배되지 않는다.
	//그래서 select 키를 사용한다.
	public void addGoods(Goods goods) {
		log.info("상품 등록 전 {}:", goods);
		goodsMapper.addGoods(goods);
		log.info("상품 등록 후 {}:", goods);
	}
	
	//상품등록
	//메서드 오버로딩(search)
	public List<Member> getSellerList(String column, String value){
		return memberMapper.getMemberListBySearch(column, value);
	}
	
	//상품목록 조회
	public List<Goods> getGoodsList(){
		return goodsMapper.getGoodsList();
		
	}
	public List<Member> getSellerList(){
		return memberMapper.getSellerList();
	}
}
