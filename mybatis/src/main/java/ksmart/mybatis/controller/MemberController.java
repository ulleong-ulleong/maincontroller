package ksmart.mybatis.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ksmart.mybatis.dto.LoginHistory;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.dto.MemberLevel;
import ksmart.mybatis.mapper.MemberMapper;
import ksmart.mybatis.service.MemberService;

@Controller
@RequestMapping("/member")
public class MemberController {
	
	private static final Logger Log = LoggerFactory.getLogger(MemberController.class);
	
	private final MemberService memberService;
	
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	@GetMapping("/loginHistory")
	@SuppressWarnings("unchecked")
	public String getLoginHistory(Model model
								 ,@RequestParam(name="currentPage", required=false, defaultValue="1") int currentPage) {
		
		Map<String, Object> resultMap = memberService.getLoginHistory(currentPage);
		
		List<LoginHistory> loginHistory = (List<LoginHistory>) resultMap.get("loginHistory");
		int lastPage = (int) resultMap.get("lastPage");
		int startPageNum = (int) resultMap.get("startPageNum");
		int endPageNum = (int) resultMap.get("endPageNum");
		
		model.addAttribute("title", "로그인 이력조회");
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("loginHistory", loginHistory);
		model.addAttribute("lastPage", lastPage);
		model.addAttribute("startPageNum", startPageNum);
		model.addAttribute("endPageNum", endPageNum);
		
		return "member/loginHistory";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		//세션 초기화
		session.invalidate();
		return "redirect:/member/login";
	}
	
	@PostMapping("/login")
	public String login(@RequestParam(name="memberId")String memberId
						,@RequestParam(name="memberPw")String memberPw
						,HttpSession session
						,RedirectAttributes reAttr) {
		
		//()에 HttpServletRequest request를 입력하고 하위에 request.getSession()를 선언하는 방법 하나
		//()에 바로 session을 선언하는 방법 둘
	//로그인은 요청되고 끝나는 상태가 아닌 유지가 되야 한다.
		//세션 객체는 브라우저가 내 주소로 요청하고 주소가 열렸을 때 그때 사용됨.
		Map<String, Object> resultMap = memberService.checkMemberInfo(memberId, memberPw);
			boolean isChecked = (boolean) resultMap.get("isChecked");
			if(isChecked) {
				//로그인 처리
				int memberLevel = (int) resultMap.get("memberLevel");
				String memberName = (String) resultMap.get("memberName");
				
				session.setAttribute("SID", memberId);
				session.setAttribute("SNAME", memberName);
				session.setAttribute("SLEVEL", memberLevel);
				//로그인 처리 후에는 메인 페이지로 전환
				return "redirect:/";
				//redirect는 강제로 값을 받아오는 형식이기 떄문에 http: get방식이다.
			}
			reAttr.addFlashAttribute("msg", "일치하는 회원의 정보가 없습니다.");
			return "redirect:/member/login";
			//http://localhost/;jsessionid=B8E19EC2D31C592C8A868CC73D6E68F6
		
	}
	
	@GetMapping("/login")
	public String login(Model model, @RequestParam(name="msg", required=false) String msg) {
		
		model.addAttribute("title", "로그인");
		if(msg != null) model.addAttribute("msg", msg);
		
		return "login/login";
	}
	
	@PostMapping("/removeMember")
	public String removeMember(@RequestParam(value="memberId") String memberId, 
							   @RequestParam(value="memberPw") String memberPw,
							   RedirectAttributes reAttr) {
		//비밀번호 일치 여부 확인
		//기존에 있던 무언갈 활용 뭘까? -> 기존 특정회원을 조회했던 내용으로, 서로 다른 값을 받는.
		Map<String, Object> resultMap = memberService.checkMemberInfo(memberId, memberPw);
		//resultMap에 빨간색 밑줄이 있는 경우 자식요소에 부모형태를 넣을 수 없어서.
		//down 캐스팅을 해야함.
		//기존 : boolean isChecked = resultMap.get(isChecked);
		boolean isChecked = (boolean) resultMap.get("isChecked");
		if(isChecked) {
			//삭제처리
			int memberLevel = (int) resultMap.get("memberLevel");
			//MemberMapper.interface -> MemberMapper.xml -> MemberService -> MemberController에 이름.
			memberService.removeMember(memberId, memberLevel);
			//삭제처리 후에는 회원의 목록 페이지로 전환
			return "redirect:/member/memberList";
			//redirect는 강제로 값을 받아오는 형식이기 떄문에 http: get방식이다.
		}
		
		reAttr.addAttribute("memberId", memberId);
		reAttr.addAttribute("msg", resultMap.get("msg"));
		//일치하지 않는다면 기존 페이지(회원 탈퇴화면)로 전환
		return "redirect:/member/removeMember";
		
	}
	
	/*회원 탈퇴 처리
	 * @param memberId
	 * @return 특정회원 탈퇴화면
	 * */
	
