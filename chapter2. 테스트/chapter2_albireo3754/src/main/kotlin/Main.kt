package com.albireo3754

import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Main

fun main(args: Array<String>) {
    val application = runApplication<Main>(*args)

    val userDao = application.getBean<UserDao>()
    val userDao2 = application.getBean<UserDao>()

    val factory = application.getBean<DaoFactory>()

    println(DaoFactory().userDao(connectionMaker = factory.connectionMaker()))
    println(DaoFactory().userDao(connectionMaker = factory.connectionMaker()))
    println(DaoFactory().userDao(connectionMaker = factory.connectionMaker()) == DaoFactory().userDao(connectionMaker = factory.connectionMaker()))
    println(userDao == userDao2)

    val user = User("whiteship", "백기선", "married")
    userDao.add(user)

    println("${user.id} 등록 성공!!")

    val user2 = userDao.get(user.id)
    println(user2.password)
    println("${user2.id} 조회 성공!!")
}