package com.google.code.lightssh.project.security.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.google.code.lightssh.project.security.service.SecurityUtil;

/**
 * 扩展“验证码”认证
 * 
 * @author YangXiaojin
 * 
 */
public class CaptchaAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {
	
	public static final String SPRING_SECURITY_FORM_CAPTCHA_KEY = "j_captcha";
	
	private String captchaParameter = SPRING_SECURITY_FORM_CAPTCHA_KEY;
	
	public void setCaptchaParameter(String captchaParameter) {
		this.captchaParameter = captchaParameter;
	}

	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		
		//验证码认证
		if( SecurityUtil.isCheckCaptcha( request ) && 
				!SecurityUtil.isPassedCaptchaValidate(
						request, request.getParameter( captchaParameter ) )){
			throw new SecurityBadCaptchaException("验证码错误！");
		}
			
		return super.attemptAuthentication(request, response);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException failed)
			throws IOException, ServletException {

		//增加失败次数
		SecurityUtil.incFailedCount(request);
		
		super.unsuccessfulAuthentication(request, response, failed);
	}
	
	 @Override
	 protected void successfulAuthentication(HttpServletRequest request,
	         HttpServletResponse response, Authentication authResult)
	         throws IOException, ServletException {
		 
		 SecurityUtil.clearFailedCount(request); //失败次数清零
		 
	     super.successfulAuthentication(request, response, authResult);
	 }

}