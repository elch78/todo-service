package com.example.todoservice.restAdapter

import com.example.todoservice.core.TimeProvider
import com.example.todoservice.core.TodoItem
import com.example.todoservice.core.TodoService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.*


@RestController
@RequestMapping("/todos")
class TodoController @Autowired constructor(
    private val timeProvider: TimeProvider,
    private val todoService: TodoService,
) {
    /**
     * due date is truncated to millis due to H2 data type
     */
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun new(@RequestBody dto: NewTodoDto): ResponseEntity<TodoItemDto> {
        LOG.debug("Create todo item. dto='{}'", dto)
        val now = timeProvider.now();

        val todoItem = todoService.new(dto.description, dto.dueAt)
        val location: URI = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(todoItem.id)
            .toUri()
        LOG.info("Todo item created. todoItem='{}'", todoItem)
        return ResponseEntity.created(location)
            .body(TodoItemDto.from(todoItem, now))
    }

    @GetMapping("/{id}", produces = ["application/json"])
    fun get(@PathVariable id: UUID): ResponseEntity<TodoItemDto> {
        LOG.debug("get id='{}'", id)
        val todoItem = todoService.findById(id)
            .orElseThrow { ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(BAD_REQUEST, "No todo item with id $id" ), null) }
        LOG.info("get successful. id='{}'", id)
        return ResponseEntity.ok(TodoItemDto.from(todoItem, timeProvider.now()))
    }

    @GetMapping(produces = ["application/json"])
    fun list(): ResponseEntity<List<TodoItem>> {
        LOG.debug("list")
        val todoItems = todoService.list()
        return ResponseEntity.ok(todoItems.toList())
        LOG.info("list successful.")
    }

    @PatchMapping("/{id}/mark_done", consumes = ["application/json"])
    fun markDone(@PathVariable id: UUID, @RequestBody dto: MarkDoneDto?) {
        LOG.debug("markDone id='{}', dto='{}'", id, dto)
        todoService.markDone(id, dto?.doneAt?:null)
        LOG.info("markDone successful. doneAt='{}'", dto?.doneAt)
    }

    @PatchMapping("/{id}/mark_undone")
    fun markUndone(@PathVariable id: UUID) {
        LOG.debug("markDone id='{}'", id)
        todoService.markUndone(id)
        LOG.info("markUndone successful. id='{}'", id)
    }

    @PatchMapping("/{id}/rephrase", consumes = ["application/json"])
    fun rephrase(@PathVariable id: UUID, @RequestBody dto: RephraseDto) {
        val newDescription = dto.newDescription
        LOG.debug("rephrase id='{}, newDescription='{}''", id, newDescription)
        todoService.rephrase(id, newDescription)
        LOG.info("rephrase successful. id='{}', newDescription='{}''", id, newDescription)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(TodoController::class.java)
    }
}
