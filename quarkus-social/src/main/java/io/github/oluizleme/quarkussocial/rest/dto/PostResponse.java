package io.github.oluizleme.quarkussocial.rest.dto;

import io.github.oluizleme.quarkussocial.domain.model.Post;

import java.time.LocalDateTime;

public class PostResponse {

	private String text;
	private LocalDateTime dateTime;

	private PostResponse(String text, LocalDateTime dateTime) {
		this.text = text;
		this.dateTime = dateTime;
	}

	public static PostResponse fromEntity(Post post) {
		var response = new PostResponse(post.getText(), post.getDateTime());
		return response;
	}

	public String getText() {
		return text;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}
}
