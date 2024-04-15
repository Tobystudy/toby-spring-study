package com.albireo3754.tobyspring.service

import com.albireo3754.domain.Level
import com.albireo3754.domain.User
import com.albireo3754.tobyspring.dao.UserDao
import jakarta.mail.Address
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.mail.javamail.MimeMailMessage
import org.springframework.stereotype.Component
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.util.Properties

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
        sendUpgradeEMail(user)
    }

    private fun sendUpgradeEMail(user: User) {
        val props = Properties()
        props["mail.smtp.host"] = "mail.albireo3754.com"
        try {
            val mail = MimeMessage(Session.getInstance(props))
            mail.setFrom("starpro123@naver.com")
            mail.addRecipient(MimeMessage.RecipientType.TO, InternetAddress("starpro123@naver.com"))
            mail.subject = "Upgrade 안내"
            mail.setText("${user.id}님의 등급이 ${user.level.name}로 업그레이드 되었습니다.")
            Transport.send(mail)
        } catch (e: Exception) {
            println(e)
            throw RuntimeException(e)
        }
    }
}