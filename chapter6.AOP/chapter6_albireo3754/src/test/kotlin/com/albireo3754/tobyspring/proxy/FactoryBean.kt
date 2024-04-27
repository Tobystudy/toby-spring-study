package com.albireo3754.tobyspring.proxy

import com.albireo3754.tobyspring.TobySpringApplication
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class MessageService {
    fun getMessage(): String {
        return "Hello, World!"
    }
}
@Component
class MessageServiceFactoryBean : FactoryBean<MessageService> {
    override fun getObject(): MessageService {
        return MessageService()
    }

    override fun getObjectType(): Class<*> {
        return MessageService::class.java
    }
}
@SpringBootTest(classes = [MessageServiceFactoryBean::class])
class FactoryBeanTests @Autowired constructor(
    var applicationContext: ApplicationContext) {
    @Test
    fun test() {
        println("test")
        applicationContext shouldNotBe null
//        applicationContext.getBean(MessageService::class.java).getMessage() shouldBe "Hello, World!"
//        applicationContext.getBean("messageService") shouldBe applicationContext.getBean(MessageService::class.java)
        val factoryBean = applicationContext.getBean("&messageService", MessageServiceFactoryBean::class.java)
//        factoryBean?.`object` shouldBe applicationContext.getBean(MessageService::class.java)
    }
}