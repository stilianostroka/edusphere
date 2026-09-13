package com.edusphere.repository;

import com.edusphere.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject,Long> {
    boolean existsByCode(String code);
    Optional<Subject> findByCode(String code);
    List<Subject> findByProgramYear(Integer programYear);
}
