package com.jwt.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jwt.domain.Role;
import com.jwt.domain.User;
import com.jwt.repo.RoleRepo;
import com.jwt.repo.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserRepo userRepo;
	private final RoleRepo roleRepo;

	public User saveUser(User user) {
		log.info("Saving new user {} to the database", user.getName());
		return userRepo.save(user);
	}

	public Role saveRole(Role role) {
		log.info("Saving new role {} to the database", role.getName());
		return roleRepo.save(role);
	}

	public void addRoleToUser(String username, String roleName) {
		log.info("Adding role {} to user {}", roleName, username);
		User user = userRepo.findByUsername(username);
		Role role = roleRepo.findByname(roleName);
		user.getRoles().add(role);
	}

	public User getUser(String username) {
		log.info("Fetching user user {}", username);
		return userRepo.findByUsername(username);
	}

	public List<User> getUsers() {
		log.info("Fetching all user");
		return userRepo.findAll();
	}

}
