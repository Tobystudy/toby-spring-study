package com.albireo3754.tobyspring.proxy

import org.junit.jupiter.api.Test

class TextViewController {
    var delegate: TextViewControllerDelegate? = null
}

class MainViewController: TextViewControllerDelegate {
    private val mainViewModel = MainViewModel()
    private val textViewController = TextViewController()

    fun viewDidInit() {
        textViewController.delegate = this
    }

    override fun textViewDidChanged() {
        mainViewModel.textViewDidChanged()
    }
}

class MainViewModel {
    fun textViewDidChanged() {
        TODO("Not yet implemented")
    }

}

interface TextViewControllerDelegate {
    fun textViewDidChanged()
}

class ProxyTests {

    @Test
    fun proxyTest() {
        val mainViewController = MainViewController()
        mainViewController.viewDidInit()
    }
}