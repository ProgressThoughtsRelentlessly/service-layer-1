package com.pthore.service.serviceLayer1.services;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pthore.service.serviceLayer1.dto.MiniPostRequestDto;
import com.pthore.service.serviceLayer1.dto.UserActivity;
import com.pthore.service.serviceLayer1.kafka.KafkaProducerClient;
import com.pthore.service.serviceLayer1.utils.Activities;
import com.pthore.service.serviceLayer1.utils.AppConstants;

@Component
@Aspect
public class UserActivityCaptureAspect {
	
	@Autowired
	private KafkaProducerClient kafkaProducerClient;
	
	private Logger log = LoggerFactory.getLogger(UserActivityCaptureAspect.class);

	// advice
	@Before(value = "userActivityLogging()")
	public void producerUserActivity(JoinPoint jp) throws JsonProcessingException {
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		String email = request.getHeader("email");
		if(email == null || email.isBlank()) {
			email = "anonymous";
		}
		boolean isLoggedIn = !(request.getHeader("jwt") == null) 
				&& !request.getHeader("jwt").isBlank();
		
		String methodName = jp.getSignature().getName();
		Object[] arguments = jp.getArgs();
		
		UserActivity userActivity = new UserActivity();
		userActivity.setActivityTimestamp(LocalDateTime.now());
		userActivity.setLoggedInUser(isLoggedIn); // TODO: add this
		
		switch(methodName) {
		case "fetchPost":
			userActivity.setActivity(Activities.POST_RELATED.VIEW_POST);
			userActivity.setPostId((String)arguments[0]);
			
			break;
		case "createNewPost":
			
			userActivity.setActivity(Activities.POST_RELATED.CREATE_POST);
			break;
		case "fetchPublicProfile": 
			
			userActivity.setActivity(Activities.USER_PROFILE_REALTED.VIEW_PROFILE);
			userActivity.setUserEmail(email);
			// This adds to author strategy
			break;
		case "search":
			
			userActivity.setActivity(Activities.SEARCH_RELATED.SEARCH_POST);
			userActivity.setSearchSentence((String)arguments[0]);
			break;
		case "fetchMiniPostsPaginated":
			
			MiniPostRequestDto miniPostRequest = (MiniPostRequestDto) arguments[0];
			String strategy = miniPostRequest.getStrategy();
			if(strategy.equals("domain")) {
				userActivity.setDomain(miniPostRequest.getInputString());
			} else if(strategy.equals("author")) {
				userActivity.setUserEmail(email);
			}
			break;
			
		case "upvotePost":
			userActivity.setActivity(Activities.POST_RELATED.UPVOTED);
			userActivity.setPostId((String)arguments[0]);
			break;
		case "commentOnPost":
			
			userActivity.setActivity(Activities.POST_RELATED.COMMENTED_ON_POST);
			userActivity.setPostId((String)arguments[0]);			
			break;

		case "updatePersonalInfo":
			
			userActivity.setActivity(Activities.USER_PROFILE_REALTED.UPDATE_PROFILE);
			userActivity.setUserEmail(email);
			
			break;
			
		default:
			
			break;
		}

		kafkaProducerClient.send( AppConstants.KAFKA.INPUT_TOPIC, email, userActivity, UserActivity.class);		
	}
	
	// pointcut
	@Pointcut(value = "execution(* com.pthore.service.serviceLayer1.controllers.FetchAggregatorController.*(..))")
	public void userActivityLogging() {}	
	
}
