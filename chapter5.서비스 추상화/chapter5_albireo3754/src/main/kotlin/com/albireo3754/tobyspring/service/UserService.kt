package com.albireo3754.tobyspring.service

import com.albireo3754.domain.Level
import com.albireo3754.tobyspring.dao.UserDao
import org.springframework.stereotype.Component

@Component
class UserService(val userDao: UserDao) {

    fun upgradeLevels() {
        val users = userDao.getAll()
        users.forEach { user ->
            var changed = false
            if (user.level == Level.BASIC && user.login >= 50) {
                user.level = Level.SILVER
                changed = true
            } else if (user.level == Level.SILVER && user.login >= 50) {
                user.level = Level.GOLD
                changed = true
            }
            if (changed) userDao.update(user)
        }
    }
}