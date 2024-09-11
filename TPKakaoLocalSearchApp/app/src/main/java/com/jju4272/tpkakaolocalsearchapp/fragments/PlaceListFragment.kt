package com.jju4272.tpkakaolocalsearchapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jju4272.tpkakaolocalsearchapp.activities.MainActivity
import com.jju4272.tpkakaolocalsearchapp.adapter.PlaceListAdapter
import com.jju4272.tpkakaolocalsearchapp.databinding.FragmentPlaceListBinding

class PlaceListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    lateinit var binding:FragmentPlaceListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 메인액티비티에 있는 플레이스 정보를 가진 응답객체(KakaoSearchPlaceResponse)가 있다면 리사이클러뷰에 보여주기
        setPlaceListToRecyclerView()
    }

    private fun setPlaceListToRecyclerView(){
        val ma:MainActivity = activity as MainActivity
        if (ma.searchPlaceResponse==null) return

        binding.recyclerView.adapter = PlaceListAdapter(requireContext(), ma.searchPlaceResponse!!.documents)
    }

}