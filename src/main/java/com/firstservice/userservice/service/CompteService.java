package com.firstservice.userservice.service;

import com.firstservice.userservice.domain.Compte;
import com.firstservice.userservice.domain.Utilisateur;

import java.util.List;

public interface CompteService {

    List<Compte> getComptes();

    Compte saveCompte(Compte compte);
}
