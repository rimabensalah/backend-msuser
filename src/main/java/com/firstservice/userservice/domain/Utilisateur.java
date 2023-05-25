package com.firstservice.userservice.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Size;


@Entity
public class Utilisateur implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Size(max = 20)
    private String username;
    @Size(max = 50)
    private String email;
    @Size(max = 120)
    private String password;
    private String resetToken;
    //@ManyToMany
    //@CascadeOnDelete (fetch = FetchType.EAGER)

   // private Collection<Role> roles = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    //@JsonBackReference
    //@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
	@OneToOne(mappedBy = "user")
    private Compte userCompte;

    @OneToOne(mappedBy = "user")
    private UserImage userImage;

    public UserImage getUserImage() {
        return userImage;
    }

    public void setUserImage(UserImage userImage) {
        this.userImage = userImage;
    }

    public Compte getUserCompte() {
        return userCompte;
    }

    public void setUserCompte(Compte userCompte) {
        this.userCompte = userCompte;
    }

    public Utilisateur() {
        super();
    }


    public Utilisateur(Long id, @Size(max = 20) String username, @Size(max = 50) String email,
                       @Size(max = 120) String password) {
        super();
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Utilisateur(@Size(max = 20) String username, @Size(max = 50) String email,
                       @Size(max = 120) String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void removeRole(long roleId){
        Role role=this.roles.stream().filter(u ->u.getId() == roleId).findFirst().orElse(null);
        if(role != null){
            this.roles.remove(role);
            role.getUsers().remove(this);
        }
    }
}
