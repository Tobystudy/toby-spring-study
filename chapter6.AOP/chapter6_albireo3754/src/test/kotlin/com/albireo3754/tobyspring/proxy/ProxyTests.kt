package com.albireo3754.tobyspring.proxy

import org.junit.jupiter.api.Test

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
}