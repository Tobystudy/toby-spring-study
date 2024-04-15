package com.albireo3754.tobyspring.service

import com.albireo3754.domain.Level
import com.albireo3754.domain.User
import com.albireo3754.tobyspring.dao.UserDao
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.MimeMailMessage
import org.springframework.stereotype.Component
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.util.Properties

@Component
class UserService(
    val userDao: UserDao,
    val mailSender: MailSender
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
        mailSender.send(mail)
    }
}