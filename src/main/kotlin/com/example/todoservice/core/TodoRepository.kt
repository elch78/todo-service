package com.example.todoservice.core

import java.util.*

interface TodoRepository {
    fun new(todo: TodoItem)
    fun save(todoItem: TodoItem)
    fun findById(id: UUID): Optional<TodoItem>
    fun findAll(notDoneOnly: Boolean): Iterable<TodoItem>
}
