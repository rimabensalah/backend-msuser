package com.firstservice.userservice.api;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstservice.userservice.domain.*;
import com.firstservice.userservice.payload.request.LoginRequest;
import com.firstservice.userservice.payload.request.SearchRequest;
import com.firstservice.userservice.payload.response.FileResponse;
import com.firstservice.userservice.payload.response.JwtResponse;
import com.firstservice.userservice.repository.*;
import com.firstservice.userservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.firstservice.userservice.payload.request.SignupRequest;
import com.firstservice.userservice.payload.response.MessageResponse;
import com.firstservice.userservice.security.jwt.JwtUtils;

@CrossOrigin("http://localhost:3000/")
@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CompteRepository compteRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private FileRepository fileRepo;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    CompteService compteService;
    @Autowired
    FileServiceImpl fileService;
    @Autowired
    NotificationPreferencesService notificationPreferencesService;
    @Autowired
    private NotificationPreferencesRepository notificationPreferencesRepository;

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }
    @RequestMapping(value = "/search", method = RequestMethod.PUT)

    public List<Utilisateur> search(@RequestParam String keyword,
                                    @RequestBody(required = false) SearchRequest searchRequest) {

        return userService.search(keyword, searchRequest);

    }



    @GetMapping("/users")
    public ResponseEntity<List<Utilisateur>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }
    //pagination
    @GetMapping("/allusers")
    public ResponseEntity<Map<String,Object>> getAllUser(
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size

    ){
        try {
            List<Utilisateur> users= new ArrayList<Utilisateur>();
            Pageable paging= PageRequest.of(page, size);
            Page<Utilisateur> pageUsers;

            if(username==null)
                pageUsers=userRepo.findAll(paging);
            else

                pageUsers=userRepo.findByUsername(username,paging);
            users=pageUsers.getContent();
            Map<String,Object> response= new HashMap<>();
            response.put("users",users);
            response.put("currentPage",pageUsers.getNumber());
            response.put("totalItems",pageUsers.getTotalElements());
            response.put("totalPages",pageUsers.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }


    }



    //add user+compte+userfile
    @PostMapping("/register")
    public ResponseEntity<?> registerWithImage(@RequestParam("data") String signUpRequestString,
                                               @RequestParam("compte") String compteString,
                                               @RequestParam("file") MultipartFile file) {

        try {

            SignupRequest signUpRequest = new ObjectMapper().readValue(signUpRequestString, SignupRequest.class);

            if (signUpRequest == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: les champs sont vides !"));
            }
            if (userRepo.existsByUsername(signUpRequest.getUsername())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
            }

            if (userRepo.existsByEmail(signUpRequest.getEmail())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
            }

            // Create new user's account
            Utilisateur user = new Utilisateur(signUpRequest.getUsername(), signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()));


            Set<String> strRoles = signUpRequest.getRole();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null) {
                Role userRole = roleRepo.findByName(RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "admin":
                            Role adminRole = roleRepo.findByName(RoleName.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(adminRole);
                            break;

                        default:
                            Role userRole = roleRepo.findByName(RoleName.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                    }
                });
            }

            user.setRoles(roles);
            userRepo.save(user);

            Compte compte = new ObjectMapper().readValue(compteString, Compte.class);
            //userimage upload
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            UserImage FileDB = new UserImage(fileName, file.getContentType(), file.getBytes());
            FileDB.setUser(user);
            fileRepo.save(FileDB);
            //compte.setUserImage(file.getBytes());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();


            compte.setCreatedDate(dateFormat.format(date));
            compte.setStatus(Status.activated);
            compte.setUser(user);
            Compte newCompte = this.compteService.saveCompte(compte);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }
    //upload image user
    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            fileService.store(file);

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }
    }
    //updatecompte with image profile
    //iduser/idCompte/idImage
    @PutMapping("/updateCompteImage/{id}/{idCompte}/{idImage}")
    public ResponseEntity<?> updtePhotoProfile(@PathVariable("id") long id,
                                               @PathVariable("idCompte") long idCompte,
                                               @PathVariable("idImage") long idImage,
                                               //@RequestParam("data") String userRequestString,
                                               @RequestParam("compte") String compteRequestString,
                                               @RequestParam("file") MultipartFile file) {




            try {
                //Utilisateur signUpRequest = new ObjectMapper().readValue(userRequestString, Utilisateur.class);
                //SignupRequest signUpRequest = new ObjectMapper().readValue(signUpRequestString, SignupRequest.class);
                Optional <Utilisateur> userData=userRepo.findById(id);
                Optional<Compte> compteData = compteRepo.findById(idCompte);
                Compte compteRequest = new ObjectMapper().readValue(compteRequestString, Compte.class);
                String message="";
                Optional<UserImage> imageData = fileRepo.findById(idImage);
                if(userData.isPresent()){
                    Utilisateur updateUser=userData.get();
                    //updateUser.setUsername(signUpRequest.getUsername());
                    //updateUser.setEmail(signUpRequest.getEmail());

                    //updateUser.setPassword(encoder.encode(signUpRequest.getPassword()));
                    //userRepo.save(updateUser);
                if(imageData.isPresent()){
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    UserImage updateimage=imageData.get();
                    updateimage.setName(fileName);
                    updateimage.setType(file.getContentType());
                    updateimage.setUserImage(file.getBytes());
                    updateimage.setUser(updateUser);
                    fileRepo.save(updateimage);
                    message = "updated the file successfully: " + file.getOriginalFilename();
                    if(compteData.isPresent()){
                        Compte updateCompte=compteData.get();

                        updateCompte.setDateDeNaiss(compteRequest.getDateDeNaiss());
                        updateCompte.setCity(compteRequest.getCity());
                        updateCompte.setAddress(compteRequest.getAddress());
                        updateCompte.setCountry(compteRequest.getCountry());
                        updateCompte.setPhoneNumber(compteRequest.getPhoneNumber());
                        updateCompte.setProfession(compteRequest.getProfession());
                        updateCompte.setCreatedDate(compteRequest.getCreatedDate());
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        updateCompte.setModifiedDate(dateFormat.format(date));

                        updateCompte.setUser(updateUser);


                        compteRepo.save(updateCompte);
                    }

                }}
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
            } catch (IOException e) {

                throw new RuntimeException(e);
            }





    }
    //update comptes without image
    @PutMapping("/updateUserProfile/{id}/{idCompte}")
    public ResponseEntity<?> updateUserProfile(@PathVariable("id") long id,
                                               @PathVariable("idCompte") long idCompte,
                                           //@RequestParam("data") String userRequestString,
                                           @RequestParam("compte") String compteRequestString
                                             ) {

        try{
            //SignupRequest signUpRequest = new ObjectMapper().readValue(signUpRequestString, SignupRequest.class);
            //Utilisateur signUpRequest = new ObjectMapper().readValue(userRequestString, Utilisateur.class);

            Optional <Utilisateur> userData=userRepo.findById(id);
            Optional<Compte> compteData = compteRepo.findById(idCompte);
            Compte compteRequest = new ObjectMapper().readValue(compteRequestString, Compte.class);

            if(userData.isPresent()){
              // Utilisateur updateUser=userData.get();
                //updateUser.setUsername(signUpRequest.getUsername());
                //updateUser.setEmail(signUpRequest.getEmail());
                //updateUser.setPassword(signUpRequest.getPassword());
               // userRepo.save(updateUser);
                if(compteData.isPresent()){
                    Compte updateCompte=compteData.get();

                    updateCompte.setDateDeNaiss(compteRequest.getDateDeNaiss());
                    updateCompte.setCity(compteRequest.getCity());
                    updateCompte.setAddress(compteRequest.getAddress());
                    updateCompte.setCountry(compteRequest.getCountry());
                    updateCompte.setPhoneNumber(compteRequest.getPhoneNumber());
                    updateCompte.setProfession(compteRequest.getProfession());
                    updateCompte.setCreatedDate(compteRequest.getCreatedDate());
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    updateCompte.setModifiedDate(dateFormat.format(date));

                    updateCompte.setUser(userData.get());

                    compteRepo.save(updateCompte);
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);

        }
        return ResponseEntity.ok(new MessageResponse("User profile updated successfully!"));
    }

    @PutMapping("/updateCompte/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody Compte compte) {
        Optional<Compte> compteData = compteRepo.findById(id);

        if (compteData.isPresent()) {
            Compte updateCompte=compteData.get();
            updateCompte.setDateDeNaiss(compte.getDateDeNaiss());
            updateCompte.setCity(compte.getCity());
            updateCompte.setAddress(compte.getAddress());
            updateCompte.setCountry(compte.getCountry());
            updateCompte.setProfession(compte.getProfession());
            updateCompte.setCreatedDate(compte.getModifiedDate());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            compte.setModifiedDate(dateFormat.format(date));

            return new ResponseEntity<>(compteRepo.save(compte), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    //get user by id
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserByid(@PathVariable(value = "id") Long id) {
        Utilisateur user = userRepo.findById(id).get();



        return new ResponseEntity<>(user, HttpStatus.OK);
    }



    //affichage user profile image
    @GetMapping("/files")
    public ResponseEntity<List<FileResponse>> getListFiles() {
        List<FileResponse> files = fileService.getAllFiles().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/auth/files/")
                    .path(dbFile.getId().toString())
                    .toUriString();

            return new FileResponse(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getUserImage().length);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }
    //get file par id

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        UserImage fileDB = fileService.getFile(id);
        byte[] base64encodedData = Base64.getEncoder().encode(fileDB.getUserImage());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(base64encodedData);
    }

    //simple resgister
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (signUpRequest == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: les champs sont vides !"));
        }
        if (userRepo.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        Utilisateur user = new Utilisateur(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));


        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepo.findByName(RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            // .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepo.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;

                    default:
                        Role userRole = roleRepo.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepo.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    //create account for admin
    @PostMapping("createaccount/{id}")
    public ResponseEntity<?> createAdminAccount(@PathVariable("id") long id,

                                                @RequestParam("compte") String compteRequestString,
                                                @RequestParam("file") MultipartFile file) {
        try{
            Optional <Utilisateur> userData=userRepo.findById(id);
            Compte compte = new ObjectMapper().readValue(compteRequestString, Compte.class);
            //userimage upload
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            UserImage FileDB = new UserImage(fileName, file.getContentType(), file.getBytes());
            FileDB.setUser(userData.get());
            fileRepo.save(FileDB);
            //compte.setUserImage(file.getBytes());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();


            compte.setCreatedDate(dateFormat.format(date));
            compte.setStatus(Status.activated);
            compte.setUser(userData.get());
            Compte newCompte = this.compteService.saveCompte(compte);
            Utilisateur user=userRepo.findById(id).get();
            NotificationPreferences preferences = notificationPreferencesRepository.findByUtilisateur(user);
            preferences = new NotificationPreferences(user);
            preferences.setEmailNotificationsEnabled(false);

            notificationPreferencesRepository.save(preferences);

        }catch (IOException e) {

        throw new RuntimeException(e);
    }
        return ResponseEntity.ok(new MessageResponse("admin registered successfully!"));
    }

        // login
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

       /* Utilisateur user=userRepo.findById(userDetails.getId()).get();
        NotificationPreferences preferences = notificationPreferencesRepository.findByUtilisateur(user);
        preferences = new NotificationPreferences(user);
        preferences.setEmailNotificationsEnabled(false);

        notificationPreferencesRepository.save(preferences);*/
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }


    @PostMapping("/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/role/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody String username, @RequestBody RoleName roleName) {
        userService.addRoleToUser(username, roleName);
        return ResponseEntity.ok().build();

    }

    //delete role from user
    @DeleteMapping("users/{userId}/role/{roleId}")
    public ResponseEntity<HttpStatus> deleteRoleFromUser(@PathVariable(value = "userId") Long userId, @PathVariable(value = "roleId") Long roleId) {
        Utilisateur user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found Tutorial with id = " + userId));

        user.removeRole(roleId);
        userRepo.save(user);
       // userRepo.deleteById(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //delete user
    @DeleteMapping("deleteUserCompte/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "userId") Long userId) {
        Utilisateur user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found Tutorial with id = " + userId));

         Long idCompte =user.getUserCompte().getId();
         Compte compteDeleted =compteRepo.findById(idCompte).get();
         compteDeleted.setStatus(Status.deactivated);
         compteRepo.save(compteDeleted);
        List<Utilisateur> queryResult = userRepo.findAllUsersByStatus(Status.deactivated);


        return new ResponseEntity<>(queryResult,HttpStatus.OK);
    }
    //find desactivated account
   @GetMapping("/deletedCompte")
    public  ResponseEntity<?> findDeletedCompte(){
        Map<Utilisateur, Long> mappedResult = new HashMap<>();
        List<Utilisateur> queryResult = userRepo.findAllUsersByStatus(Status.deactivated);
       //List<Utilisateur>  userQuery =  queryResult;
        
        return  new ResponseEntity<>(queryResult,HttpStatus.OK);

    }

    //get all roles
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getListRoles() {
        List<Role> roles = userService.getRoles();

        return ResponseEntity.status(HttpStatus.OK).body(roles);
    }
    @GetMapping("/professions")
    public ResponseEntity<?> getListProfession() {
       // Compte compte=new Compte();
        List<Profession> professions=Arrays.asList(Profession.values());
        //assertEquals(EXPECTED_LIST, professions);



        return new ResponseEntity<>(professions,HttpStatus.OK);
    }



    @GetMapping("/getuserbystatus")
    public ResponseEntity<Map<String,Object>> getUserbystatus(
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size

    ){
        try {
            List<Utilisateur> users= new ArrayList<Utilisateur>();
            Pageable paging= PageRequest.of(page, size);
            Page<Utilisateur> pageUsers;

            if(status==null)
                pageUsers=userRepo.findAll(paging);
            else

                pageUsers=userRepo.findUsersByStatus(status,paging);
            users=pageUsers.getContent();
            Map<String,Object> response= new HashMap<>();
            response.put("users",users);
            response.put("currentPage",pageUsers.getNumber());
            response.put("totalItems",pageUsers.getTotalElements());
            response.put("totalPages",pageUsers.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }


    }

    @GetMapping("/getuserbystatusandusername")
    public ResponseEntity<Map<String,Object>> getUserstatus(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size

    ){
        try {
            List<Utilisateur> users= new ArrayList<Utilisateur>();
            Pageable paging= PageRequest.of(page, size);
            Page<Utilisateur> pageUsers;

            if(status==null)
                if(username==null){
                    pageUsers=userRepo.findAll(paging);
                }else{pageUsers=userRepo.findByUsernameContainingIgnoreCase(username,paging);}

            else{
                if(username==null){
                    pageUsers=userRepo.findUsersByStatus(status,paging);
                }else {
                    pageUsers=userRepo.findByUsernameContainingIgnoreCaseAndStatus(status,username,paging);
                }

            }


            users=pageUsers.getContent();
            Map<String,Object> response= new HashMap<>();
            response.put("users",users);
            response.put("currentPage",pageUsers.getNumber());
            response.put("totalItems",pageUsers.getTotalElements());
            response.put("totalPages",pageUsers.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        }


    }

    @GetMapping("/getrecentuser")
    public ResponseEntity<?> getRecentUser(){
        List<Utilisateur> user = userRepo.findTop5ByOrderByUserCompteCreatedDateDesc();
        return  new ResponseEntity<>(user,HttpStatus.OK);
    }
    //areEmailNotificationsEnabled
    @GetMapping("/enablesendmail/{userid}")
    public ResponseEntity<?> areEmailNotificationsEnabled(
            @PathVariable(value = "userid") Long userid
    ){
        Utilisateur user= userRepo.findById(userid).get();

         return new ResponseEntity<>(notificationPreferencesService.areEmailNotificationsEnabled(user),HttpStatus.OK);
    }

    @PatchMapping("/changenotifpreference/{userid}")
    public  ResponseEntity<?>  changeNotificationpreference(
            @PathVariable(value = "userid") Long userid,
            @RequestParam(required = false) Boolean enableEmailNotifications
    ){
        Utilisateur user= userRepo.findById(userid).get();

        return new ResponseEntity<>(notificationPreferencesService.changeNotificationperference(user),HttpStatus.OK);
    }

    @GetMapping("/getuserstat")
    public  Map<String,Object>  totaluser(){
        return userService.displayuserStats();
    }
    @PutMapping("/{utilisateurId}/emailnotifications")
    public ResponseEntity<NotificationPreferences> updateEmailNotificationsEnabled(@PathVariable Long utilisateurId,
                                                                                   @RequestParam boolean emailNotificationsEnabled) {
        NotificationPreferences updatedNotificationPreferences = notificationPreferencesService.updateEmailNotificationsEnabled(utilisateurId, emailNotificationsEnabled);
        if (updatedNotificationPreferences != null) {
            return new ResponseEntity<>(updatedNotificationPreferences, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
