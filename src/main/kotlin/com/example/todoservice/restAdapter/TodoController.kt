package com.example.todoservice.restAdapter

import com.example.todoservice.TimeProvider
import com.example.todoservice.core.TodoItem
import com.example.todoservice.core.TodoRepository
import com.example.todoservice.core.UuidProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI


@RestController
@RequestMapping("/todo")
class TodoController @Autowired constructor(
    private val repo: TodoRepository,
    private val timeProvider: TimeProvider,
    private val uuidProvider: UuidProvider,
) {
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun new(@RequestBody dto: NewTodoDto): ResponseEntity<TodoItemDto> {

        val now = timeProvider.now();
        if(dto.dueAt.isBefore(now)) {
            throw ErrorResponseException(BAD_REQUEST, ProblemDetail.forStatusAndDetail(BAD_REQUEST, "dueAt must not be in the past: ${dto.dueAt}" ), null)
        }

        val todoItem = TodoItem(
            id = uuidProvider.randomUuid(),
            description = dto.description,
            createdAt = now,
            dueAt = dto.dueAt,
        )

        repo.new(todoItem)
        val location: URI = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(todoItem.id)
            .toUri()
        return ResponseEntity.created(location)
            .body(TodoItemDto.from(todoItem, now))
    }
}
