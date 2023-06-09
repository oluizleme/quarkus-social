package io.github.oluizleme.quarkussocial.domain.repository;

import io.github.oluizleme.quarkussocial.domain.model.Follower;
import io.github.oluizleme.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

	public boolean follows(User follower, User user) {
		var params = Parameters.with("follower", follower)
												.and("user", user)
												.map();
		PanacheQuery<Follower> query = find("follower =:follower and user =:user", params);
		Optional<Follower> result = query.firstResultOptional();

		return result.isPresent();
	}
}
