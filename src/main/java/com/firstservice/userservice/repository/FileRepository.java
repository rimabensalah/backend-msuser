package com.firstservice.userservice.repository;

import com.firstservice.userservice.domain.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository  extends JpaRepository<UserImage, Long> {

    UserImage findByName(String name);
}
