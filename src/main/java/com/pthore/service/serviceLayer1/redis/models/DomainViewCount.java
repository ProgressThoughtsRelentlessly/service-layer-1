package com.pthore.service.serviceLayer1.redis.models;

public class DomainViewCount implements IConsumerObject<DomainViewCount>{
	
	private String domainName;
	private Long viewCount;
	
	
	@Override
	public String toString() {
		return "DomainViewCount [domainName=" + domainName + ", viewCount=" + viewCount + "]";
	}
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public Long getViewCount() {
		return viewCount;
	}
	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}

	@Override
	public int compareTo(DomainViewCount o) {
		
		return Long.compare(this.viewCount, o.viewCount);
	}
	
	
	
}
