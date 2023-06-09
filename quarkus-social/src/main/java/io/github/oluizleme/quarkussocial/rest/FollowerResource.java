package io.github.oluizleme.quarkussocial.rest;

import io.github.oluizleme.quarkussocial.domain.model.Follower;
import io.github.oluizleme.quarkussocial.domain.repository.FollowerRepository;
import io.github.oluizleme.quarkussocial.domain.repository.UserRepository;
import io.github.oluizleme.quarkussocial.rest.dto.CreateFollowerRequest;
import io.github.oluizleme.quarkussocial.rest.dto.FollowerResponse;
import io.github.oluizleme.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

	private FollowerRepository repository;
	private UserRepository userRepository;

	@Inject
	public FollowerResource(FollowerRepository repository,
							UserRepository userRepository) {
		this.repository = repository;
		this.userRepository = userRepository;
	}

	@PUT
	@Transactional
	public Response followUser(@PathParam("userId") Long userId, CreateFollowerRequest request) {
		var user = userRepository.findById(userId);

		if(userId.equals(request.getFollowerId())) {
			return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself").build();
		}

		if(null == user) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		var follower = userRepository.findById(request.getFollowerId());
		if(null == follower) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		boolean follows = repository.follows(follower, user);
		if(!follows) {
			var entity = new Follower(user, follower);
			repository.persist(entity);
		}
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@GET
	public Response listFollowers(@PathParam("userId") Long userId) {
		var user = userRepository.findById(userId);

		if(null == user) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		var list = repository.findByUser(userId);
		var followerList = list.stream()
				.map(FollowerResponse::new)
				.collect(Collectors.toList());

		FollowersPerUserResponse responseObject = new FollowersPerUserResponse(followerList.size(), followerList);

		return Response.ok(responseObject).build();
	}

	@DELETE
	@Transactional
	public Response unfollowUser(@PathParam("userId")Long userId, @QueryParam("followerId") Long followerId) {
		var user = userRepository.findById(userId);

		if(null == user) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		var follower = userRepository.findById(followerId);
		if(null == follower) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		repository.deleteByFollowerAndUser(followerId, userId);
		return Response.status(Response.Status.NO_CONTENT).build();
	}
}
