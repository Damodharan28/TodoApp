package com.example.todo.controller;

import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService)
    {
        this.todoService = todoService;
    }

    @GetMapping
    public String listTodos(Model model,
                            @RequestParam(value="filter", required = false) String filter,
                            @RequestParam(value = "sort", required = false, defaultValue = "desc") String sort) {
        List<Todo> todos;

        //filter
        if("PENDING".equalsIgnoreCase(filter)) {
            todos = todoService.getTodosByStatus(Todo.Status.PENDING);
        }
        else if("DONE".equalsIgnoreCase(filter)) {
            todos = todoService.getTodosByStatus(Todo.Status.DONE);
        }
        else {
            todos = todoService.getAllTodos();
        }

        //sort
        if("asc".equalsIgnoreCase(sort)){
            todos.sort(Comparator.comparing(Todo::getCreatedAt));
        }
        else {
            todos.sort(Comparator.comparing(Todo::getCreatedAt).reversed());
        }

        model.addAttribute("todos", todos);
        model.addAttribute("currentFilter", filter);
        model.addAttribute("currentSort", sort);

        return "todos";
    }

    @GetMapping("/add")
    public String showAddForm(Todo todo) {
        return "add-todo";
    }

    @PostMapping("/add")
    public String addTodo(@Valid Todo todo, BindingResult result){
        if(result.hasErrors()) return "add-todo";
        todoService.saveTodo(todo);
        return "redirect:/todos";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model){
        Todo todo = todoService.getTodoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo Id:" + id));
        model.addAttribute("todo", todo);
        return "edit-todo";
    }

    @PostMapping("/edit/{id}")
    public String editTodo(@PathVariable("id") Long id, @Valid Todo todo, BindingResult result) {
        if(result.hasErrors()) { todo.setId(id); return "edit-todo"; }
        todoService.saveTodo(todo);
        return "redirect:/todos";
    }

    @GetMapping("/delete/{id}")
    public String deleteTodo(@PathVariable("id") Long id) {
        todoService.deleteTodo(id);
        return "redirect:/todos";
    }

    @GetMapping("/done/{id}")
    public String markAsDone(@PathVariable("id") Long id) {
        Todo todo = todoService.getTodoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo Id:" + id));
        todo.setStatus(Todo.Status.DONE);
        todoService.saveTodo(todo);
        return "redirect:/todos";
    }
}
