package me.spica.spicaweather2.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding


abstract class BindingActivity<ViewBindingType : ViewBinding> : AppCompatActivity() {

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
