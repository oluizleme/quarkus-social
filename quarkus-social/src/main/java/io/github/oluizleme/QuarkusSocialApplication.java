package io.github.oluizleme;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(
		tags = {
				@org.eclipse.microprofile.openapi.annotations.tags.Tag(name = "users", description = "Operations related to users"),
		},
		info = @Info(
				title = "Quarkus Social API",
				version = "1.0.0",
				description = "Quarkus Social API",
				contact = @Contact(
						name = "Luiz Leme",
						url = "https://github.com/oluizleme",
						email = "o.luizleme@gmail.com"
				),
				license = @License(
						name = "Apache 2.0",
						url = "https://www.apache.org/licenses/LICENSE-2.0.html")
		)
)
public class QuarkusSocialApplication extends Application {
}
