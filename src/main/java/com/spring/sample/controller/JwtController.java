package com.spring.sample.controller;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.spring.sample.service.JwtService;

@Controller
public class JwtController {
	
	@Autowired
	JwtService jservice;
	
	@RequestMapping(value = "/token")
	public ModelAndView asdsad(ModelAndView mav, HttpServletResponse res) {
		System.out.println("this is /token");
		
		res.addHeader("alg", "HS256");
		res.addHeader("kid", "xxxxxx");
		res.addHeader("typ", "JWT");
		
		// create new random key just for this sample
		// you don't need to create it
		// the secret key will be shared in a separate channel
		byte[] key = new byte[32]; // 256 bit length
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(key);
		SecretKey secretKey = new SecretKeySpec(key, "AES");
		 
		// key id to identify the secret key => 비밀 키를 식별하기 위한 키 ID
		// this value also will be shared together with a secret key => 이 값도 비밀 키와 함께 공유됩니다.
		String keyId = "dev:xxxxxxx"; // sample
		 
		// Sender (encrypting a message) logic from here => 
		// let associatedData as keyId
		byte[] associatedData = keyId.getBytes(StandardCharsets.UTF_8);
		
		byte[] kid = keyId.getBytes();
		
		String jwt = jservice.createToken(keyId);
		System.out.println("token : " + jwt);
		
		mav.addObject("token", jwt);
		mav.setViewName("token");
		return mav;
	}

}
