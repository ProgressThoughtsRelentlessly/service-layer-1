package com.pthore.service.serviceLayer1.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pthore.service.serviceLayer1.dto.DomainDto;
import com.pthore.service.serviceLayer1.dto.MiniPost;
import com.pthore.service.serviceLayer1.dto.MiniPostRequestDto;
import com.pthore.service.serviceLayer1.dto.PostDto;
import com.pthore.service.serviceLayer1.dto.PostUpdateDto;
import com.pthore.service.serviceLayer1.dto.PublicUserProfileInfo;
import com.pthore.service.serviceLayer1.dto.UserProfileUpdate;
import com.pthore.service.serviceLayer1.services.DataFetchService;

@RestController
@RequestMapping(value="/api")
@CrossOrigin(value= {"*"})
public class FetchAggregatorController {
	
	
	@Autowired
	private DataFetchService dataFetchService;
	
	private Logger log = LoggerFactory.getLogger(FetchAggregatorController.class);

/*********************************** TEST APIs ***********************************************************************************/
	@GetMapping(value = "/test")
	public ResponseEntity<?> test () throws Exception {
	
		log.info("API is reachable");
		return ResponseEntity.ok().body("Test Success! Api is reachable!");
	}
	
	@PostMapping(value ="/post", name="ApiToHandlePostData")
	public String welcome(@RequestParam(value="files", required=false)MultipartFile[] files, 
			@RequestParam(value="data", required=false, defaultValue = "[]") List<String> postTexts,
			@RequestParam(value="metadata", required=true, defaultValue="[]")List<String> metadata) {
		if(files != null) {
			for(MultipartFile file: files) {
				log.info("Received: Filename = {}, Filesize = {}", file.getOriginalFilename(), file.getSize());
			}
		}

		log.info("post metadata = {}", metadata);
		log.info("post text body = {}", postTexts);
		return "Successfully Received the requests";
	}
	
/***********************************************************************************************************************************/
	/*
	 *   /home page api:
	 *  to fetch the post-card-details, domains, mini-post data (this should load more on scroll)
	 */
	@GetMapping(value = "/cards")
	public ResponseEntity<?> fetchAllPostCardDetails() throws Exception {

		List<MiniPost> miniPosts = dataFetchService.fetchTrendingPostsAsMiniPosts();
		return ResponseEntity.ok().body(miniPosts);
	}
	
	@GetMapping(value="/domains") 
	public ResponseEntity<?> getAllAvailableDomainDetails() throws Exception {
		
		List<DomainDto> domains = dataFetchService.fetchAllDomains();
		return ResponseEntity.ok().body(domains);
	}
	
	/*
	 *  /explore
	 * To loadMini-post list, user-preferred doamins, search.
	 */
	@PostMapping(value="/mini-posts/{page}")
	public ResponseEntity<?> fetchMiniPostsPaginated(
			@RequestBody MiniPostRequestDto miniPostRequest,
			@PathVariable int page) {
		
		List<MiniPost> miniPosts = dataFetchService.fetchMiniPostsBasedOnStrategy(miniPostRequest, page);
		return ResponseEntity.ok().body(miniPosts); 
	}
	
	/*
	 * search posts based on domains, topic names, author, or simply any name
	 *  You could also use the above api to fetch the search result.
	*/
	@GetMapping(value="/search", params = {"searchInput"})
	public ResponseEntity<?> search(@Param(value = "searchInput") String searchInput, 
			@Param(value = "page") int page) {
		
		// TODO: paginate this.
		List<MiniPost> miniPosts = dataFetchService.fetchMiniPostsBasedOnSearchKeyword(searchInput, page);
		return ResponseEntity.ok().body(miniPosts); 
	}
	
	
	/*
	 *   /profile and /profile/:id
	 *   userDetails, userPost-statistics over any year, mini-post-data, create Post POST request.
	 */
	@GetMapping(value="/public-profile/{email}")
	public ResponseEntity<?> fetchPublicProfile(@PathVariable String email) {
		
		PublicUserProfileInfo publicUserProfileInfo = dataFetchService.fetchPublicProfileInfo(email);
		return  ResponseEntity.ok().body(publicUserProfileInfo);
	}
	
	@PostMapping(value="/create-post")
	public ResponseEntity<?> createNewPost(
			@RequestParam(value="files", required=false)MultipartFile[] files, 
			@RequestParam(value="data", required=false) List<String> postTexts,
			@RequestParam(value="metadata", required=true) List<String> metadata,
			@RequestParam(value="postTitle", required=true, defaultValue="") String  postTitle,
			HttpServletRequest request,
			@RequestHeader(required = true, name = "email") String email
			) throws IOException {
		
		dataFetchService.createPost(files, postTexts, metadata, postTitle, email);
		return  ResponseEntity.ok("Success");
	}
	
	/*
	 *   /post/:id
	 *   basic user/author details, post details, the entire post.
	*/
	@GetMapping(value = "/post/{id}")
	public ResponseEntity<?> fetchPost(@PathVariable String postId, 
			@RequestParam("parent-postId")String parentPostId, 
			HttpServletRequest request, 
			HttpServletResponse response) throws JsonMappingException, JsonProcessingException {
		
		String userEmail = (String) request.getAttribute("user-email");
		dataFetchService.updateLearningStreakAndCache(userEmail, parentPostId, postId);
		PostDto post = dataFetchService.fetchPost(postId);
		return  ResponseEntity.ok().body(post);
	}
	/*
	 *   /profile/update
	 *   send a POST request to update the profile.
	*/
	@PutMapping(value = "/update-profile") 
	public ResponseEntity<?>  updatePersonalInfo(@RequestBody UserProfileUpdate profileUpdate) {
		
		dataFetchService.updatePersonalInfo(profileUpdate);
		return  ResponseEntity.ok("Success");
	}
	
	/*
	 * Api to download post Image.
	*/
	@GetMapping(value = "/download/{postId}/{index}", produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<?> downloadPostImage (
			@PathVariable String postId, 
			@PathVariable int index) throws JsonMappingException, JsonProcessingException {
		
		byte[] image = dataFetchService.downloadPostImage(postId, index);
		return  ResponseEntity.ok().body(image);
	}
	
	/*
	 * Api to update or comment on a post.
	*/
	@PostMapping(value = "/post/update")
	public ResponseEntity<?> updatesPostOrComments(
			@RequestParam(value="files", required=false)MultipartFile[] files, 
			@RequestParam(value="data", required=false, defaultValue = "[]") List<String> postTexts,
			@RequestParam(value="metadata", required=false, defaultValue="[]")List<String> metadata,
			@RequestParam(value="postId", required=false, defaultValue="") String  postId,
			@RequestParam(value="postTitle", required=true, defaultValue="") String  postTitle,
			@ModelAttribute PostUpdateDto postUpdateDto
			) {
		
		String result = dataFetchService.updatePost(files, postTexts, metadata, postId, postTitle, postUpdateDto);	
		return ResponseEntity.ok().body(result);
	}
	
	
	/*
	 * Api to upvote the post
	*/
	@PatchMapping(value = "/upvote/{postId}") 
	public ResponseEntity<?>  upvotePost (@PathVariable String postId) {
		
		dataFetchService.upvotePost(postId);
		return  ResponseEntity.ok("Success");
	}
	
	
	
}
