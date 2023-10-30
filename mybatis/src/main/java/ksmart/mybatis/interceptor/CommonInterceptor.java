package ksmart.mybatis.interceptor;

import java.util.Set;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ksmart.mybatis.controller.MemberController;

@Component
public class CommonInterceptor implements HandlerInterceptor{
//interceptor는 이미 잘 만들어진 것이 있다.
//인터셉터 설정만 한 것이고 사용할 수 있도록 config를 package + class를 만든다.
	
	private static final Logger Log = LoggerFactory.getLogger(MemberController.class);
	
	/*prehandle = controller 진입 전 실행되는 메소드
	 * HandlerMapping이 핸들러 객체를 결정한 후 HandlerAdapter가 핸들러를 호출하기 전에 호출되는 메소드
	 controller에 도달하기 전에 요청을 가로챔. 
	 조건에 맞으면 true, 안 맞으면 false(요청 거부)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//요청 파라미터 (요청에 관한 모든 파라미터를 다 가지고 있음.
		// 그중 keySet
		Set<String> parameterKeySet = request.getParameterMap().keySet();
		
		//MemberId="id001", memberPw="pw001" ...
		StringJoiner param = new StringJoiner(", "); //새롭게 무언가 추가되면 구분자를 넣어주는 역할
		
		//파라미터 내용 문자열 연결
		for(String key : parameterKeySet) {
			param.add(key + ": " + request.getParameter(key));
		}
		
		//공통 액세스 로그 처리
		Log.info("ACCESS INFO =======================================START");
		Log.info("PORT   :::::      {}", request.getLocalPort());
		Log.info("ServerName     :::::       {}", request.getServerName());
		Log.info("Method      :::::     {}", request.getMethod());
		Log.info("URI      :::::      {}", request.getRequestURI());
		Log.info("Client IP      :::::     {}", request.getRemoteAddr());
		Log.info("Parameter      :::::     {}", param);
		Log.info("ACCESS INFO =======================================END");
		
		
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
		//return타입은 boolean이다. 즉, HandlerInterceptor.super.preHandle(request, response, handler)는 true.
	}
	
	/* postHandle = controller의 메소드 실행 직후
	 * HandlerAdapter가 실제로 핸들러를 호출한 후 DispatcherServlet이 뷰를 렌더링하기 전에 호출되는 메소드 
	 * 실행 목적이기 떄문에 voic
	 * */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	/*afterCompletion : 뷰 렌더링 후에 호출되는 메소드
	 * DispatcherServlet이 뷰를 렌더링한 후 호출되는 메소드
	 * */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
}
