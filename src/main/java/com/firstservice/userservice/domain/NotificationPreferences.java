package com.firstservice.userservice.domain;


import javax.persistence.*;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean emailNotificationsEnabled=true;

    @OneToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    public NotificationPreferences() {
    }
    public NotificationPreferences( Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public NotificationPreferences(boolean emailNotificationsEnabled, Utilisateur utilisateur) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.utilisateur = utilisateur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public void setEmailNotificationsEnabled(boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}

