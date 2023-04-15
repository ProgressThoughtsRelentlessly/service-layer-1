package com.pthore.service.serviceLayer1.redis.models;

public class UserActivityTimeRange implements IConsumerObject<UserActivityTimeRange> {
	
	private String activeTimeRange;
	private Long activityCount;
	
	


	@Override
	public String toString() {
		return "UserActivityRange [activeTimeRange=" + activeTimeRange + ", activityCount=" + activityCount + "]";
	}

	public Long getActivityCount() {
		return activityCount;
	}

	public void setActivityCount(Long activityCount) {
		this.activityCount = activityCount;
	}

	public String getActiveTimeRange() {
		return activeTimeRange;
	}

	public void setActiveTimeRange(String activeTimeRange) {
		this.activeTimeRange = activeTimeRange;
	}

	@Override
	public int compareTo(UserActivityTimeRange o) {
		return Long.compare(this.activityCount, o.activityCount);
	}

}
