package com.firstservice.userservice.service;

import com.firstservice.userservice.domain.NotificationPreferences;
import com.firstservice.userservice.domain.Utilisateur;
import com.firstservice.userservice.repository.NotificationPreferencesRepository;
import com.firstservice.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationPreferencesService {
    @Autowired
    private NotificationPreferencesRepository notificationPreferencesRepository;
    public boolean areEmailNotificationsEnabled(Utilisateur utilisateur) {
        NotificationPreferences preferences = notificationPreferencesRepository.findByUtilisateur(utilisateur);
        return preferences != null && preferences.isEmailNotificationsEnabled();
    }

   public NotificationPreferences changeNotificationperference(Utilisateur user){
       NotificationPreferences preferences = notificationPreferencesRepository.findByUtilisateur(user);
       if(preferences.isEmailNotificationsEnabled()==true){
           preferences.setEmailNotificationsEnabled(false);
       }else{
           preferences.setEmailNotificationsEnabled(true);
       }

       return  notificationPreferencesRepository.save(preferences);
   }
    public NotificationPreferences updateEmailNotificationsEnabled(Long id, boolean emailNotificationsEnabled) {
        NotificationPreferences notificationPreferences = notificationPreferencesRepository.findByUtilisateurId(id);
        if (notificationPreferences != null) {
            notificationPreferences.setEmailNotificationsEnabled(emailNotificationsEnabled);
            return notificationPreferencesRepository.save(notificationPreferences);
        } else {
            return null;
        }
    }

}











