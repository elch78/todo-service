package com.example.todoservice

import java.time.Instant

class TodoItem(
    private val description: String,
    private val createdAt: Instant,
    private val doneAt: Instant?
)
