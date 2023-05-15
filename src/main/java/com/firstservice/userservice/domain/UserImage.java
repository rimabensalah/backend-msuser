package com.firstservice.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class UserImage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "image_name")
    private String name;
    @Column(name = "image_type")
    private String type;
    @Lob
    private byte[] userImage;
    @Nullable
    @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id")
    @JsonIgnore
    //@MapsId
    private Utilisateur user;

    public UserImage() {
    }

    public UserImage( String name, String type, byte[] userImage) {

        this.name = name;
        this.type = type;
        this.userImage = userImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getUserImage() {
        return userImage;
    }

    public void setUserImage(byte[] userImage) {
        this.userImage = userImage;
    }

    @Nullable
    public Utilisateur getUser() {
        return user;
    }

    public void setUser(@Nullable Utilisateur user) {
        this.user = user;
    }
}
