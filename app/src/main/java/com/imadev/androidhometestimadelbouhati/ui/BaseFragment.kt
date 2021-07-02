package com.imadev.androidhometestimadelbouhati.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.imadev.androidhometestimadelbouhati.R
abstract class BaseFragment<V : ViewBinding> : Fragment() {
    private var _binding: V? = null
    protected val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createViewBinding(inflater,container)
        return binding.root
    }

    abstract fun createViewBinding(inflater: LayoutInflater,container: ViewGroup?):V
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}