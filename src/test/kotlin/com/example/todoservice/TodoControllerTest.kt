package com.example.todoservice

import com.example.todoservice.core.TodoItem
import com.example.todoservice.core.TodoRepository
import com.example.todoservice.core.UuidProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS
import java.util.*

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

    @Test
    fun dueDateMustNotBeInThePast() {
        // Given
        whenever(timeProvider.now()).thenReturn(NOW)
        val dueInThePast = NOW.minusSeconds(5)


        // When
        createTodoItem(due = dueInThePast)
            .andExpect(status().isBadRequest)

        // Then
    }

    @ParameterizedTest
    fun createTodoInvalidBodyShouldReturn400() {
        // Given
        whenever(timeProvider.now()).thenReturn(NOW)
        val invalidBody = "{}"

        // When
        createTodoItem(invalidBody)
            .andExpect(status().isBadRequest)

        // Then
    }

    private fun createTodoItem(due: Instant) =
        createTodoItem(
                """
                    {
                        "description":"testDescription",
                        "dueAt": "$due"
                    }
                    """.trimIndent()
            )

    private fun createTodoItem(content: String) = mvc.perform(
        post("/todo")
            .contentType("application/json")
            .content(content)
    )

    companion object {
        private val NOW = Instant.now()
        private val RANDOM_UUID = UUID.randomUUID()
        private val DUE = NOW.plus(3, DAYS)
    }
}
