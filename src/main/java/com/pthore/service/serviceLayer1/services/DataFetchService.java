package com.pthore.service.serviceLayer1.services;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pthore.service.serviceLayer1.dto.DomainDto;
import com.pthore.service.serviceLayer1.dto.MiniPost;
import com.pthore.service.serviceLayer1.dto.MiniPostRequestDto;
import com.pthore.service.serviceLayer1.dto.PostDto;
import com.pthore.service.serviceLayer1.dto.PostUpdateDto;
import com.pthore.service.serviceLayer1.dto.PublicUserProfileInfo;
import com.pthore.service.serviceLayer1.dto.UserProfileUpdate;
import com.pthore.service.serviceLayer1.utils.AppConstants;

@Service
public class DataFetchService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private final Logger logger = LoggerFactory.getLogger(DataFetchService.class);
	
	public List<MiniPost> fetchTrendingPostsAsMiniPosts() throws Exception {
		
		ListOperations<String, String> listOperation = redisTemplate.opsForList();
		JsonMapper mapper  = new JsonMapper();
		
		List<String> miniPostsStringList = listOperation.range(AppConstants.REDIS.MOST_TRENDING_POSTS_MINI_POST_TOPIC_NAME, 0, -1);
		List<MiniPost> miniPosts = miniPostsStringList.stream()
				.map(mp -> {
					MiniPost miniPost = new MiniPost();
					try {
						miniPost = mapper.readValue(mp, MiniPost.class);
					} catch (JsonProcessingException e) {
						logger.error(e.getMessage());
					}
					return miniPost;
				}).collect(Collectors.toList());
		
		return miniPosts;
	}
	
	public List<PostDto> fetchTrendingPosts() throws Exception {
		
		ListOperations<String, String> listOperation = redisTemplate.opsForList();
		JsonMapper mapper  = new JsonMapper();
		
		List<String> postsStringList = listOperation.range(AppConstants.REDIS.MOST_TRENDING_POSTS_TOPIC_NAME, 0, -1);
		List<PostDto> posts = postsStringList.stream()
				.map(mp -> {
					PostDto post = new PostDto();
					try {
						post = mapper.readValue(mp, PostDto.class);
					} catch (JsonProcessingException e) {
						logger.error(e.getMessage());
					}
					return post;
				}).collect(Collectors.toList());
		
		return posts;
	}

	public List<MiniPost> fetchMiniPostsBasedOnStrategy(MiniPostRequestDto miniPostRequest, int page) {
		
		String url =  AppConstants.URI.POST_SERVICE_BASE_URL + "miniPosts/" + page;
		ParameterizedTypeReference<List<MiniPost>> typeRef = new ParameterizedTypeReference<List<MiniPost>>() {};
		RequestEntity<MiniPostRequestDto>  requestEntity = new RequestEntity<MiniPostRequestDto>(miniPostRequest, HttpMethod.POST, URI.create(url) );
		ResponseEntity<List<MiniPost>> response = restTemplate.exchange(url , HttpMethod.POST, requestEntity, typeRef);
		List<MiniPost> miniPosts = response.getBody();
		return miniPosts;
		
	}

	public List<MiniPost> fetchMiniPostsBasedOnSearchKeyword(String searchInput, int page) {
		
		String url = AppConstants.URI.POST_SERVICE_BASE_URL + "search/" + searchInput + "/" + page;
		ParameterizedTypeReference<List<MiniPost>> typeRef = new ParameterizedTypeReference<List<MiniPost>>() {};
		ResponseEntity<List<MiniPost>> response = restTemplate.exchange(url , HttpMethod.GET, null, typeRef);
		List<MiniPost> miniPosts = response.getBody();
		return miniPosts;
	}

	public PublicUserProfileInfo fetchPublicProfileInfo(String email) {
		
		String url =  AppConstants.URI.USER_DETAILS_SERVICE_BASE_URL + "public-profile/" + email;
		ResponseEntity<PublicUserProfileInfo> response = restTemplate.getForEntity( url , 
				PublicUserProfileInfo.class);
		return response.getBody();
	}

	public PostDto fetchPost(String postId) throws JsonMappingException, JsonProcessingException {
		
		PostDto post = null;
		ListOperations<String, String> listOperation = redisTemplate.opsForList();
		JsonMapper mapper  = new JsonMapper();
		
		// Cannot use index Operation as its supported in V6.6 and above im using a lower version of redis.
		List<String> postIds = listOperation.range(AppConstants.REDIS.MOST_TRENDING_POSTS_ID_TOPIC_NAME, 0, -1);
		int index = postIds.indexOf(postId);
		if(index > -1) {
			String postString = listOperation.index(AppConstants.REDIS.MOST_TRENDING_POSTS_TOPIC_NAME, index);
			post = mapper.readValue(postString, PostDto.class);
		} else {
			// TODO: since post is not in cache, make a restCall and fetch it.
			String url = AppConstants.URI.POST_SERVICE_BASE_URL + postId;
			ResponseEntity<PostDto> response = restTemplate.getForEntity(url, PostDto.class);
			post = response.getBody();
		}
		return post;
	}

	public void updatePersonalInfo(UserProfileUpdate profileUpdate) {
		
		String url = AppConstants.URI.USER_DETAILS_SERVICE_BASE_URL + "update-profile";
		RequestEntity<UserProfileUpdate>  requestEntity = new RequestEntity<UserProfileUpdate>(profileUpdate, HttpMethod.POST, URI.create(url) );
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
		
	}

	public List<DomainDto> fetchAllDomains() throws Exception {
		// TODO: to be implemented in UDS.
		String url = AppConstants.URI.USER_DETAILS_SERVICE_BASE_URL + "domains/manage/get";
		ParameterizedTypeReference<List<DomainDto>> typeRef = new ParameterizedTypeReference<List<DomainDto>>() {};
		ResponseEntity<List<DomainDto>> response = restTemplate.exchange( url, HttpMethod.GET, null, typeRef);
		List<DomainDto> domains = response.getBody();
		return domains;
	}

	public String createPost(MultipartFile[] files, List<String> postTexts, List<String> metadata, String postTitle, String email) throws IOException {
		
		MultiValueMap<String, Object> multivalueMap = new LinkedMultiValueMap<>();
		for(int i = 0; i< files.length; i++)
			multivalueMap.add("files", files[i].getResource() );
		multivalueMap.add("data", postTexts);
		multivalueMap.add("metadata", metadata);
		multivalueMap.add("postTitle", postTitle);
		multivalueMap.add("email", email);
		String url = AppConstants.URI.POST_SERVICE_BASE_URL + "create";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(multivalueMap, headers);
		
		ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST,  httpEntity, String.class);
		return response.getBody();
	}

	public String updatePost(MultipartFile[] files, List<String> postTexts, List<String> metadata, String postId,
			String postTitle, PostUpdateDto postUpdateDto) {

		MultiValueMap<String, Object> multivalueMap = new LinkedMultiValueMap<>();
		for(int i = 0; i< files.length; i++)
			multivalueMap.add("files", files[i]);
		
		multivalueMap.add("data", postTexts);
		multivalueMap.add("metadata", metadata);
		multivalueMap.add("postTitle", postTitle);
		multivalueMap.add("postId", postId);
		multivalueMap.add("postUpdateDto", postUpdateDto);
		
		String url = AppConstants.URI.POST_SERVICE_BASE_URL + "update";
		ResponseEntity<String> response = restTemplate.postForEntity( url, multivalueMap, String.class);
		return response.getBody();
	}

	public void upvotePost(String postId) {
		
		String url = AppConstants.URI.POST_SERVICE_BASE_URL + "upvote" + postId;
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		String result = response.getBody();
	}

	public byte[] downloadPostImage(String postId, int index) {
		
		String url = AppConstants.URI.POST_SERVICE_BASE_URL + "download/" + postId + "/" + index;
		ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
		byte[] result = response.getBody();

		return result;
	}

	public void updateLearningStreakAndCache(String userEmail, String parentPostId, String postId) {
		/* design:
				- check if the user => parentPostId is same as what you have and also the 
				 time stamp in the redis hash. 
				- if its the same then add the current postId to the redis set containing 
				 the streak posts.
				- else, get the size of the corresponding set and make a rest call to post 
				 service API to send the current Streak value.
				- and insert new parentPostId into hash and add new streak post into the redis Set.
			operations:
				hexists <userEmail> parentPostId
				hget <userEmail> creationTimestamp
				if exists:
					sadd <parentPostId> <postId>
			 	else:
			 		hget <userEmail> parentPostId
					scard <oldParentPostId>
					hdel <userEmail> parentPostId creationTimestamp
					hset <userEmail> parentPostId <parentPostId> 
					hset <userEmail> creationTimestamp <creationTimestamp>			
					srem <oldParentPostId> <members>
					sadd <parentPostId> <postId>
				
					make api call to update learningStreak.
		 */
		HashOperations<String, String, String> hashOperations= this.redisTemplate.opsForHash();
		SetOperations<String, String> setOperations = redisTemplate.opsForSet();
		boolean hasKey = hashOperations.hasKey(userEmail, "parentPostId");
		
		String creationTimeStampString = hashOperations.get(userEmail, "creation-timestamp");
		LocalDate creationTimeStamp = LocalDate.parse(creationTimeStampString);
		boolean isCreatedToday = creationTimeStamp.equals(LocalDate.now());
		long setSize = 0;
		if(hasKey && isCreatedToday) {
			setOperations.add(parentPostId, postId);
			setSize = setOperations.size(parentPostId);
			logger.info("Added a postId = {} under parentPostId = {} of redis set", postId, parentPostId, setSize);
		}else {
			String oldParentPostId = hashOperations.get(userEmail, "parentPostId");
			setSize = setOperations.size(oldParentPostId);
			hashOperations.delete(userEmail, "parentPostId", "creationTimestamp");
			hashOperations.put(userEmail, "parentPostId", parentPostId);
			hashOperations.put(userEmail, "creationTimestamp", LocalDate.now().toString());
			setOperations.remove(oldParentPostId, setOperations.members(oldParentPostId));
			setOperations.add(parentPostId, postId);
			
			// TODO: implement the correct body type here.
			String[] body = new String[] {userEmail, Long.toString(setSize)};
			this.restTemplate.postForEntity(
					AppConstants.URI.USER_DETAILS_SERVICE_BASE_URL + "update-learning-streak", 
					body, 
					String.class);
		}

	}

}
