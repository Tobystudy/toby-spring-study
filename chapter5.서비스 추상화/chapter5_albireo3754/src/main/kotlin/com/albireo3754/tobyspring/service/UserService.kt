package com.albireo3754.tobyspring.service

import com.albireo3754.domain.Level
import com.albireo3754.domain.User
import com.albireo3754.tobyspring.dao.UserDao
import org.springframework.stereotype.Component

@Component
class UserService(val userDao: UserDao) {

    fun upgradeLevels() {
        val users = userDao.getAll()
        users.forEach { user ->
            if (canUpgradeLevel(user)) {
                upgradeLevel(user)
            }
        }
    }

    fun canUpgradeLevel(user: User): Boolean {
        return when (user.level) {
            Level.BASIC -> user.login >= 50
            Level.SILVER -> user.recommend >= 30
            Level.GOLD -> false
        }
    }

    fun upgradeLevel(user: User) {
        user.upgradeLevel()
        userDao.update(user)
    }
}