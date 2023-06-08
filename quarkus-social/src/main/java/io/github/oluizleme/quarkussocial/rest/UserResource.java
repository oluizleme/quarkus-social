package io.github.oluizleme.quarkussocial.rest;

import io.github.oluizleme.quarkussocial.domain.model.User;
import io.github.oluizleme.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	@POST
	@Transactional
	public Response createUser(CreateUserRequest userRequest){
		User user = new User();
		user.setName(userRequest.getName());
		user.setAge(userRequest.getAge());
		user.persist();
		return Response.ok(user).build();
	}

	@GET
	public Response getAllUsers(){
		PanacheQuery<User> query = User.findAll();
		return Response.ok(query.list()).build();
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public Response deleteUser(@PathParam("id") Long id) {
		User user = User.findById(id);
		if (null == user) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		user.delete();
		return Response.ok().build();
	}

	@PUT
	@Path("{id}")
	@Transactional
	public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData){
		User user = User.findById(id);
		if (null == user) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		user.setName(userData.getName());
		user.setAge(userData.getAge());
		user.persist();
		return Response.ok().build();
	}
}
