package com.albireo3754

import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Main

fun main(args: Array<String>) {
    val application = runApplication<Main>(*args)

    val userDao = application.getBean<UserDao>()

    val user = User("whiteship", "백기선", "married")
    userDao.add(user)

    val user2 = userDao.get(user.id)

    if (user.name != user2.name) {
        println("테스트 실패 ${user.name} != ${user2.name} !!")
    } else if (user.password != user2.password) {
        println("테스트 실패 ${user.password} != ${user2.password} !!")
    } else {
        println("${user.id} 조회 성공!!")
    }
}