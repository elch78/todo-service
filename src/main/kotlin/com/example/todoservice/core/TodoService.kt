package com.example.todoservice.core

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.ErrorResponseException
import java.time.Instant
import java.util.*

@Service
class TodoService @Autowired constructor(
    private val repo: TodoRepository,
    private val timeProvider: TimeProvider,
    private val uuidProvider: UuidProvider,
) {
    @Transactional
    fun new(description: String, dueAt: Instant): TodoItem {
        LOG.debug("new description='{}', dueAt='{}'", description, dueAt)
        val now = timeProvider.now()

        if(dueAt.isBefore(now)) {
            throw error(BAD_REQUEST, "dueAt must not be in the past: $dueAt")
        }

        val todoItem = TodoItem(
            id = uuidProvider.randomUuid(),
            description = description,
            createdAt = now,
            dueAt = dueAt,
        )
        repo.new(todoItem)
        LOG.info("new successful todoItem='{}'", todoItem)

        return todoItem
    }

    fun findById(id: UUID): Optional<TodoItem> {
        LOG.debug("findById id='{}'", id)
        val todoItem = repo.findById(id)
        LOG.info("findById successful id='{}', todoItem='{}'", id, todoItem)
        return todoItem
    }

    @Transactional
    fun markDone(id: UUID, doneAt: Instant?) {
        LOG.debug("markDone id='{}', doneAt='{}'", id, doneAt)
        val todoItem = findById(id)
            .orElseThrow{ error(NOT_FOUND, "Todo item not found: $id") }

        checkDueDate(todoItem)

        todoItem.markDone(doneAt?:timeProvider.now())
        repo.save(todoItem)
        LOG.info("markDone successful id='{}'", id)
    }

    @Transactional
    fun markUndone(id: UUID) {
        LOG.debug("markUndone id='{}'", id)
        val todoItem = findById(id)
            .orElseThrow{ error(NOT_FOUND, "Todo item not found: $id") }

        checkDueDate(todoItem)

        todoItem.markUndone()
        repo.save(todoItem)
        LOG.info("markUndone successful id='{}'", id)
    }

    private fun error(httpStatus: HttpStatus, detail: String) =
        ErrorResponseException(httpStatus, ProblemDetail.forStatusAndDetail(httpStatus, detail), null)

    private fun checkDueDate(todoItem: TodoItem) {
        if(todoItem.dueAt.isBefore(timeProvider.now())) {
            throw ErrorResponseException(CONFLICT, ProblemDetail.forStatusAndDetail(CONFLICT, "Todo item may not be changed past due date: ${todoItem.dueAt}"), null)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(TodoService::class.java)
    }
}
