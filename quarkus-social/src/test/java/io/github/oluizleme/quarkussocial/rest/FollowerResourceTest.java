package io.github.oluizleme.quarkussocial.rest;

import com.google.gson.Gson;
import io.github.oluizleme.quarkussocial.domain.model.Follower;
import io.github.oluizleme.quarkussocial.domain.model.User;
import io.github.oluizleme.quarkussocial.domain.repository.FollowerRepository;
import io.github.oluizleme.quarkussocial.domain.repository.UserRepository;
import io.github.oluizleme.quarkussocial.rest.dto.CreateFollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

	@Inject
	UserRepository userRepository;
	@Inject
	FollowerRepository followerRepository;
	private Long userId;
	private Long userFollowerId;


	@BeforeEach
	@Transactional
	void setUp() {
		//usuario padrao dos testes
		var user = new User();
		user.setAge(30);
		user.setName("Fulano");
		userRepository.persist(user);
		userId = user.getId();
		//usuario seguidor
		var userFollower = new User();
		userFollower.setAge(33);
		userFollower.setName("Beltrano");
		userRepository.persist(userFollower);
		userFollowerId = userFollower.getId();
		//cria um follower
		var followerEntity = new Follower(user, userFollower);
		followerRepository.persist(followerEntity);
	}

	@Test
	@DisplayName("Should return 409 when folloewrId is equals to userId")
	public void sameUserAsFollowerTest() {
 		var request = new CreateFollowerRequest();
		 request.setFollowerId(userId);

		 var json = new Gson().toJson(request);

		given()
				.contentType(ContentType.JSON)
				.body(json)
				.pathParams("userId", userId)
			.when()
				.put()
			.then()
				.statusCode(Response.Status.CONFLICT.getStatusCode())
				.body(is("You can't follow yourself"));
	}

	@Test
	@DisplayName("Should return 404 on follow a user when userId does not exist")
	public void userNotFoundWhenTryingToFollowTest() {
		var request = new CreateFollowerRequest();
		request.setFollowerId(userId);
		var inexistentUserId = 999L;

		var json = new Gson().toJson(request);

		given()
			.contentType(ContentType.JSON)
			.body(json)
			.pathParams("userId", inexistentUserId)
		.when()
			.put()
		.then()
			.statusCode(Response.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	@DisplayName("Should follow an user")
	public void followAnUserTest() {
		var request = new CreateFollowerRequest();
		request.setFollowerId(userFollowerId);

		var json = new Gson().toJson(request);

		given()
			.contentType(ContentType.JSON)
			.body(json)
			.pathParams("userId", userId)
		.when()
			.put()
		.then()
			.statusCode(Response.Status.NO_CONTENT.getStatusCode());
	}

	@Test
	@DisplayName("Should return 404 on list user followers and userId does not exist")
	public void userNotFoundWhenListingFollowersTest() {
		var request = new CreateFollowerRequest();
		request.setFollowerId(userId);
		var inexistentUserId = 999L;

		var json = new Gson().toJson(request);

		given()
			.contentType(ContentType.JSON)
			.pathParams("userId", inexistentUserId)
		.when()
			.get()
		.then()
			.statusCode(Response.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	@DisplayName("Should list a user's followers")
	public void listFollowerTest() {

		var response =
		given()
			.contentType(ContentType.JSON)
			.pathParams("userId", userId)
		.when()
			.get()
		.then()
			. extract().response();

		var followersCount = response.body().jsonPath().get("followerCount");
		var followersContent = response.body().jsonPath().getList("content");

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
		assertEquals(1, followersCount);
		assertEquals(1, followersContent.size());
	}

	@Test
	@DisplayName("Should return 404 on unfollow user and userId does not exist")
	public void userNotFoundWhenUnfollowingAnUserTest() {
		var inexistentUserId = 999L;

		given()
			.pathParams("userId", inexistentUserId)
			.queryParam("followerId", userFollowerId)
		.when()
			.delete()
		.then()
			.statusCode(Response.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	@DisplayName("Should unfollow an user")
	public void unfollowUserTest() {
		given()
			.pathParams("userId", userId)
			.queryParam("followerId", userFollowerId)
		.when()
			.delete()
		.then()
			.statusCode(Response.Status.NO_CONTENT.getStatusCode());
	}

}