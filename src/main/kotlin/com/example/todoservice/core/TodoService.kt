package com.example.todoservice.core

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TodoService @Autowired constructor(
    private val repo: TodoRepository
) {
    fun new(todo: TodoItem) {
        return repo.new(todo)
    }
}
