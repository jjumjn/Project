package com.jju4272.tpkakaolocalsearchapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jju4272.tpkakaolocalsearchapp.data.Place
import com.jju4272.tpkakaolocalsearchapp.databinding.RecyclerItemListFragmentBinding

class PlaceListAdapter(val context: Context, val documents:List<Place>) : Adapter<PlaceListAdapter.VH>(){

    inner class VH(val binding:RecyclerItemListFragmentBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RecyclerItemListFragmentBinding.inflate(LayoutInflater.from(context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int {
        return documents.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val place:Place = documents[position]

        holder.binding.tvPlaceName.text = place.place_name
        holder.binding.tvDistance.text = place.distance + "m"
        holder.binding.tvAddress.text = if(place.road_address_name=="") place.address_name else place.road_address_name

    }

}