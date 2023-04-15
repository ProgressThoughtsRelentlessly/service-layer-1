package com.pthore.service.serviceLayer1.utils;

import java.util.Arrays;
import java.util.List;

public interface AppConstants {
	
	// public final String
	
	public interface KAFKA {
		public final String KAFKA_BOOTSTRAP_SERVER = "127.0.0.1:9092";
		public final String INPUT_TOPIC = "users_activities";

	}
	
	
	public interface REDIS {
		
		public final String REDIS_HOST_AND_PORT = "127.0.0.1:6379";
		public final List<String> ANALYTICS_OUTPUT_TOPIC_NAMES = Arrays.asList(
				"most-viewed-post-count", 
				"most-viewed-profile-count",
				"most-upvoted-post-count",
				"most-searched-keyword-count",
				"most-popular-domain-count",
				"peak-user-activity-range");
		
		public interface ANALYTICS_TOPICS {
			public final String MOST_VIEWED_POST_COUNT = "most-viewed-post-count";
			public final String MOST_VIEWED_PROFILE_COUNT = "most-viewed-profile-count";
			public final String MOST_UPVOTED_POST_COUNT = "most-upvoted-post-count";
			public final String MOST_SEARCHED_KEYWORD_COUNT = "most-searched-keyword-count";
			public final String MOST_POPULAR_DOMAIN_COUNT = "most-popular-domain-count";
			public final String PEAK_USER_ACTIVITY_RANGE= "peak-user-activity-range";

		}
		public final String MOST_TRENDING_POSTS_ID_TOPIC_NAME = "most-trending-posts-ids";
		public final String MOST_TRENDING_POSTS_TOPIC_NAME = "most-trending-posts";
		public final String MOST_TRENDING_POSTS_MINI_POST_TOPIC_NAME = "most-trending-posts-mini-posts";
		
	}
	
	public interface URI {
		public final String POST_SERVICE_BASE_URL = "http://localhost:8020/api/posts/";
		public final String USER_DETAILS_SERVICE_BASE_URL = "http://localhost:8010/api/uds/";
		public final String AUTH_SERVICE_BASE_URL = "http://localhost:8000/api/auth/";
	}
	
}
