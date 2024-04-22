package com.todoapp.todoapp.business.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todoapp.todoapp.business.persistence.DAO.TaskDAO;

@Repository
public interface TaskRepository extends JpaRepository<TaskDAO, Long> {
}
