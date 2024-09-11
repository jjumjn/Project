package com.jju4272.tpkakaolocalsearchapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jju4272.tpkakaolocalsearchapp.databinding.FragmentPlaceFavoriteBinding
import com.jju4272.tpkakaolocalsearchapp.databinding.FragmentPlaceListBinding

class PlaceFavoriteFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    lateinit var binding:FragmentPlaceFavoriteBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 여기서 작업 시작
    }

}