package io.github.oluizleme.quarkussocial.rest;

import com.google.gson.Gson;
import io.github.oluizleme.quarkussocial.rest.dto.CreateUserRequest;
import io.github.oluizleme.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {
	@TestHTTPResource("/users")
	URL apiURL;

	@Test
	@DisplayName("Should create an user successfully")
	@Order(1)
	public void createUserTest() {
		var user = new CreateUserRequest();
		user.setName("Fulano");
		user.setAge(30);

		var json = new Gson().toJson(user);

		var response =given()
			.contentType(ContentType.JSON)
			.body(json)
		.when()
				.post(apiURL)
		.then()
				.extract().response();

		assertEquals(201, response.statusCode());
		assertNotNull(response.getBody().jsonPath().getLong("id"));
	}

	@Test
	@DisplayName("Should return error when json is not valid")
	@Order(2)
	public void createUserValidationErrorTest(){
		var user = new CreateUserRequest();
		user.setAge(null);
		user.setName(null);

		var json = new Gson().toJson(user);

		var response = given()
				.contentType(ContentType.JSON)
				.body(json)
			.when()
				.post(apiURL)
			.then()
				.extract().response();

		assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
		assertEquals("Validation Error", response.getBody().jsonPath().getString("message"));

		List<Map<String, String>> errors = response.getBody().jsonPath().getList("errors");
		assertNotNull(errors.get(0).get("message"));
		assertNotNull(errors.get(1).get("message"));

	}

	@Test
	@DisplayName("Should list all users")
	@Order(3)
	public void listAllUsersTest() {
		given().contentType(ContentType.JSON)
				.when()
				.get(apiURL)
			.then()
				.statusCode(200)
				.body("size()", is(1));
	}

}