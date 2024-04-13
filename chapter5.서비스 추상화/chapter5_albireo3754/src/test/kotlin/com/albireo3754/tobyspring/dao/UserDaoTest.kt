package com.albireo3754.tobyspring.dao

import com.albireo3754.domain.Level
import com.albireo3754.domain.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.sql.SQLException

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserDaoTest (var userDao: UserDao)
{
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User

    @BeforeEach
    fun setUp() {
        user1 = User("whiteship", "백기선", "married", Level.BASIC, 1, 0)
        user2 = User("gyumee", "백명선", "single", Level.SILVER, 55, 10)
        user3 = User("toby", "김영한", "single", Level.GOLD, 100, 40)
        userDao.deleteAll()
    }

    @Test
    fun addAndGet() {
        val user = User("whiteship", "백기선", "married", Level.BASIC, 1, 0)
        userDao.add(user)

        val user2 = userDao.get(user.id)

        checkSameUser(user, user2)
    }

    @Test
    fun kotlinEum() {
        assertEquals(Level.valueOf(0), Level.BASIC)
    }

    private fun checkSameUser(user: User, user2: User) {
        assertEquals(user.id, user2.id)
        assertEquals(user.name, user2.name)
        assertEquals(user.password, user2.password)
        assertEquals(user.level, user2.level)
        assertEquals(user.login, user2.login)
        assertEquals(user.recommend, user2.recommend)
    }

    @Test
    fun test() {
        print(userDao)
        println(this)

        assertEquals(userDao.getCount(), 0)

        userDao.add(user1)
        assertEquals(userDao.getCount(), 1)

        userDao.add(user2)
        assertEquals(userDao.getCount(), 2)

        userDao.add(user3)
        assertEquals(userDao.getCount(), 3)
    }
}