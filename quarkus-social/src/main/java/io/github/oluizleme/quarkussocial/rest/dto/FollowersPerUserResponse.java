package io.github.oluizleme.quarkussocial.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FollowersPerUserResponse {
	private Integer followerCount;
	private List<FollowerResponse> content;

	public FollowersPerUserResponse(Integer followerCount, List<FollowerResponse> content) {
		this.followerCount = followerCount;
		this.content = content;
	}
}
