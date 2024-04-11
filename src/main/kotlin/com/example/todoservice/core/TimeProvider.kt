package com.example.todoservice.core

import org.springframework.stereotype.Component
import java.time.Instant

@Component
class TimeProvider {
    fun now() = Instant.now()
}
