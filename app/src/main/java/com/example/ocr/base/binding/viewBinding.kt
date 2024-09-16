package com.example.ocr.base.binding

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : ViewBinding> AppCompatActivity.viewBinding() =
    ActivityViewBindingDelegate(T::class.java, this)

class ActivityViewBindingDelegate<T : ViewBinding>(
    private val bindingClass: Class<T>,
    val activity: AppCompatActivity,
) : ReadOnlyProperty<AppCompatActivity, T> {

    private var binding: T? = null

    override fun getValue(
        thisRef: AppCompatActivity,
        property: KProperty<*>,
    ): T {
        binding?.let {
            return it
        }

        val lifecycle = thisRef.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Cannot access viewBinding activity lifecycle is ${lifecycle.currentState}")
        }

         val bindMethod = bindingClass.getMethod("inflate", LayoutInflater::class.java)
        binding = bindMethod.invoke(null, thisRef.layoutInflater).cast<T>()

        return binding!!
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T> Any.cast(): T = this as T



inline fun <reified T : ViewBinding> Fragment.viewBinding() = FragmentViewBindingDelegate(T::class.java)

class FragmentViewBindingDelegate<T : ViewBinding>(
    private val bindingClass: Class<T>
) : ReadOnlyProperty<Fragment, T> {
    private var binding: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        binding?.let { return it }

        if (!thisRef.viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Cannot access viewBinding. View lifecycle is ${thisRef.viewLifecycleOwner.lifecycle.currentState}!")
        }

        binding = bindingClass.getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, LayoutInflater.from(thisRef.requireContext())) as T

        thisRef.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                binding = null
            }
        })

        return binding!!
    }
}