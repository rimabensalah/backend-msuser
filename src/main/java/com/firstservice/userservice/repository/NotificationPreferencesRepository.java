package com.firstservice.userservice.repository;

import com.firstservice.userservice.domain.NotificationPreferences;
import com.firstservice.userservice.domain.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {
    @Query("SELECT np FROM NotificationPreferences np WHERE np.utilisateur = ?1")
    NotificationPreferences findByUtilisateur(Utilisateur utilisateur);
    NotificationPreferences findByUtilisateurId(Long id);

}
