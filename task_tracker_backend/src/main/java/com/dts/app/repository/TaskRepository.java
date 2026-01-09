package com.dts.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dts.app.model.Task;
import com.dts.app.model.Status;
import java.util.List;

/**
 * Repository interface for Task entities.
 * * The @Repository annotation marks this interface as a Spring Data repository
 */
@Repository
// JpaRepository<Task, Long>:
// Task (Entity type the repository manages)
// Long (data type of Entity's primary key (id field))
public interface TaskRepository extends JpaRepository<Task, Long> {
    //find tasks for specific user
    List<Task> findByUserId(Long id);

    //find tasks for specific userand status
    List<Task> findByUserIdAndStatus(Long id, Status status);

}
