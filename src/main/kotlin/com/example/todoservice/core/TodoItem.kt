package com.example.todoservice.core

import com.example.todoservice.core.TodoItemStatus.DONE
import com.example.todoservice.core.TodoItemStatus.NOT_DONE
import com.example.todoservice.core.TodoItemStatus.PAST_DUE
import java.time.Instant

data class TodoItem(
    val description: String,
    val createdAt: Instant,
    val dueAt: Instant,
    val doneAt: Instant? = null
) {
    fun status(currentTime: Instant) = when {
        doneAt == null && currentTime.isAfter(dueAt) -> PAST_DUE
        doneAt == null -> NOT_DONE
        else -> DONE
    }
}
