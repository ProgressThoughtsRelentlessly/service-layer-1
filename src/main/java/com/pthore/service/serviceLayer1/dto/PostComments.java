package com.pthore.service.serviceLayer1.dto;

import java.util.List;
import java.util.Map;

public class PostComments {
	
	private String commentingUserEmail;
	
	private String comment;
	
	// key is the replier's email address, value is his comment. 
	private List<PostReply> commentReplies;
	
	public PostComments() {
	}

	public PostComments(String commentingUserEmail, String comment, List<PostReply> commentReplies) {
		super();
		this.commentingUserEmail = commentingUserEmail;
		this.comment = comment;
		this.commentReplies = commentReplies;
	}

	@Override
	public String toString() {
		return "PostComments [commentingUserEmail=" + commentingUserEmail + ", comment=" + comment + ", commentReplies="
				+ commentReplies + "]";
	}

	public String getCommentingUserEmail() {
		return commentingUserEmail;
	}

	public void setCommentingUserEmail(String commentingUserEmail) {
		this.commentingUserEmail = commentingUserEmail;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<PostReply> getCommentReplies() {
		return commentReplies;
	}

	public void setCommentReplies(List<PostReply> commentReplies) {
		this.commentReplies = commentReplies;
	}
}
