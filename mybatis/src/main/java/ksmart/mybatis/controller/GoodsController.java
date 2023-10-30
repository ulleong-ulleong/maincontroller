package ksmart.mybatis.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ksmart.mybatis.dto.Goods;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.service.GoodsService;

@Controller
@RequestMapping("/goods")
public class GoodsController {
	
	
	private static final Logger log = LoggerFactory.getLogger(GoodsController.class);

	
	private final GoodsService goodsService;
	
	public GoodsController(GoodsService goodsService) {
		this.goodsService = goodsService;
	}
	
	@PostMapping("/addGoods")
	public String addGoods(Goods goods) {
		log.info("goods: {}", goods);	
		goodsService.addGoods(goods);
		return "redirect:/goods/goodsList";
	}
	
	@GetMapping("/addGoods")
	public String addGoods(Model model) {
		//관리자에 해당하기 때문에 판매자에 해당하는 정보만 가져올 수 있도록.
		List<Member> sellerList = goodsService.getSellerList("l.level_name", "판매자");
		log.info("sellerList:{}", sellerList);
		model.addAttribute("title", "상품등록");
		model.addAttribute("sellerList", sellerList);
		return "goods/addGoods";
	}
	
	
	@GetMapping("/sellerList")
	public String getSellerList(Model model) {
		List<Member> sellerList = goodsService.getSellerList();
		log.info("sellerList: {}", sellerList);
		
		model.addAttribute("title", "판매자 현황");
		model.addAttribute("sellerList", sellerList);
		return "goods/sellerList";
	}
	
	@GetMapping("/goodsList")
	public String getGoodsList(Model model) {
		List<Goods> goodsList = goodsService.getGoodsList();
		log.info("goodsList: {}", goodsList);
		
		model.addAttribute("title", "상품목록");
		model.addAttribute("goodsList", goodsList);
		return "goods/goodsList";
	}
}
