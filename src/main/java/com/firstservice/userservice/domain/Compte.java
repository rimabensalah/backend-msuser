package com.firstservice.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Compte implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compte_id")
    private Long id;
    @Temporal(TemporalType.DATE)
    @Column(name = "date_de_naiss")
    private Date dateDeNaiss;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Profession profession;
    @Column(name = "phone_number")
    @Size(max = 15)
    private String phoneNumber;
    @Size(max = 100)
    private String address;
    @Size(max = 100)
    private String city;
    @Size(max = 100)
    private String country;

    @Nullable
    @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id")
    @JsonIgnore
    //@MapsId
    private Utilisateur user;

    /*@Lob
    private byte[] userImage;*/
    @CreatedDate
    @Column(name="created_date")
    private String createdDate;

    @LastModifiedDate
    @Column(name="modified_date")
    private String modifiedDate;

    @Column(name = "compte_status")
    private  Status status ;

    //les getters && les setters

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Compte() {
    }

    public Compte(Long id, Date dateDeNaiss, @Size(max = 100) String country,
                  Profession profession, @Size(max = 15) String phoneNumber,
                  @Size(max = 100) String address, @Size(max = 100) String city, Utilisateur user) {
        super();
        this.id = id;
        this.dateDeNaiss = dateDeNaiss;
        this.country = country;
        this.profession = profession;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateDeNaiss() {
        return dateDeNaiss;
    }

    public void setDateDeNaiss(Date dateDeNaiss) {
        this.dateDeNaiss = dateDeNaiss;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Utilisateur getUser() {
        return user;
    }

    public void setUser(Utilisateur user) {
        this.user = user;
    }


    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
