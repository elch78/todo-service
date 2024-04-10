package com.example.todoservice

import com.example.todoservice.core.TodoItem
import com.example.todoservice.core.TodoRepository
import com.example.todoservice.core.UuidProvider
import org.hamcrest.Matchers.endsWith
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
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
import java.util.stream.Stream

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    @MockBean
    private val repo: TodoRepository,
    @MockBean
    private val timeProvider: TimeProvider,
    @MockBean
    private val uuidProvider: UuidProvider,
) {

    @AfterEach
    fun afterEach() {
        verifyNoMoreInteractions(repo)
    }

    @Test
    fun createTodoHappyCase() {
        // Given
        whenever(timeProvider.now()).thenReturn(NOW)
        whenever(uuidProvider.randomUuid()).thenReturn(RANDOM_UUID)

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
            .andExpect(header().string("Location", endsWith("/todo/$RANDOM_UUID")))

        // Then
        verify(repo).new(TodoItem(description = "testDescription", createdAt = NOW, dueAt = DUE, doneAt = null, id = RANDOM_UUID))
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
    @MethodSource
    fun createTodoInvalidBodyShouldReturn400(invalidBody: String) {
        // Given
        whenever(timeProvider.now()).thenReturn(NOW)

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

        @JvmStatic
        fun createTodoInvalidBodyShouldReturn400() = Stream.of(
            Arguments.of("{}"),
            Arguments.of("{description: null}"),
            Arguments.of("{description: \"valid\"}"),
            Arguments.of("{description: \"valid\", dueAt: null}"),
            Arguments.of("{description: null, dueAt: \"$DUE\"}"),
            Arguments.of("{dueAt: \"$DUE\"}"),
        )
    }
}
