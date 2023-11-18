package me.spica.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import me.spica.base.tools.ActivityFinishUtil


abstract class BindingActivity<ViewBindingType : ViewBinding> : AppCompatActivity() {

    companion object{
        private val activityFinishUtil = ActivityFinishUtil()
    }

    private var _binding: ViewBindingType? = null

    protected val viewBinding
        get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = setupViewBinding(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        initializer()
        activityFinishUtil.addActivity(this)
    }

    abstract fun initializer()


    fun finishOtherActivity() {
        activityFinishUtil.finishOtherAllActivity(this)
    }

    abstract fun setupViewBinding(inflater: LayoutInflater): ViewBindingType

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
        activityFinishUtil.removeActivity(this)
    }


}
