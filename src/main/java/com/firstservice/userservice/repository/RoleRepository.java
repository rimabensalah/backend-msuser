package com.firstservice.userservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.firstservice.userservice.domain.Role;
import com.firstservice.userservice.domain.RoleName;




public interface RoleRepository extends JpaRepository<Role,Long>{
	Optional<Role> findByName(RoleName name);
	List<Role> findAll();

}
