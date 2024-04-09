package com.example.todoservice

import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    @MockBean
    private val repo: TodoRepository,
    @MockBean
    private val timeProvider: TimeProvider,
) {

    @Test
    fun createTodoHappyCase() {
        // Given
        whenever(timeProvider.now()).thenReturn(NOW)

        // When
        mvc.perform(post("/todo")
            .content("""
                {
                    "description":"testDescription",
                    "dueAt": "2024-04-09T12:46:31.198671665Z"
                }
                """.trimIndent()))
            .andExpect(status().isCreated)
            .andExpect(content().json("""
                {
                    "description":"testDescription",
                    "dueAt": "2024-04-09T12:46:31.198671665Z",
                    "created": "2024-04-09T12:46:31.198671665Z",
                    "status": "NOT_DONE",
                    "done": null
                }
            """.trimIndent()))

        // Then
        verify(repo.new(TodoItem(description = "testDescription", createdAt = NOW, doneAt = null)))
    }

    companion object {
        private val NOW = Instant.now()
    }
}
