package com.albireo3754

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DaoFactory {
    @Bean
    fun connectionMaker(): ConnectionMaker {
        return SimpleConnectionMaker()
    }
}