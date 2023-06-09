package io.github.oluizleme.quarkussocial.rest.dto;

import lombok.Data;

import java.util.List;


@Data
public class FollowersPerUserResponse {
	private Integer followerCount;
	private List<FollowerResponse> content;

	public FollowersPerUserResponse(Integer followerCount, List<FollowerResponse> content) {
		this.followerCount = followerCount;
		this.content = content;
	}
}
