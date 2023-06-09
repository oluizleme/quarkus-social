package io.github.oluizleme.quarkussocial.rest;

import io.github.oluizleme.quarkussocial.domain.model.User;
import io.github.oluizleme.quarkussocial.domain.repository.UserRepository;
import io.github.oluizleme.quarkussocial.rest.dto.CreateUserRequest;
import io.github.oluizleme.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private UserRepository repository;
	private Validator validator;

	@Inject
	public UserResource(UserRepository repository, Validator validator){
		this.repository = repository;
		this.validator = validator;
	}

	@POST
	@Transactional
	public Response createUser(CreateUserRequest userRequest){

		Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
		if (!violations.isEmpty()) {
			return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
		}

		User user = new User();
		user.setName(userRequest.getName());
		user.setAge(userRequest.getAge());

		repository.persist(user);

		return Response.status(Response.Status.CREATED.getStatusCode())
				.entity(user)
				.build();
	}

	@GET
	public Response getAllUsers(){
		PanacheQuery<User> query = repository.findAll(Sort.by("id", Sort.Direction.Ascending));
		return Response.ok(query.list()).build();
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public Response deleteUser(@PathParam("id") Long id) {
		User user = repository.findById(id);
		if (null == user) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		repository.delete(user);
		return Response.noContent().build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData){
		User user = repository.findById(id);
		if (null == user) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userData);
		if (!violations.isEmpty()) {
			return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
		}

		user.setName(userData.getName());
		user.setAge(userData.getAge());
		return Response.noContent().build();
	}
}
