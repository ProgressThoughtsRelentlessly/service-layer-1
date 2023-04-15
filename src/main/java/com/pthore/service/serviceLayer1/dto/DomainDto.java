package com.pthore.service.serviceLayer1.dto;

public class DomainDto {
	
	private String id;
	private String domainName;
	private String domainDescription;	
	private String domainLink;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDomainLink() {
		return domainLink;
	}

	public void setDomainLink(String domainLink) {
		this.domainLink = domainLink;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDomainDescription() {
		return domainDescription;
	}

	public void setDomainDescription(String domainDescription) {
		this.domainDescription = domainDescription;
	}
	
}
