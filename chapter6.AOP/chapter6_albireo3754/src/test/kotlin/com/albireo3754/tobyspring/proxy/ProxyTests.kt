package com.albireo3754.tobyspring.proxy

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class TextViewController {
    var delegate: TextViewControllerDelegate? = null
}

class MainViewController(
    private val mainViewModel: MainViewModel
): TextViewControllerDelegate by mainViewModel {
    private val textViewController = TextViewController()

    fun viewDidInit() {
        textViewController.delegate = this
    }

    override fun textViewDidChanged() {
        mainViewModel.textViewDidChanged()
    }

    override fun textViewDidBeginEditing() {
        mainViewModel.textViewDidBeginEditing()
    }
}

class UserServiceImpl: UserService {
    override fun getAll(): List<String> {
        return listOf("User")
    }

    override fun getUserById(id: Long): String {
        return "User"
    }
}

class UserServiceProxy(private val user: UserService): UserService by user {
    override fun getUserById(id: Long): String {
        return user.getUserById(id) + "Proxy"
    }
}

class UserServiceProxyV2(private var target: Any?): InvocationHandler {

    override fun invoke(proxy: Any, method: Method?, args: Array<out Any>?): Any? {
        println(method?.name)
        println(args)
        val notNullableArgs = args ?: arrayOf()
        if (method?.name == "getUserById") {
            return method.invoke(target, *notNullableArgs) as String + "Proxy"
        }
        return method?.invoke(target, *notNullableArgs)
    }
}

interface UserService {
    fun getAll(): List<String>

    fun getUserById(id: Long): String
}


open class MainViewModel : TextViewControllerDelegate {
    override fun textViewDidChanged() {
        TODO("Not yet implemented")
    }

    override fun textViewDidBeginEditing() {
        TODO("Not yet implemented")
    }

}

interface TextViewControllerDelegate {
    fun textViewDidChanged()
    fun textViewDidBeginEditing()
}

class ProxyTests {

    @Test
    fun proxyTest() {
        val mainViewController = MainViewController(MainViewModel())
        mainViewController.viewDidInit()
    }

    @Test
    fun test_getUserByIdShouldBeProxying_getAllShouldNotBeProxying() {
        val userService = UserServiceImpl()
        val userServiceProxy = UserServiceProxy(userService)

        assertProxying(userServiceProxy)
    }

    @Test
    fun test_whenDynamicProxy_getUserByIdShouldBeProxying_getAllShouldNotBeProxying() {
        val userService = UserServiceImpl()
        val dynamicProxy = Proxy.newProxyInstance(javaClass.classLoader, arrayOf(UserService::class.java), UserServiceProxyV2(userService)) as? UserService
        dynamicProxy shouldNotBe null
        assertProxying(dynamicProxy!!)
    }

    private fun assertProxying(proxy: UserService) {
        proxy.getUserById(1) shouldBe "UserProxy"
        assertEquals(proxy.getUserById(1), "UserProxy")
//        proxy.getAll() shouldBe listOf("User")
        proxy.getAll()
    }

    @Test
    fun test_reflection() {
        val name = "Ako"

        name.length shouldBe 3
        String::class.java.getMethod("length").invoke(name) shouldBe 3

    }

    @Test
    fun test_dynamic_proxy() {

    }
}