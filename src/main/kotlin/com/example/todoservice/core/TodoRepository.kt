package com.example.todoservice.core

import java.util.*

interface TodoRepository {
    fun new(todo: TodoItem)
    fun findById(id: UUID): Optional<TodoItem>
}
