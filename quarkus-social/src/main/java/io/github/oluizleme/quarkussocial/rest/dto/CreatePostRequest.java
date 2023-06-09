package io.github.oluizleme.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotEmpty;

public class CreatePostRequest {

	@NotEmpty(message = "The post text cannot be empty")
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
