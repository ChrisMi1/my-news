package com.techie.mynews.repository;

import com.techie.mynews.model.Source;
import com.techie.mynews.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    User findUserByEmail(String email);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.userSources us
        LEFT JOIN FETCH us.source
        WHERE u.email = :email
    """)
    Optional<User> findUserWithSourcesByEmail(@Param("email") String email);
}
