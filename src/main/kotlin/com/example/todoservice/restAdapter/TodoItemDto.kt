package com.example.todoservice.restAdapter

import com.example.todoservice.core.TodoItem
import com.example.todoservice.core.TodoItemStatus
import java.time.Instant
import java.util.*

class TodoItemDto(
    val id: UUID,
    val description: String,
    val dueAt: Instant,
    val createdAt: Instant,
    val doneAt: Instant?,
    val status: TodoItemStatus
) {
    companion object {
        fun from(todoItem: TodoItem, currentTime: Instant) = TodoItemDto(
            id = todoItem.id,
            description = todoItem.description,
            dueAt = todoItem.dueAt,
            createdAt = todoItem.createdAt,
            doneAt = todoItem.doneAt,
            status = todoItem.status(currentTime)
        )
    }
}
