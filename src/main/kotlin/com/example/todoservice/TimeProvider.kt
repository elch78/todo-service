package com.example.todoservice

import java.time.Instant

class TimeProvider {
    fun now() = Instant.now()
}
