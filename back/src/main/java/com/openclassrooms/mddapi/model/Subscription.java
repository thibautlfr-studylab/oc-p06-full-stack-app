package com.openclassrooms.mddapi.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entity representing a user's subscription to a topic.
 */
@Entity
@Table(name = "subscriptions", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "user_id", "topic_id" }, name = "unique_subscription")
})
public class Subscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subscription_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topic_id", nullable = false)
	private Topic topic;

	@CreationTimestamp
	@Column(name = "subscribed_at", updatable = false)
	private LocalDateTime subscribedAt;

	public Subscription() {
	}

	public Subscription(User user, Topic topic) {
		this.user = user;
		this.topic = topic;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public LocalDateTime getSubscribedAt() {
		return subscribedAt;
	}

	public void setSubscribedAt(LocalDateTime subscribedAt) {
		this.subscribedAt = subscribedAt;
	}
}
