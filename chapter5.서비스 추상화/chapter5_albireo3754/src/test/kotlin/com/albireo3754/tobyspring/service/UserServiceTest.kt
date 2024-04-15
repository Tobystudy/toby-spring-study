package com.albireo3754.tobyspring.service

import com.albireo3754.domain.Level
import com.albireo3754.domain.User
import com.albireo3754.tobyspring.dao.UserDao
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.TestConstructor

@SpringBootTest(classes = [DummyMailSenderService::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserServiceTest(var userService: UserService, var userDao: UserDao) {
    private var users = mutableListOf<User>()


    @BeforeEach
    fun setUp() {
        users.add(User("whiteship", "백기선", "married", Level.BASIC, 1, 0))
        users.add(User("gyumee", "강명선", "single", Level.SILVER, 55, 10))
        users.add(User("toby", "김영한", "single", Level.GOLD, 100, 40))
        userDao.deleteAll()
    }
    @Test
    fun test() {
        assertNotEquals(userService, null)
    }

    @Test
    fun upgradeLevels() {
        users[0].login = 50
        userDao.add(users[0])

        userService.upgradeLevels()

        checkLevel(users[0], Level.BASIC)
    }

    private fun checkLevel(user: User, level: Level) {
        assertEquals(userDao.get(user.id).level, level)
    }
}