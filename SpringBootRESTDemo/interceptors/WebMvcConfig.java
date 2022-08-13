package com.mercury.SpringBootRESTDemo.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private ProductHandlerInterceptor productHandlerInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// like AOP Pointcut
		registry.addInterceptor(productHandlerInterceptor)
				.addPathPatterns("/products", "/products/*");
	}
}
