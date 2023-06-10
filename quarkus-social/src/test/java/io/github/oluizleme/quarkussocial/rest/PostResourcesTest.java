package io.github.oluizleme.quarkussocial.rest;

import com.google.gson.Gson;
import io.github.oluizleme.quarkussocial.domain.model.Follower;
import io.github.oluizleme.quarkussocial.domain.model.Post;
import io.github.oluizleme.quarkussocial.domain.model.User;
import io.github.oluizleme.quarkussocial.domain.repository.FollowerRepository;
import io.github.oluizleme.quarkussocial.domain.repository.PostRepository;
import io.github.oluizleme.quarkussocial.domain.repository.UserRepository;
import io.github.oluizleme.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestHTTPEndpoint(PostResources.class)
class PostResourcesTest {

	@Inject
	UserRepository userRepository;
	@Inject
	FollowerRepository followerRepository;
	@Inject
	PostRepository postRepository;
	Long userId;
	Long userNotFollowerId;
	Long userFollowerId;

	@BeforeEach
	@Transactional
	public void setUp() {
		//usuario padrao dos testes
		var user = new User();
		user.setAge(30);
		user.setName("Fulano");
		userRepository.persist(user);
		userId = user.getId();

		//criada a postagem para o usuario
		Post post = new Post(user,"My first post");
		postRepository.persist(post);

		//usuario que nao segue ninguem
		var userNotFollower = new User();
		userNotFollower.setAge(33);
		userNotFollower.setName("Ciclano");
		userRepository.persist(userNotFollower);
		userNotFollowerId = userNotFollower.getId();

		//usuario seguidor
		var userFollower = new User();
		userFollower.setAge(33);
		userFollower.setName("Beltrano");
		userRepository.persist(userFollower);
		userFollowerId = userFollower.getId();

		Follower follower = new Follower(user, userFollower);
		followerRepository.persist(follower);

	}

	@Test
	@DisplayName("Should create a post for an user")
	public void createPostTest() {
		var postRequest = new CreatePostRequest();
		postRequest.setText("My first post");
		var json = new Gson().toJson(postRequest);

		given()
				.contentType(ContentType.JSON)
				.body(json)
				.pathParams("userId", userId)
			.when()
				.post()
			.then()
				.statusCode(201);
	}

	@Test
	@DisplayName("Should return 404 when trying to create a post for an user that does not exist")
	public void postForAnInexistentUserTest() {
		var postRequest = new CreatePostRequest();
		postRequest.setText("My first post");
		var json = new Gson().toJson(postRequest);
		var inexistentUserId = 999L;
		given()
				.contentType(ContentType.JSON)
				.body(json)
				.pathParams("userId", inexistentUserId)
				.when()
				.post()
				.then()
				.statusCode(404);
	}

	@Test
	@DisplayName("Should return 404 when user does not exist")
	public void listPostUserNotFoundTest(){
		var inexistentUserId = 999L;

		given()
				.pathParams("userId", inexistentUserId)
			.when()
				.get()
			.then()
				.statusCode(404);
	}

	@Test
	@DisplayName("Should return 400 when followerId reader is not present")
	public void listPostFollowerHeaderNotSendTest(){
		given()
			.pathParams("userId", userId)
		.when()
			.get()
		.then()
			.statusCode(400)
			.body(is("You forgot the header followerId"));
	}

	@Test
	@DisplayName("Should return 404 when follower does not exist")
	public void listPostFollowerNotFoundTest(){
		var inexistentFollowerId = 999L;
		given()
			.pathParams("userId", userId)
			.header("followerId", inexistentFollowerId)
		.when()
			.get()
		.then()
			.statusCode(400)
			.body(is("Inexistent followerId"));
	}

	@Test
	@DisplayName("Should return 403 when follower is not a follower of the user")
	public void listPostNotAFollowerTest(){
		given()
			.pathParams("userId", userId)
			.header("followerId", userNotFollowerId)
		.when()
			.get()
		.then()
			.statusCode(403)
			.body(is("You can't see these posts"));
	}

	@Test
	@DisplayName("Should return posts")
	public void listPostTest(){
		given()
			.pathParams("userId", userId)
			.header("followerId", userFollowerId)
		.when()
			.get()
		.then()
			.statusCode(200)
			.body("size()",is(1));
	}
}