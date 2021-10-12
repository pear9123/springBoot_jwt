package com.jwt.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwt.domain.User;

public interface UserRepo extends JpaRepository<User, Long> {

	User findByUsername(String username);
}
