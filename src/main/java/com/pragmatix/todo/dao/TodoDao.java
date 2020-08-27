package com.pragmatix.todo.dao;

import com.pragmatix.todo.model.Todo;
import java.util.List;

public interface TodoDao {
    List<Todo> listTodos(String filter);

    Todo getTodo(String id);

    Todo updateTodo(Todo todo);

    Todo markTodoAsCompleted(String  todoId);

    Todo saveTodo(Todo todo);

    void deleteTodo(String id);
}
