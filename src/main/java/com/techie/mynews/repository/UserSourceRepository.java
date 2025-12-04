package com.techie.mynews.repository;

import com.techie.mynews.model.UserSource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSourceRepository extends JpaRepository<UserSource,String> {
    boolean existsByUser_IdAndSource_Id(String userId,Integer sourceId);
}
