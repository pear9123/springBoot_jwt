package com.jwt.service;

import java.util.List;

import com.jwt.domain.Role;
import com.jwt.domain.User;

public interface UserService {

	User saveUser(User user);
	Role saveRole(Role role);
	void addRoleToUser(String username, String roleName);
	User getUser(String username);
	List<User> getUsers();
}
