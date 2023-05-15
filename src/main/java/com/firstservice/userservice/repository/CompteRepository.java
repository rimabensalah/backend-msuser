package com.firstservice.userservice.repository;

import com.firstservice.userservice.domain.Compte;
import com.firstservice.userservice.domain.Profession;
import com.firstservice.userservice.domain.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompteRepository extends JpaRepository<Compte,Long> {

    Compte findByStatus(Status status);
    @Query("SELECT COUNT(c) FROM Compte c WHERE MONTH(c.createdDate) = MONTH(CURRENT_DATE) AND YEAR(c.createdDate) = YEAR(CURRENT_DATE)")
    Long getNumberOfCompteAddedThisMonth();



}
