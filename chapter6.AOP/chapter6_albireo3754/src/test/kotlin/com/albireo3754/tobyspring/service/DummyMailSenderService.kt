package com.albireo3754.tobyspring.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Component

@Component
class DummyMailSenderService: MailSenderService {
    override fun send(vararg simpleMessages: SimpleMailMessage?) {
    }
}