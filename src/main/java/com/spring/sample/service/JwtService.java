package com.spring.sample.service;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {
	public static int GCM_IV_LENGTH = 12; // in byte
	public static int KEY_LENGTH = 32; // in byte
 	public static int AUTH_TAG_LENGTH = 16; // in byte
 	
 	static String headerString = "{\"iss\":\"lg u+\",\"iat\":1627024245}";
// 	static String userInfoString = "{\"ci\":\"ci_value\",\"ti\":\"ti_value\",\"phoneNumber\":\"01012345678\",\"gender\":\"m\"}";

	public String createToken(String secretKey) {

		Map<String, Object> headers = new HashMap<String, Object>(); 
		headers.put("alg", "HS256");
		headers.put("kid", "xxxxxx");
		headers.put("typ", "JWT");
			
		Map<String, Object> payloads = new HashMap<String, Object>();
		
		Long expiredTime = 1000 * 60l;
		Date now = new Date();
		now.setTime(now.getTime() + expiredTime); 
		
		payloads.put("iss", "lg u+");
		payloads.put("iat", new Date());
		payloads.put("exp", now);
		
		String jwt = Jwts.builder()
		       .setHeader(headers)
		       .setClaims(payloads)
		       .signWith(SignatureAlgorithm.HS256, secretKey)
		       .compact();
		
		return jwt;
	}
	
	/**
	* Generate a initialization vector (iv) for encryption
	* 
	* @param length byte length for iv
	* @reutrn iv byte array representation of iv
	* @throws Exception if anything goes wrong
	*/
	private byte[] generateIV(int length) throws Exception {
		SecureRandom secureRandom = new SecureRandom();
		byte[] iv = new byte[length];
		secureRandom.nextBytes(iv);
		return iv;
	}
	
	/**
	* Encrypt a plain text with a given key with an optional associated data
	* Note that in order to prevent from reusing the same iv for multiple encryption,
	* this encryption method internally generates random iv for every request.
	* Also, the iv (okay to be public without any risk) is located before the generated cipher text.
	* 
	* @param plainText to be encrypted
	* @param secretKey AES typed key used to encrypt
	* @param associatedData optional, additional data verifying integrity during decryption
	* @return byte array representation of cipher text
	* @throws Exception if anything goes wrong
	*/
	public byte[] encrypt(byte[] plainText, SecretKey secretKey, 
							byte[] associatedData) throws Exception {
		final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		// generate fresh iv for every encryption
		byte[] iv = generateIV(GCM_IV_LENGTH);
		System.out.println();
		GCMParameterSpec parameterSpec = new GCMParameterSpec(AUTH_TAG_LENGTH * 8, iv);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
		// set associated data if available
		if (associatedData != null) {
			cipher.updateAAD(associatedData);
		}
		byte[] cipherText = cipher.doFinal(plainText);
		ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
		byteBuffer.put(iv);
		byteBuffer.put(cipherText);
		return byteBuffer.array();
	}
	 
	/**
	* Decrypt a cipher text with a given key with an optional associated data
	* 
	* @param cipherText iv with cipher text
	* @param secretKey AES typed key used to decrypt
	* @param associatedData optional, additional data verifying integrity during decryption
	* @return byte array representation of plain text
	* @throws Exception if anything goes wrong
	*/
	public byte[] decrypt(byte[] cipherText, SecretKey secretKey, byte[] 
							associatedData) throws Exception {
		final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		 
		// use first 12 bytes for iv
		GCMParameterSpec parameterSpec = new GCMParameterSpec(AUTH_TAG_LENGTH * 8, cipherText, 0, GCM_IV_LENGTH);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
		// set associated data if available 
		if (associatedData != null) {
			cipher.updateAAD(associatedData);
		}
		 
		// use everything from 12 bytes on as cipher text
		byte[] plainText = cipher.doFinal(cipherText, GCM_IV_LENGTH, cipherText.length - GCM_IV_LENGTH);
		return plainText;
	}
	
}
