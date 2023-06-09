package io.github.oluizleme.quarkussocial.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "posts")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "post_text")
	private String text;
	@Column(name = "dateTime")
	private LocalDateTime dateTime;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;


	private Post() {
	}

	public Post(User user, String text) {
		this.user = user;
		this.text = text;
		this.dateTime = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Post post = (Post) o;
		return Objects.equals(id, post.id) && Objects.equals(text, post.text) && Objects.equals(dateTime, post.dateTime) && Objects.equals(user, post.user);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, text, dateTime, user);
	}
}
