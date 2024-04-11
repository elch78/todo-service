package com.example.todoservice.core

import java.time.Instant
import java.util.*

interface TodoRepository {
    fun new(todo: TodoItem)
    fun findById(id: UUID): Optional<TodoItem>
    fun markDone(id: UUID, doneAt: Instant)
    fun markUndone(id: UUID)
}
