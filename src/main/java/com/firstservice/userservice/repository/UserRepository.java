package com.firstservice.userservice.repository;

import com.firstservice.userservice.domain.Compte;
import com.firstservice.userservice.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import com.firstservice.userservice.domain.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserRepository extends JpaRepository<Utilisateur, Long>, JpaSpecificationExecutor<Utilisateur> {
	Utilisateur findByUsername(String username);
	
	Boolean existsByUsername(String username);
	Boolean existsByEmail (String email);

	Page<Utilisateur> findByUsername(String username, Pageable pageable);
	//Page<Object[]> findByStatus(Status status, Pageable pageable);
	List<Utilisateur> findByUsername(String username, Sort sort);

	@Query("select u from Utilisateur u where u.userCompte.status = ?1")
	List<Utilisateur> findAllUsersByStatus(@Param("status") Status status);
	@Query("select u from Utilisateur u where u.userCompte.status = ?1")
	Page<Utilisateur> findUsersByStatus(@Param("status") Status status, Pageable pageable);
	Page<Utilisateur>  findByUsernameContainingIgnoreCase(String username,Pageable pageable);
	@Query("select u from Utilisateur u where u.userCompte.status = ?1")
	Page<Utilisateur>  findByUsernameContainingIgnoreCaseAndStatus(@Param("status") Status status,String username,Pageable pageable);
}