	@GetMapping("/removeMember")
	public String removeMember(@RequestParam(value="memberId") String memberId
							  ,@RequestParam(value="msg", required=false)String msg //msg값은 있으나 없으나 받아야되니까 required false를 선언.
							  ,Model model) {
		model.addAttribute("tltle", "회원탈퇴");
		model.addAttribute("memberId", memberId);
		
		if(msg != null) model.addAttribute("msg", msg);
		return "member/removeMember";
	}
	
	/*회원 수정 처리
	 * @param member(커멘드 객체)
	 * @return redirect:/member/memberList => 회원 리스트 리디렉션 요청
	 * */
	@PostMapping("/modifyMember")
	public String modifyMember(Member member) {
		Log.info("회원수정 Member: {}", member);
		
		//회원 수정 처리
		memberService.modifyMember(member);
		
		return "redirect:/member/memberList";
	}
	
	/*회원 목록에서 수정 요청을 받아보자
	 * 회원 수정 폼 ex) /member/modifyMember?memberId=id001에서 memberId=id001는 쿼리스트링이라고 한다.
	 * @param 받는 데이터가 하나이기에 굳이 Member 객체를 새로 생성할 필요가 없다. 그러면 커맨드 객체를 굳이 선언해야 할까? 그렇기에 RequestParam을 사용한다.
	 * RequestParam에는 required 옵션이 있는데 왜 false로 설정하지 않을까? 
	 * -> RequstParam 옵션 : required 속성이란? 쓰지 않으면 true로 설정, 하지만 false로 놓게 되면 주소만 정확하다면 요청한다.
	 * required false는 보통 검색할 떄 사용한다.
	 * @RequestParam String memberId(회원아이디)입력 받음.
	 * @return 특정회원 정보 수정 페이지로 이동(포워딩)
	 * */
	@GetMapping("/modifyMember")
	public String modifyMember(@RequestParam(name="memberId") String memberId, Model model) {
		//data를 보내줄 수 있게 model도 필요하다.
		Member memberInfo = memberService.getMemberInfoById(memberId);
		
		//Member 수정에서 회원등급도 표시하기 위한 작성
		List<MemberLevel> memberLevelList = memberService.getMemberLevelList();
		Log.info("memberId: {}" + memberId);
		
		model.addAttribute("title", "회원수정");
		model.addAttribute("memberInfo", memberInfo);
		//Member 수정에서 회원등급도 표시하기 위한 작성
		model.addAttribute("memberLevelList", memberLevelList);
		
		return "member/modifyMember";
	}
	/**
	 * 회원가입 처리
	 * @param Member member (컨트롤러에서 매개변수로 DTO 선언을 하면 커맨드객체)
	 * @return "redirect:/member/memberList" -> 처리후 새로운 주소 요청
	 */
	@PostMapping("/addMember")
	public String addMember(Member member) {
		
		Log.info("회원가입 Member: {}", member);
		//System.out.println("회원가입시 입력한 데이터 Member: " + member);
		memberService.addMember(member);
		
		return "redirect:/member/memberList";
	}
	
	/**
	 * 아이디 중복체크 (ajax 요청 응답)
	 * @param memberId (클라이언트에서 입력한 아이디)
	 * @return @ResponseBody 응답시 body 영역에 응답한 데이터를 전달
	 */
	@PostMapping("/idCheck")
	@ResponseBody
	public boolean idCheck(@RequestParam(name="memberId") String memberId) {
		
		Log.info("memberId:{}", memberId);
		//System.out.println("memberId : " + memberId);
		
		boolean isduplicate = memberService.idCheck(memberId);
		
		return isduplicate;
	}

	/**
	 * 회원가입폼
	 * @param model
	 * @return view => member/addMember 회원가입폼페이지
	 */
	@GetMapping("/addMember")
	public String addMember(Model model) {
		
		List<MemberLevel> memberLevelList = memberService.getMemberLevelList();

		model.addAttribute("title", "회원가입");
		model.addAttribute("memberLevelList", memberLevelList);
		
		return "member/addMember";
	}
	
	/**
	 * 회원목록 조회
	 * @param model
	 * @return view => member/memberList 회원목록페이지
	 */
	@GetMapping("/memberList")
	public String getMemberList(Model model
								,@RequestParam(name="searchKey", required = false) String searchKey
								,@RequestParam(name="searchValue", required = false, defaultValue="") String searchValue) {
								//requestParam의 3가지 속성 name=value, 기본값은 null이라면 ""으로 초기화하겠다. required
		
		List<Member> memberList = null;
		if(searchKey != null) {
			memberList = memberService.getMemberList(searchKey, searchValue);
		}else {
			memberList = memberService.getMemberList();
		}
		
		//앞으로 sysout으로 찍지 않고 log4j로 찍을 것. 
		Log.info("회원목록 {}", memberList);
		//System.out.println(memberList); -> 리소스 낭비가 있음.
		Log.info("searchKey {}", searchKey);
		Log.info("searchValue {}", searchValue);
		
		
		model.addAttribute("title", "회원목록");
		model.addAttribute("memberList", memberList);
		
		return "member/memberList";
	}
	
}
