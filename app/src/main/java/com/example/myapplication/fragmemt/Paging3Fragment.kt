package com.example.myapplication.fragmemt

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R

class Paging3Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("PagingActivity","Paging3Fragment onCreateView")
        return inflater.inflate(R.layout.fragment_paging,container,false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("PagingActivity","Paging3Fragment onAttach")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("PagingActivity","Paging3Fragment onCreate")
        retainInstance=true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("PagingActivity","Paging3Fragment onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        Log.e("PagingActivity","Paging3Fragment onStart")
    }

    override fun onResume() {
        super.onResume()
        val str= arguments?.get("key")
        Log.e("PagingActivity", "Paging3Fragment onCreate----$str")
        Log.e("PagingActivity","Paging3Fragment onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e("PagingActivity","Paging3Fragment onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e("PagingActivity","Paging3Fragment onStop")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e("PagingActivity","Paging3Fragment onDetach")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("PagingActivity","Paging3Fragment onDestroyView")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.e("PagingActivity","Paging3Fragment onDestroy")
    }


}