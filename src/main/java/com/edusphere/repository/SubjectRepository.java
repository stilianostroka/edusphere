package com.edusphere.repository;

import com.edusphere.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject,Long> {
    boolean existsByCode(String code);
    List<Subject> findByProgramYear(Integer programYear);
}
