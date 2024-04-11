package com.example.todoservice.h2Adapter

import com.example.todoservice.core.TodoItem
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.*


interface TodoItemCrudRepository: CrudRepository<TodoItem, UUID> {
    @Modifying
    @Query(value = "INSERT INTO todo_item (id, description, created_at, due_at) VALUES (:id, :description, :createdAt, :dueAt)")
    fun insert(@Param("id") id: UUID, @Param("description") description: String, @Param("createdAt") createdAt: Instant, @Param("dueAt")dueAt: Instant)

    @Modifying
    @Query("UPDATE todo_item SET done_at = :doneAt WHERE id = :id")
    fun markDone(id: UUID, doneAt: Instant)

    @Modifying
    @Query("UPDATE todo_item SET done_at = null WHERE id = :id")
    fun markUndone(id: UUID)
}
