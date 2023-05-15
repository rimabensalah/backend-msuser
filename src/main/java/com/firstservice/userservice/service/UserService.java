package com.firstservice.userservice.service;

import java.util.List;

import com.firstservice.userservice.domain.Compte;
import com.firstservice.userservice.domain.Role;
import com.firstservice.userservice.domain.RoleName;
import com.firstservice.userservice.domain.Utilisateur;
import com.firstservice.userservice.payload.request.SearchRequest;

public interface UserService {
	
	Utilisateur saveUser(Utilisateur user);
	Role saveRole (Role role);
	void addRoleToUser(String username,RoleName roleName);
	Utilisateur getUser(String username);
	List<Utilisateur> getUsers();
    List<Role> getRoles();
	List<Compte> getComptes();

	List<Utilisateur> search(String keyword , SearchRequest searchRequest);

}
