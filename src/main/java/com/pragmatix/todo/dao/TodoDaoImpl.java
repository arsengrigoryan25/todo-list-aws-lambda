package com.pragmatix.todo.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.pragmatix.todo.model.Todo;

import java.util.*;

public class TodoDaoImpl implements TodoDao{

    private AmazonDynamoDB client;
    private DynamoDBMapper mapper;

    public TodoDaoImpl() {
        this.client = AmazonDynamoDBClientBuilder.standard().build();
        this.mapper = new DynamoDBMapper(this.client);
    }

    /**
     *  Get items by completed field
     *
     * @param filter  -  0 - return all items, 1 - return is not completed items
     * @return  -  list items
     */
    @Override
    public List<Todo> listTodos(String filter) {
        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression();
        // Note : filter controls whether the backend should return all or only pending todos
        if ("1".equals(filter)) {
            Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
                    .withAttributeValueList(new AttributeValue().withBOOL(false));
            dynamoDBScanExpression.addFilterCondition("completed", condition);
        }
        return this.mapper.scan(Todo.class, dynamoDBScanExpression);
    }

    /**
     *  Get item by id field
     *
     * @param id  -  returned id of item
     * @return  -  item
     */
    @Override
    public Todo getTodo(String id) {
        return this.mapper.load(Todo.class, id);
    }

    /**
     * Update of item by id
     *
     * @param todo  -  the updatable item
     * @return  -  Updated item
     */
    @Override
    public Todo updateTodo(Todo todo) {
        Todo existingTodo = this.getTodo(todo.getId());
        if (existingTodo == null) {
            return null;
        }
        existingTodo.setTitle(todo.getTitle().trim());
        existingTodo.setDescription(todo.getDescription().trim());
        existingTodo.setCompleted(todo.isCompleted());
        existingTodo.setUpdatedAt(new Date());
        this.mapper.save(existingTodo);
        return existingTodo;
    }

    /**
     * Make item completed by id
     *
     * @param todoId  -  Id of completed items
     * @return  -  Completed items if have id, or null if there is no items of this id
     */
    @Override
    public Todo markTodoAsCompleted(String todoId) {
        Todo existingTodo = this.getTodo(todoId);
        if (existingTodo == null) {
            return null;
        }
        existingTodo.setCompleted(true);
        existingTodo.setUpdatedAt(new Date());
        this.mapper.save(existingTodo);
        return existingTodo;
    }

    /**
     * Create item
     *
     * @param todo  -  creatable item
     * @return  -  creatable item
     */
    @Override
    public Todo saveTodo(Todo todo) {
        todo.setCompleted(false);
        todo.setCreatedAt(new Date());
        todo.setUpdatedAt(new Date());
        this.mapper.save(todo);
        return todo;
    }

    /**
     * Delete the item by id
     *
     * @param id  -  deletable item
     */
    @Override
    public void deleteTodo(String id) {
        Todo todo = this.getTodo(id);
        if (todo != null) {
            this.mapper.delete(todo);
        }
    }
}
