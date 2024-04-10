package com.example.todoservice.core

interface TodoRepository {
    fun new(todo: TodoItem)
}
