package com.example.todoservice.restAdapter

import com.example.todoservice.core.TodoItem
import java.time.Instant
import java.time.temporal.TemporalUnit

data class NewTodoDto(
    val description: String,
    val dueAt: Instant
) {
}
