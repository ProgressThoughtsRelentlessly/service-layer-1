package com.pthore.service.serviceLayer1.scheduledTask;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pthore.service.serviceLayer1.dto.MiniPost;
import com.pthore.service.serviceLayer1.dto.PostDto;
import com.pthore.service.serviceLayer1.redis.models.IConsumerObject;
import com.pthore.service.serviceLayer1.redis.models.PostViewCount;
import com.pthore.service.serviceLayer1.utils.AppConstants;

@Service
public class TrendingPostsScheduledService {

	@Autowired
	@Qualifier("redisTemplateForString")
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	@Qualifier("redisTemplateForIConsumerObject")
	private RedisTemplate<String, IConsumerObject<?>> redisTemplate2;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private final Logger logger = LoggerFactory.getLogger(TrendingPostsScheduledService.class);
	
	
	@Async("threadPoolTaskExecutor")
	public void fetchAndUpdateMostViewedPosts() {
		JsonMapper mapper = new JsonMapper();
		
		ListOperations<String, IConsumerObject<?>> listOperation = redisTemplate2.opsForList();
		long size = listOperation.size(AppConstants.REDIS.ANALYTICS_TOPICS.MOST_VIEWED_POST_COUNT);
		List<IConsumerObject<?>> postViewCountList = listOperation
				.range(
						AppConstants.REDIS.ANALYTICS_TOPICS.MOST_VIEWED_POST_COUNT, 
						0, 
						size);
		
		List<String> postIds = postViewCountList.stream()
				.map(p -> {
					return ((PostViewCount)p).getPostId();
				}).collect(Collectors.toList());
		
		// SInce we use REDIS v 5.2.x
		// TODO: update to REDIS >= 6.2 and use 'count' attribute to delete N elements at once.
		ListOperations<String, String> listOperation2 = redisTemplate.opsForList();

		size = listOperation2.size("most-trending-posts");
		for(int i = 0; i < size; i++) {
			listOperation2.leftPop("most-trending-postIds");
			listOperation2.leftPop("most-trending-posts");
		}
		
		postIds.stream().forEach(pid -> {
			// TODO: fetch the Posts in BULK to reduce network cost.
			ResponseEntity<PostDto> response = restTemplate.getForEntity("http://localhost:8080/api/posts/post/" + pid, PostDto.class); 
			PostDto postDto = response.getBody();
			try {
				String postString = mapper.writeValueAsString(postDto);
				listOperation2.leftPush("most-trending-posts", postString);
			} catch (JsonProcessingException e) {
				logger.error("{}", e.getMessage());
			}
		});
		
		listOperation2.leftPushAll("most-trending-postIds", postIds);
		
		HttpEntity<List<String>> requestEntity = new RequestEntity<List<String>>( postIds, HttpMethod.POST, URI.create(""));
		ParameterizedTypeReference<List<MiniPost>> typeRef = new ParameterizedTypeReference<List<MiniPost>>() {};
		ResponseEntity<List<MiniPost>> response = restTemplate.exchange("http://localhost:8080/api/posts/miniPosts/1", HttpMethod.POST, requestEntity, typeRef);
		List<MiniPost> miniPosts = response.getBody();
		
		List<String> miniPostsStringList = miniPosts.stream()
				.map(mp -> {
					try {
						return mapper.writeValueAsString(mp);
					} catch (JsonProcessingException e) {
						logger.error("{}", e.getMessage());
					}
					return null;
				})
				.filter(mp -> mp != null)
				.collect(Collectors.toList());
		
		size = listOperation2.size("most-trending-posts-mini-posts");
		for(int i = 0; i < size; i++) {
			listOperation2.leftPop("most-trending-posts-mini-posts");
		}
		
		listOperation2.leftPushAll("most-trending-posts-mini-posts", miniPostsStringList);
		
	}
	
	@Async("threadPoolTaskExecutor")
	@Scheduled(fixedDelay = 20_000, initialDelay = 3_000	) 
	public void fetchAndUpdateTrendingPostsBasedOnDifferentInsights() {
		
		fetchAndUpdateMostViewedPosts();
		
		// TODO: fetch posts based on other analytics as well. and update REdis.
	}
}
