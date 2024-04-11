package com.example.todoservice

import com.example.todoservice.core.TimeProvider
import com.example.todoservice.core.TodoItemStatus
import com.example.todoservice.core.TodoItemStatus.DONE
import com.example.todoservice.core.TodoItemStatus.NOT_DONE
import com.example.todoservice.core.TodoRepository
import com.example.todoservice.core.UuidProvider
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.MINUTES
import java.util.*
import java.util.stream.Stream

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val repo: TodoRepository,
    @MockBean
    private val timeProvider: TimeProvider,
    @MockBean
    private val uuidProvider: UuidProvider,
) {


    @Test
    fun createTodoHappyCase() {
        // Given
        withCurrentTime(NOW)
        whenever(uuidProvider.randomUuid()).thenReturn(RANDOM_UUID)

        // When
        createTodoItem(due = DUE)
            .andExpect(status().isCreated)
            .andExpect(content().json("""
                {
                    id: "$RANDOM_UUID",
                    "description":"testDescription",
                    "dueAt": "$DUE",
                    "createdAt": "$NOW",
                    "status": "NOT_DONE",
                    "doneAt": null
                }
            """.trimIndent()))
            .andExpect(header().string("Location", endsWith("/todos/$RANDOM_UUID")))

        // Then
    }

    @Test
    fun dueDateMustNotBeInThePast() {
        // Given
        withCurrentTime(NOW)
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
        withCurrentTime(NOW)

        // When
        createTodoItem(invalidBody)
            .andExpect(status().isBadRequest)

        // Then
    }


    @Test
    fun getTodoItemHappyCase() {
        // Given
        val id = UUID.randomUUID()
        whenever(uuidProvider.randomUuid()).thenReturn(id)
        withCurrentTime(NOW)

        // When
        createTodoItem(DUE)
        getTodoItem(id)
            .andExpect(status().isOk)
            .andExpect(content().json("""
                {
                    id: "$id",
                    "description":"testDescription",
                    "dueAt": "$DUE",
                    "createdAt": "$NOW",
                    "status": "NOT_DONE",
                    "doneAt": null
                }
            """.trimIndent(), true))

        // Then
    }

    @Test
    fun getTodoItemShouldReturn404IfItemIsNotFound() {
        // Given
        val id = UUID.randomUUID()

        // When
        getTodoItem(id)
            .andExpect(status().isNotFound)

        // Then
    }

    @Test
    fun markDoneHappyCase() {
        // Given
        val id = UUID.randomUUID()
        whenever(uuidProvider.randomUuid()).thenReturn(id)
        withCurrentTime(NOW)

        // When
        createTodoItem(DUE)
        markDone(id).andExpect(status().isOk)
        expectTodoItemStatus(id, DONE, NOW)

        // Then
    }

    @Test
    fun markDoneShouldReturn404IfTodoItemDoesNotExist() {
        // Given
        val id = UUID.randomUUID()
        whenever(uuidProvider.randomUuid()).thenReturn(id)
        withCurrentTime(NOW)

        // When
        markDone(id).andExpect(status().isNotFound)
    }

    @Test
    fun markDoneShouldReturn409IfDueDateIsInThePast() {
        // Given
        val id = UUID.randomUUID()
        whenever(uuidProvider.randomUuid()).thenReturn(id)
        withCurrentTime(NOW)

        // When
        createTodoItem(DUE)

        // Given
        withCurrentTime(DUE.plus(1, MINUTES))
        markDone(id).andExpect(status().isConflict)
    }

    @Test
    fun markUndoneHappyCase() {
        // Given
        val id = UUID.randomUUID()
        whenever(uuidProvider.randomUuid()).thenReturn(id)
        withCurrentTime(NOW)

        // When
        createTodoItem(DUE)
        markDone(id)
        markUndone(id)
            .andExpect(status().isOk)
        expectTodoItemStatus(id, NOT_DONE, null)
    }

    @Test
    fun markUndoneShouldReturn409IfDueDateIsInThePast() {
        // Given
        val id = UUID.randomUUID()
        whenever(uuidProvider.randomUuid()).thenReturn(id)
        withCurrentTime(NOW)

        // When
        createTodoItem(DUE)
        markDone(id)

        withCurrentTime(DUE.plus(1, MINUTES))
        markUndone(id).andExpect(status().isConflict)
    }

    @Test
    fun markUndoneShouldReturn404IfTodoItemDoesNotExist() {
        // Given
        val id = UUID.randomUUID()

        // When
        markUndone(id)
            .andExpect(status().isNotFound)
    }

    @Test
    fun rephraseHappyCase() {
        // Given
        val id = UUID.randomUUID()
        val newDescription = "newDescription"
        whenever(uuidProvider.randomUuid()).thenReturn(id)
        withCurrentTime(NOW)

        // When
        createTodoItem(DUE)
        mvc.perform(patch("/todos/$id/rephrase")
            .content("""
                {
                    "description": "$newDescription"
                }
            """.trimIndent()))
            .andExpect(status().isOk)
        getTodoItem(id)
            .andExpect(jsonPath("$.description", `is`("$newDescription")))

        // Then
    }

    private fun withCurrentTime(currentTime: Instant?) {
        whenever(timeProvider.now()).thenReturn(currentTime)
    }

    private fun markUndone(id: UUID?) = mvc.perform(patch("/todos/$id/mark_undone"))

    private fun expectTodoItemStatus(id: UUID, status: TodoItemStatus, doneAt: Instant?) {
        getTodoItem(id)
            .andExpect(jsonPath("$.status", `is`("$status")))
            .andExpect(jsonPath("$.doneAt", `is`(if (doneAt == null) null else "$doneAt")))
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
        post("/todos")
            .contentType("application/json")
            .content(content)
    )


    private fun getTodoItem(id: UUID?) = mvc.perform(get("/todos/$id"))

    private fun markDone(id: UUID?) = mvc.perform(
        patch("/todos/$id/mark_done")
            .contentType("application/json")
            .content(
                """
                    {
                        "doneAt": null
                    }
                """.trimIndent()
            )
    )

    companion object {
        private val NOW = Instant.now().truncatedTo(ChronoUnit.MILLIS)
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
