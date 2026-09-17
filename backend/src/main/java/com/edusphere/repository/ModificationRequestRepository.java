package com.edusphere.repository;

import com.edusphere.entity.ModificationRequest;
import com.edusphere.entity.ModificationRequestStatus;
import com.edusphere.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModificationRequestRepository extends JpaRepository<ModificationRequest, Long> {
    List<ModificationRequest> findAllByStatus(ModificationRequestStatus status);
    List<ModificationRequest> findAllByRequestedBy(Teacher requestedBy);
}
