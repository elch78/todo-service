package com.example.todoservice.core

import org.springframework.stereotype.Component
import java.util.*

@Component
class UuidProvider {
    fun randomUuid() = UUID.randomUUID()
}
