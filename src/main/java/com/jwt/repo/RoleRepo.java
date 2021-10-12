package com.jwt.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwt.domain.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

	Role findByname(String name);
}