package com.pthore.service.serviceLayer1.redis.models;

public class SearchedKeywordCount implements IConsumerObject<SearchedKeywordCount> {
	private String keyword;
	private Long usageCount;
	
	@Override
	public String toString() {
		return "SearchedKeyword [keyword=" + keyword + ", usageCount=" + usageCount + "]";
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Long getUsageCount() {
		return usageCount;
	}
	public void setUsageCount(Long usageCount) {
		this.usageCount = usageCount;
	}
	@Override
	public int compareTo(SearchedKeywordCount o) {
		
		return Long.compare(this.usageCount, o.usageCount);
	}
	
}
