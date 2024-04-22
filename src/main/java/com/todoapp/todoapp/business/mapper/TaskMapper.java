package com.todoapp.todoapp.business.mapper;

import org.mapstruct.Mapper;

import com.todoapp.todoapp.business.persistence.DAO.TaskDAO;
import com.todoapp.todoapp.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDAO taskToTaskDAO(Task task);

    Task taskDAOToTask(TaskDAO taskDAO);

}
