package io.github.oluizleme.quarkussocial.rest.dto;

import io.github.oluizleme.quarkussocial.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
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
}
