package com.example.todoservice.core

import com.example.todoservice.core.TodoItemStatus.DONE
import com.example.todoservice.core.TodoItemStatus.NOT_DONE
import com.example.todoservice.core.TodoItemStatus.PAST_DUE
import org.springframework.data.annotation.Id
import java.time.Instant
import java.util.*

class TodoItem(
    @Id
    val id: UUID,
    var description: String,
    val createdAt: Instant,
    val dueAt: Instant,
    var doneAt: Instant? = null
) {
    fun markDone(doneAt: Instant) {
        // TODO require doneAt == null
        this.doneAt = doneAt
    }

    fun markUndone() {
        this.doneAt = null
    }

    fun status(currentTime: Instant) = when {
        doneAt == null && currentTime.isAfter(dueAt) -> PAST_DUE
        doneAt == null -> NOT_DONE
        else -> DONE
    }

    fun rephrase(newDescription: String) {
        description = newDescription
    }

    override fun toString(): String {
        return "TodoItem(id=$id, description='$description', createdAt=$createdAt, dueAt=$dueAt, doneAt=$doneAt)"
    }
}
