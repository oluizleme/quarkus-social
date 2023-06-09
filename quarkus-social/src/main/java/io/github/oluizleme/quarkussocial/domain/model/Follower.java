package io.github.oluizleme.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "followers")
@Getter
@Setter
public class Follower {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "follower_id")
	private User follower;

	private Follower() {

	}

	public Follower(User user, User follower) {
		this.user = user;
		this.follower = follower;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Follower follower1 = (Follower) o;
		return Objects.equals(id, follower1.id) && Objects.equals(user, follower1.user) && Objects.equals(follower, follower1.follower);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, user, follower);
	}
}
