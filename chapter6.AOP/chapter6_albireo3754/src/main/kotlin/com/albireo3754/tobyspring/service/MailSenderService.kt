package com.albireo3754.tobyspring.service

import org.springframework.mail.SimpleMailMessage

interface MailSenderService {
    fun send(vararg simpleMessages: SimpleMailMessage?)
}