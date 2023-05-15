package com.firstservice.userservice.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.firstservice.userservice.domain.Utilisateur;
import com.firstservice.userservice.repository.UserRepository;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	  @Autowired
	  UserRepository userRepository;

	  @Override
   @Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Utilisateur user =userRepository.findByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("user not found in the database");
			
		
		}

			    return UserDetailsImpl.build(user);
	}

}
