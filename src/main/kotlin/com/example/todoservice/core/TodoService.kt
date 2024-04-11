package com.example.todoservice.core

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Service
import org.springframework.web.ErrorResponseException
import java.time.Instant
import java.util.*

@Service
class TodoService @Autowired constructor(
    private val repo: TodoRepository,
    private val timeProvider: TimeProvider,
    private val uuidProvider: UuidProvider,
) {
    fun new(description: String, dueAt: Instant): TodoItem {
        val now = timeProvider.now()

        if(dueAt.isBefore(now)) {
            throw ErrorResponseException(BAD_REQUEST, ProblemDetail.forStatusAndDetail(BAD_REQUEST, "dueAt must not be in the past: $dueAt" ), null)
        }

        val todoItem = TodoItem(
            id = uuidProvider.randomUuid(),
            description = description,
            createdAt = now,
            dueAt = dueAt,
        )
        repo.new(todoItem)

        return todoItem
    }

    fun findById(id: UUID): Optional<TodoItem> {
        val todoItem = repo.findById(id)
        return todoItem
    }

    fun markDone(id: UUID, doneAt: Instant?) {
        // FIXME not found
        // FIXME read + write requires transaction
        val todoItem = findById(id).get()
        todoItem.markDone(doneAt?:timeProvider.now())
        repo.save(todoItem)
    }

    fun markUndone(id: UUID) {
        // FIXME not found
        // FIXME read + write requires transaction
        val todoItem = findById(id).get()
        todoItem.markUndone()
        repo.save(todoItem)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(TodoService::class.java)
    }
}
