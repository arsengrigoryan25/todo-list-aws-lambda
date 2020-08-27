package com.pragmatix.todo.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragmatix.todo.dao.TodoDao;
import com.pragmatix.todo.dao.TodoDaoImpl;
import com.pragmatix.todo.model.Todo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoRequestHandler {
    private TodoDao dao;

    private TodoDao getTodoDao() {
        if (this.dao == null) {
            this.dao = new TodoDaoImpl();
        }
        return this.dao;
    }

    public void setTodoDao(TodoDao dao) {
        this.dao = dao;
    }

    /**
     *  Get items depend of path`s parameter "exclude_completed"
     *
     * @param request  -
     * @return  -  list items
     */
    public APIGatewayProxyResponseEvent listTodos(APIGatewayProxyRequestEvent request) {
        String exclude_completed = request.getQueryStringParameters().get("exclude_completed");
        List<Todo> todos = this.getTodoDao().listTodos(exclude_completed);

        try {
            return parsObjectToJsonAndAddResponse(todos);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent().withStatusCode(500);
        }
    }

    /**
     * Get item by id, taking id from URl
     *
     * @param request  -
     * @return  -
     */
    public APIGatewayProxyResponseEvent getTodoById(APIGatewayProxyRequestEvent request) {
        String todoId = request.getPathParameters().get("id");
        Todo todo = this.getTodoDao().getTodo(todoId);

        if (todo == null) {
            return new APIGatewayProxyResponseEvent().withStatusCode(404);
        }

        try {
            return parsObjectToJsonAndAddResponse(todo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent().withStatusCode(500);
        }
    }

    /**
     * Update item by id
     *
     * @param request  -
     * @return  -
     */
    public APIGatewayProxyResponseEvent updateTodo(APIGatewayProxyRequestEvent request) {
        String body = request.getBody();
        ObjectMapper mapper = new ObjectMapper();
        Todo todo;
        try {
            todo = mapper.readValue(body, Todo.class);
            todo = this.getTodoDao().updateTodo(todo);

            return parsObjectToJsonAndAddResponse(todo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent().withStatusCode(500);
        }
    }

    /**
     *  Make item completed by id
     *
     * @param request  -
     * @return  -
     */
    public APIGatewayProxyResponseEvent markTodoAsCompleted(APIGatewayProxyRequestEvent request) {
        String todoId = request.getPathParameters().get("id");
        Todo todo = this.getTodoDao().getTodo(todoId);
        if (todo == null) {
            return new APIGatewayProxyResponseEvent().withStatusCode(404);
        }
        try {
            todo = this.getTodoDao().markTodoAsCompleted(todo.getId());

            return parsObjectToJsonAndAddResponse(todo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent().withStatusCode(500);
        }
    }

    /**
     * Create item
     *
     * @param request  -
     * @return  -
     */
    public APIGatewayProxyResponseEvent createTodo(APIGatewayProxyRequestEvent request) {
        String body = request.getBody();
        ObjectMapper mapper = new ObjectMapper();
        Todo todo;

        try {
            todo = mapper.readValue(body, Todo.class);
            todo = this.getTodoDao().saveTodo(todo);

            return parsObjectToJsonAndAddResponse(todo);
        } catch (IOException e) {
            e.printStackTrace();
            return new APIGatewayProxyResponseEvent().withStatusCode(500);
        }
    }

    /**
     * Delete item by id
     *
     * @param request  -
     * @return  -
     */
    public APIGatewayProxyResponseEvent deleteTodo(APIGatewayProxyRequestEvent request) {
        String todoId = request.getPathParameters().get("id");
        this.getTodoDao().deleteTodo(todoId);

        return new APIGatewayProxyResponseEvent().withStatusCode(200);
    }

    /**
     *  Pars Todo List or Todo object in JSON format, and include this JSON APIGatewayProxyResponseEvent for response
     *
     * @param todo  -  Todo object or Todo list
     * @return  -
     * @throws JsonProcessingException
     */
    private APIGatewayProxyResponseEvent parsObjectToJsonAndAddResponse(Object todo) throws JsonProcessingException {
        Map<String, String> headers = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        String jsonInString = mapper.writeValueAsString(todo);
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(headers).withBody(jsonInString);
    }

}
