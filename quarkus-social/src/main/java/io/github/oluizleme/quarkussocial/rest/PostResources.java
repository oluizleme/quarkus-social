package io.github.oluizleme.quarkussocial.rest;

import io.github.oluizleme.quarkussocial.domain.model.Post;
import io.github.oluizleme.quarkussocial.domain.model.User;
import io.github.oluizleme.quarkussocial.domain.repository.PostRepository;
import io.github.oluizleme.quarkussocial.domain.repository.UserRepository;
import io.github.oluizleme.quarkussocial.rest.dto.CreatePostRequest;
import io.github.oluizleme.quarkussocial.rest.dto.PostResponse;
import io.github.oluizleme.quarkussocial.rest.dto.ResponseError;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;
import java.util.stream.Collectors;

@Path("users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResources {

	private UserRepository userRepository;
	private PostRepository repository;
	private Validator validator;

	@Inject
	public PostResources(UserRepository userRepository, PostRepository repository, Validator validator) {
		this.userRepository = userRepository;
		this.repository = repository;
		this.validator = validator;
	}

	@POST
	@Transactional
	public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request){
		User user = userRepository.findById(userId);
		if(null == user){
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		Set<ConstraintViolation<CreatePostRequest>> violations = validator.validate(request);
		if (!violations.isEmpty()) {
			return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
		}

		Post post = new Post(user, request.getText());
		repository.persist(post);
		return Response.status(Response.Status.CREATED).build();
	}

	@GET
	public Response listPosts(@PathParam("userId") Long userId){
		User user = userRepository.findById(userId);
		if(null == user){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		var list = repository.find(
				"user",
				Sort.by("dateTime", Sort.Direction.Descending),
				user).list();

		var postResposeList = list.stream()
				.map(PostResponse::fromEntity)
				.collect(Collectors.toList());

		return Response.ok(postResposeList).build();
	}
}
