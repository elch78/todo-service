package com.example.todoservice.restAdapter

import com.example.todoservice.core.TodoItem
import com.example.todoservice.core.TodoItemStatus
import java.time.Instant

class TodoItemDto(
    val description: String,
    val dueAt: Instant,
    val createdAt: Instant,
    val doneAt: Instant?,
    val status: TodoItemStatus
) {
    companion object {
        fun from(todoItem: TodoItem, currentTime: Instant) = TodoItemDto(
            description = todoItem.description,
            dueAt = todoItem.dueAt,
            createdAt = todoItem.createdAt,
            doneAt = todoItem.doneAt,
            status = todoItem.status(currentTime)
        )
    }
}
