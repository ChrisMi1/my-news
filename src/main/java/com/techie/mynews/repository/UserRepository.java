package com.techie.mynews.repository;

import com.techie.mynews.model.Source;
import com.techie.mynews.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
    User findUserByEmail(String email);
}
