package edu.poly.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.poly.service.AuthInterceptor;
import edu.poly.service.GlobalInterceptor;
import jakarta.security.auth.message.config.AuthConfig;

@Configuration
public class InterConfig implements WebMvcConfigurer {

	@Autowired
	GlobalInterceptor global;

	@Autowired
	AuthInterceptor auth;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(global)
		.addPathPatterns("/**")
		.excludePathPatterns("/assets/**");
		
		registry.addInterceptor(auth)
		.addPathPatterns("/account/edit","/mail/newMail",
		"/account/chgpwd", "/order/**", "/admin/**","/account/homepage" , "/account/contact",
		"/products/**","/shoppingcart/listorderdetails")
		.excludePathPatterns("/assets/**", "/admin/home/index");
	}
}
