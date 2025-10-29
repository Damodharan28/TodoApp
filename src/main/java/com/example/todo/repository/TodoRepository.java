package com.example.todo.repository;

import com.example.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByStatus(Todo.Status status);
    List<Todo> findAllByOrderByCreatedAtAsc();
    List<Todo> findAllByOrderByCreatedAtDesc();
}
