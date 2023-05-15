package com.firstservice.userservice.api;

import com.firstservice.userservice.domain.Compte;
import com.firstservice.userservice.domain.Utilisateur;
import com.firstservice.userservice.repository.CompteRepository;
import com.firstservice.userservice.repository.UserRepository;
import com.firstservice.userservice.service.CompteServiceImpl;
import com.firstservice.userservice.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000/")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private CompteRepository compteRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    UserServiceImpl userService;


    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public  String adminAccess(){
        return " Admin content ";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Utilisateur>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }



    @GetMapping("/getcomptes")
    public ResponseEntity<List<Compte>> getComptes() {
        List <Compte> allComptes= compteRepo.findAll();
                //.orElseThrow(() -> new RuntimeException("comptes not found!" ));
        return new ResponseEntity<>(allComptes,HttpStatus.OK) ;
    }


    @GetMapping({ "/comptes/{id}", "/users/{id}/comptes" })
    public ResponseEntity<Compte> getComptesById(@PathVariable(value = "id") Long id) {
        Compte comptes = compteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found compte with id = " + id));

        return new ResponseEntity<>(comptes, HttpStatus.OK);
    }

    @GetMapping({"/getuser/{id}"})
    public ResponseEntity<Utilisateur> getUserById(@PathVariable(value = "id") Long id) {
        Utilisateur user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found user with id = " + id));

        return new ResponseEntity<>(user, HttpStatus.OK);
    }


}
