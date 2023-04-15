package com.pthore.service.serviceLayer1.dto;

import java.util.List;

/*
	with this dto you should be able to update any part of the Post. Including comments.
*/
public class PostUpdateDto {
	
	private String postId;
	private String operation; // available are: UPDATE-MY-POST, COMMENT
	private List<PostComments> postCommentsList;  // why list? because the same person might comment more than once.
	
	
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public List<PostComments> getPostCommentsList() {
		return postCommentsList;
	}
	public void setPostCommentsList(List<PostComments> postCommentsList) {
		this.postCommentsList = postCommentsList;
	}
	
	

	
}
