package io.github.oluizleme.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreatePostRequest {

	@NotEmpty(message = "The post text cannot be empty")
	private String text;
}
