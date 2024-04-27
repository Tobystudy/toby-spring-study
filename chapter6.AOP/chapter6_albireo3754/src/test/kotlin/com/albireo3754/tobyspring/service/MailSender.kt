package com.albireo3754.tobyspring.service

import org.springframework.mail.SimpleMailMessage

interface MailSender {
    fun send(mail: SimpleMailMessage)
}