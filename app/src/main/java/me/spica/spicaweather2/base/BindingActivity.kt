package me.spica.spicaweather2.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import rikka.material.app.MaterialActivity


abstract class BindingActivity<ViewBindingType : ViewBinding> : MaterialActivity() {

    private var _binding: ViewBindingType? = null

    protected val viewBinding
        get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = setupViewBinding(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        initializer()
    }

    abstract fun initializer()

    abstract fun setupViewBinding(inflater: LayoutInflater): ViewBindingType

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}
