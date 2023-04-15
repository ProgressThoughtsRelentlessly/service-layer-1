package com.pthore.service.serviceLayer1.configs;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pthore.service.serviceLayer1.utils.AppConstants;

@Component
public class JwtVerificationFilter extends OncePerRequestFilter {

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, BadCredentialsException {
		
		
		String jwtString = request.getHeader("jwt");
		if(jwtString == null || jwtString.isBlank()) {
			throw new BadCredentialsException("JWT token not found.");
		}

		String url = AppConstants.URI.AUTH_SERVICE_BASE_URL + "verify-jwt";
		ResponseEntity<String> authServerResponse = restTemplate.postForEntity( url , jwtString, String.class);
		String isJwtValid = authServerResponse.getBody();
		// TODO: getEmail and validation from the api call. and set 'email' attribute in the httpRequest.
		if(isJwtValid.equals("isValid")) {
			
			filterChain.doFilter(request, response);
		} else {
			throw new BadCredentialsException("JWT token is not valid. Login Again.");
		}
	}
	

}
