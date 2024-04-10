package com.example.todoservice.h2Adapter

import com.example.todoservice.core.TodoItem
import com.example.todoservice.core.TodoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class SqlTodoItemRepository @Autowired constructor(
    private val repository: TodoItemCrudRepository
): TodoRepository {
    override fun new(todo: TodoItem) {
        repository.insert(id = todo.id, description = todo.description, createdAt = todo.createdAt, dueAt = todo.dueAt)
    }

    override fun findById(id: UUID): Optional<TodoItem> = repository.findById(id)
}
