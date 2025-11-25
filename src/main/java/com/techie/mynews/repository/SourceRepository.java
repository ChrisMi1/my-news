package com.techie.mynews.repository;

import com.techie.mynews.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SourceRepository extends JpaRepository<Source,Integer>, JpaSpecificationExecutor<Source> {

}
