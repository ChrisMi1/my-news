package com.techie.mynews.repository;

import com.techie.mynews.model.UserSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSourceRepository extends JpaRepository<UserSource,String> {
    boolean existsByUser_IdAndSource_Id(String userId,Integer sourceId);

    @Query("SELECT us FROM UserSource us WHERE us.user.id = :userId AND us.source.id IN :sourceIds")
    List<UserSource> findAllByUserIdAndSourceIds(
            @Param("userId") String userId,
            @Param("sourceIds") List<Integer> sourceIds
    );
}
