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
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tags(value=@Tag(name="users", description="Operations related to users"))
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
	@APIResponses(
			value = {
					@APIResponse(
							responseCode = "201",
							description = "User created",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = User.class)
							)
					),
					@APIResponse(
							responseCode = "422",
							description = "Invalid user data",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ResponseError.class)
							)
					)
			}
	)
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
	@APIResponses(
			value = {
					@APIResponse(
							responseCode = "200",
							description = "List of users",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = User.class)
							)
					)
			}
	)
	public Response getAllUsers(){
		PanacheQuery<User> query = repository.findAll(Sort.by("id", Sort.Direction.Ascending));
		return Response.ok(query.list()).build();
	}

	@DELETE
	@Path("{id}")
	@Transactional
	@APIResponses(
			value = {
					@APIResponse(
							responseCode = "204",
							description = "User deleted"
					),
					@APIResponse(
							responseCode = "404",
							description = "User not found"
					)
			}
	)
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
	@APIResponses(
			value = {
					@APIResponse(
							responseCode = "204",
							description = "User updated"
					),
					@APIResponse(
							responseCode = "404",
							description = "User not found"
					),
					@APIResponse(
							responseCode = "422",
							description = "Invalid user data",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = ResponseError.class)
							)
					)
			})
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
