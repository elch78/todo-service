package com.example.todoservice

interface TodoRepository {
    fun new(todo: TodoItem?)
}
