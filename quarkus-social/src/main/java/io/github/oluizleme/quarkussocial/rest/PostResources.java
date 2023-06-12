package io.github.oluizleme.quarkussocial.rest;

import io.github.oluizleme.quarkussocial.domain.model.Post;
import io.github.oluizleme.quarkussocial.domain.model.User;
import io.github.oluizleme.quarkussocial.domain.repository.FollowerRepository;
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
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import java.util.Set;
import java.util.stream.Collectors;

@Path("users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tags(value = @Tag(name = "posts", description = "Operations related to posts"))
public class PostResources {

	private UserRepository userRepository;
	private PostRepository repository;
	private FollowerRepository followerRepository;
	private Validator validator;

	@Inject
	public PostResources(UserRepository userRepository,
						 PostRepository repository,
						 FollowerRepository followerRepository,
						 Validator validator) {
		this.userRepository = userRepository;
		this.repository = repository;
		this.followerRepository = followerRepository;
		this.validator = validator;
	}

	@POST
	@Transactional
	@APIResponses(
			value = {
					@APIResponse(
							responseCode = "201",
							description = "Post created"
					),
					@APIResponse(
							responseCode = "422",
							description = "Invalid post data",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ResponseError.class)
							)
					)
			})
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
	@APIResponses(
			value = {
					@APIResponse(
							responseCode = "200",
							description = "List of posts",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = PostResponse.class)
							)
					),
					@APIResponse(
							responseCode = "400",
							description = "Bad request",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ResponseError.class)
							)
					),
					@APIResponse(
							responseCode = "403",
							description = "Forbidden",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ResponseError.class)
							)
					),
					@APIResponse(
							responseCode = "404",
							description = "User not found"
					)
			})
	public Response listPosts(@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId){
		User user = userRepository.findById(userId);
		if(null == user){
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		if(null == followerId){
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("You forgot the header followerId")
					.build();
		}

		var follower = userRepository.findById(followerId);

		if(null == follower){
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Inexistent followerId")
					.build();
		}

		boolean follows = followerRepository.follows(follower, user);
		if(!follows){
			return Response.status(Response.Status.FORBIDDEN)
					.entity("You can't see these posts")
					.build();
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
