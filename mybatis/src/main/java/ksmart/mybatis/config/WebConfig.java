package ksmart.mybatis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ksmart.mybatis.interceptor.CommonInterceptor;
import ksmart.mybatis.interceptor.LoginInterceptor;


/**
 * @Configuration 스프링 프로젝트 내의 설정에 관련된 빈을 선언할 때 사용됨.
 * WebMvcConfigurer 클래스: web에 관련된 모든 설정들이 추상화되어 있는 클래스
 * */
@Configuration
public class WebConfig implements WebMvcConfigurer{
	//웹프로그램 설정 -> 인터셉트
	
	// 의존성 주입 Dependency Injection
	private final CommonInterceptor commonInterceptor;
	private final LoginInterceptor loginInterceptor;
	
	public WebConfig(CommonInterceptor commonInterceptor, LoginInterceptor loginInterceptor) {
		this.commonInterceptor = commonInterceptor;
		this.loginInterceptor = loginInterceptor;
	}
	
	/**WebMvcConfigurer의 addInterceptor 메소드를 오버라이드 하여 생성한 interceptor를 추가
	 * */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		//정적 리소스 주소는 배제시켜야 한다.
		//주소 요청 가로챌건지에 대한 패턴, 모든 주소가 있을 떄 로그를 찍어줘(=addPathPattens), excludePathPatterns는 css 또는 js등 정적(static) 리소스는 제외해달라는 의미
		registry.addInterceptor(commonInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/css/**")
				.excludePathPatterns("/js/**")
				.excludePathPatterns("/favicon.ico");
		/*
		//로그인 검증 interceptor
		registry.addInterceptor(loginInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/css/**")
				.excludePathPatterns("/js/**")
				.excludePathPatterns("/favicon.ico")
				.excludePathPatterns("/member/addMember")
				.excludePathPatterns("/member/idCheck")
				.excludePathPatterns("/member/login")
				.excludePathPatterns("/member/logout");
		*/
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
	
}
