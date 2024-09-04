package com.example.ocr.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */
abstract class BaseFragment<T : ViewBinding>(bindingClass: Class<T>) : Fragment() {

    protected val binding: T by FragmentViewBindingDelegate(bindingClass)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObserver()
    }
    abstract fun setupUi()

    abstract fun setupObserver()

    private inner class FragmentViewBindingDelegate(
        private val bindingClass: Class<T>
    ) : ReadOnlyProperty<Fragment, T> {
        private var binding: T? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            binding?.let { return it }

            val lifecycle = thisRef.viewLifecycleOwner.lifecycle
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                throw IllegalStateException("Cannot access viewBinding. View lifecycle is ${lifecycle.currentState}!")
            }

            val inflater = thisRef.layoutInflater
            val container = thisRef.view?.parent as? ViewGroup
            binding = bindingClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
                .invoke(null, inflater, container, false) as T

            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    binding = null
                }
            })

            return binding!!
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
