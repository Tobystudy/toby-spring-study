package com.albireo3754.tobyspring.service

import com.albireo3754.domain.Level
import com.albireo3754.domain.User
import com.albireo3754.tobyspring.dao.UserDao
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Component
import java.util.Properties

@Component
class UserService(
    val userDao: UserDao,
    @Qualifier("mailSenderServiceImpl")
    val mailSenderService: MailSenderService
) {

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
        sendUpgradeEMail(user)
    }

    private fun sendUpgradeEMail(user: User) {
        val props = Properties()
        props["mail.smtp.host"] = "mail.albireo3754.com"
        val mail = SimpleMailMessage()
        mail.setFrom("user")
        mail.setSubject("Upgrade 안내")
        mailSenderService.send(mail)
    }
}