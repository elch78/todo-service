package com.example.todoservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
class TodoServiceApplication

fun main(args: Array<String>) {
	runApplication<TodoServiceApplication>(*args)
}
