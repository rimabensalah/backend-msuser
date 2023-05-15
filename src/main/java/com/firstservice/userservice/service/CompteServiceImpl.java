package com.firstservice.userservice.service;

import com.firstservice.userservice.domain.Compte;
import com.firstservice.userservice.domain.Utilisateur;
import com.firstservice.userservice.repository.CompteRepository;
import com.firstservice.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompteServiceImpl implements CompteService {
    @Autowired
    private CompteRepository compteRepo;
    @Autowired
    private UserRepository userRepo;


    @Override
    public List<Compte> getComptes() {

        return compteRepo.findAll();
    }

    public Compte saveCompte(Compte compte){
        return this.compteRepo.save(compte);
    }
}
