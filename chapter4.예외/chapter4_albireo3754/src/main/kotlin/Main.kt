package com.albireo3754

import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Main

fun main(args: Array<String>) {
    val application = runApplication<Main>(*args)
}