package com.example.todoservice

import com.example.todoservice.core.TodoItem
import com.example.todoservice.core.TodoRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    @MockBean
    private val repo: TodoRepository,
    @MockBean
    private val timeProvider: TimeProvider,
) {

    @AfterEach
    fun afterEach() {
        verifyNoMoreInteractions(repo)
    }

    @Test
    fun createTodoHappyCase() {
        // Given
        whenever(timeProvider.now()).thenReturn(NOW)
        whenever(repo.new(any())).thenReturn(TodoItem(description = "testDescription", createdAt = NOW, dueAt = DUE))

        // When
        createTodoItem(due = DUE)
            .andExpect(status().isCreated)
            .andExpect(content().json("""
                {
                    "description":"testDescription",
                    "dueAt": "$DUE",
                    "createdAt": "$NOW",
                    "status": "NOT_DONE",
                    "doneAt": null
                }
            """.trimIndent()))

        // Then
        verify(repo).new(TodoItem(description = "testDescription", createdAt = NOW, dueAt = DUE, doneAt = null))
    }



    private fun createTodoItem(due: Instant) = mvc.perform(
        post("/todo")
            .contentType("application/json")
            .content(
                """
                    {
                        "description":"testDescription",
                        "dueAt": "$due"
                    }
                    """.trimIndent()
            )
    )

    companion object {
        private val NOW = Instant.now()
        private val DUE = NOW.plus(3, DAYS)
    }
}
