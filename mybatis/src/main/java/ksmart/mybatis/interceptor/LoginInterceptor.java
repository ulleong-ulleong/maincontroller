package ksmart.mybatis.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor{

	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		//로그인 하지 않았다면 로그인 페이지로
		HttpSession session = request.getSession();
		//session은 null일 수가 없다. 브라우져를 열면 session 객체는 열린다.
		
		String sessionId = (String) session.getAttribute("SID"); //getAttribute 자체가 object로 되어 있어 Session 앞에 String을 붙여주어야 한다.
		if(sessionId != null) {
			int sessionLevel = (int) session.getAttribute("SLEVEL");
			String requestURI = request.getRequestURI();
			//판매자
			if(sessionLevel == 2) {
				//주소요청에 대한 필터링 작업, 주소를 외워서 입력해도 로그인하지 않았으면 메인화면으로 갈 수 있도록
				if(	  requestURI.indexOf("/member/memberList") 	> -1
				   || requestURI.indexOf("/member/modify")		> -1	
				   || requestURI.indexOf("/member/remove") 		> -1 
				   || requestURI.indexOf("/goods/add") 			> -1 
				   || requestURI.indexOf("/goods/modify") 		> -1 
				   || requestURI.indexOf("/goods/remove") 		> -1 ) {
					response.sendRedirect("/"); //index화면으로 향할 수 있도록 경로 설정
					return false;
				}
			}
			return true;
		}
		response.sendRedirect("/member/login");
		return false; //재진입을 못하도록 return을 false로 설정해야 한다.
	}
}
