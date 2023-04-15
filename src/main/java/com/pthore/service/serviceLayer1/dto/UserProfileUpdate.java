package com.pthore.service.serviceLayer1.dto;


public class UserProfileUpdate {
	
	private String email;
	private String firstname;
	private String lastname;
	private String mobile;
	private String description;
	private String oldPassword;
	private String newPassword;
	
	public UserProfileUpdate() {}
	
	public UserProfileUpdate(String email, String firstname, String lastname, String mobile,
			String description, String oldPassword, String newPassword) {
		super();
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.mobile = mobile;
		this.description = description;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}

	@Override
	public String toString() {
		return "UserProfileUpdate [email=" + email + ", firstname=" + firstname
				+ ", lastname=" + lastname + ", mobile=" + mobile + ", description=" + description + ", oldPassword="
				+ oldPassword + ", newPassword=" + newPassword + "]";
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	
	
}
