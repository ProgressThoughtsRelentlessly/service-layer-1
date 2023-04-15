package com.pthore.service.serviceLayer1.dto;

import java.util.List;
import java.util.Map;

public class PublicUserProfileInfo {
	
	private String email;
	private String profilePicture;
	private Long followersCount;
	private Map<String, String> socialMediaLinks;
	private Map<String, Integer> monthlyPostCount;
	private List<String> followedGroups;
	
	public PublicUserProfileInfo() {}
	

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public Long getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(Long followersCount) {
		this.followersCount = followersCount;
	}
	public Map<String, String> getSocialMediaLinks() {
		return socialMediaLinks;
	}
	public void setSocialMediaLinks(Map<String, String> socialMediaLinks) {
		this.socialMediaLinks = socialMediaLinks;
	}
	public Map<String, Integer> getMonthlyPostCount() {
		return monthlyPostCount;
	}
	public void setMonthlyPostCount(Map<String, Integer> monthlyPostCount) {
		this.monthlyPostCount = monthlyPostCount;
	}
	
	
}
