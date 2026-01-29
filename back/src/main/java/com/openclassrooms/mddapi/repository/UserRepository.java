package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds a user by email.
   */
  Optional<User> findByEmail(String email);

  /**
   * Finds a user by username.
   */
  Optional<User> findByUsername(String username);

  /**
   * Finds a user by email or username (for login).
   */
  Optional<User> findByEmailOrUsername(String email, String username);

  /**
   * Checks if an email already exists.
   */
  boolean existsByEmail(String email);

  /**
   * Checks if a username already exists.
   */
  boolean existsByUsername(String username);
}
