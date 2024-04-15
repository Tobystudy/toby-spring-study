package com.albireo3754.tobyspring.service

import com.albireo3754.domain.Level
import com.albireo3754.domain.User
import com.albireo3754.tobyspring.dao.UserDao
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.support.DefaultTransactionDefinition

@Component
class UserService(
    val userDao: UserDao,
    private val transactionManager: DataSourceTransactionManager
) {

    fun upgradeLevels() {
        val status = transactionManager.getTransaction(DefaultTransactionDefinition())

        val users = userDao.getAll()
        try {
            users.forEach { user ->
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user)
                }
            }
            transactionManager.commit(status)
        } catch (e: Exception) {
            transactionManager.rollback(status)
            throw e
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