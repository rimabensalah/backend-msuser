package com.firstservice.userservice.service;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import com.firstservice.userservice.domain.Compte;
import com.firstservice.userservice.payload.request.SearchRequest;
import com.firstservice.userservice.repository.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.firstservice.userservice.domain.Role;
import com.firstservice.userservice.domain.RoleName;
import com.firstservice.userservice.domain.Utilisateur;
import com.firstservice.userservice.repository.RoleRepository;
import com.firstservice.userservice.repository.UserRepository;
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private CompteRepository compteRepo;
	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private EntityManager entityManager;



	@Override
	public Utilisateur saveUser(Utilisateur user) {
		// TODO Auto-generated method stub
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		return userRepo.save(user);
	}

	@Override
	public Role saveRole(Role role) {
		// TODO Auto-generated method stub
		return roleRepo.save(role);
	}

	@Override
	public void addRoleToUser(String username, RoleName roleName) {
		// TODO Auto-generated method stub
		Utilisateur user =userRepo.findByUsername(username);
		Role role =roleRepo.findByName(roleName).get();
		user.getRoles().add(role);
		
		
	}

	@Override
	public Utilisateur getUser(String username) {
		// TODO Auto-generated method stub
		return userRepo.findByUsername(username);
	}

	@Override
	public List<Utilisateur> getUsers() {
		// TODO Auto-generated method stub
		return userRepo.findAll();
	}
	
	@Override
	public List<Role> getRoles() {
		// TODO Auto-generated method stub
		return roleRepo.findAll();
	}

	@Override
	public List<Compte> getComptes() {
		return compteRepo.findAll();
	}

	@Override
	public List<Utilisateur> search(String keyword, SearchRequest searchRequest) {
		List<String> columns;
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Utilisateur> q = cb.createQuery(Utilisateur.class);
		Root<Utilisateur> utilisateurRootRoot = q.from(Utilisateur.class);
		List<Predicate> predicates = new ArrayList<>();

		columns = searchRequest.getColumns();
		for (int i = 0; i < columns.size(); i++)  {
			predicates.add(cb.or(cb.like(utilisateurRootRoot.get(String.valueOf(columns.get(i))).as(String.class), "%" + keyword + "%")));

		}
		q.select(utilisateurRootRoot).where(cb.or(predicates.toArray(new Predicate[predicates.size()])));
	    List<Utilisateur> resultList = entityManager.createQuery(q).getResultList();


		return resultList;
	}


	public Long totalUser(){
		Long total= userRepo.findAll().stream().count();
		Long totaluser=compteRepo.findAll().stream().count();
		//Long totalCompte=compteRepo.getNumberOfCompteAddedThisMonth();
		return  totaluser;

	}
	public Long totalCompteAdded(){
		Long totalCompte=compteRepo.getNumberOfCompteAddedThisMonth();
		return  totalCompte;
	}
	public Map<String,Object> displayuserStats(){
		Map<String, Object> stats = new HashMap<>();
		long numUsers= totalUser();
		long numUserAdded=totalCompteAdded();
		stats.put("numUsers",numUsers);
		stats.put("numUserAdded",numUserAdded);

		System.out.println("Statistiques d'utilisation pour l'admin " +  ":");
		System.out.println("- Nombre de users inscrits : " + numUsers);
		System.out.println("- Nombre de users added this month : " + numUserAdded);
		System.out.println();

		return stats;
	}




}
