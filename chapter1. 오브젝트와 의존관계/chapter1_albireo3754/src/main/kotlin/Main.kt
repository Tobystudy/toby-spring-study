package com.albireo3754

fun main() {
    val userDao = UserDao()

    val user = User("whiteship", "백기선", "married")
    userDao.add(user)

    println("${user.id} 등록 성공!!")

    val user2 = userDao.get(user.id)
    println(user2.password)
    println("${user2.id} 조회 성공!!")
}