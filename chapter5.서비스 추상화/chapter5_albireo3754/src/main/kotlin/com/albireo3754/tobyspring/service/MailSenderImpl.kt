package com.albireo3754.tobyspring.service

import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Component

@Component
class MailSenderImpl: MailSender {
    override fun send(vararg simpleMessages: SimpleMailMessage?) {
    }
}