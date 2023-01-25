package com.gakk.noorlibrary.ui.adapter.umrahhajj

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.util.PLACE_HOLDER_16_9
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setImageFromUrl
import com.mcc.noor.model.umrah_hajj.UmrahHajjData
class UmrahPackAdapter(
    private var hajj_packlist: List<UmrahHajjData?>,
    private val onItemClickListener: OnItemClickListener?,
) :RecyclerView.Adapter<UmrahPackAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: View) :
        RecyclerView.ViewHolder(binding) {

        fun bind(umrah_hajj_pack: UmrahHajjData, position: Int) {
            binding.apply {

                val pack_img = binding.findViewById<ImageView>(R.id.pack_img)
                val progressBar = binding.findViewById<ProgressBar>(R.id.progressBar)

                setImageFromUrl(pack_img,umrah_hajj_pack.image,progressBar,PLACE_HOLDER_16_9)
                binding.findViewById<TextView>(R.id.titile).text = umrah_hajj_pack.packageName
                binding.findViewById<TextView>(R.id.subtitile).text = umrah_hajj_pack.packageDescription?.subHeading
                binding.findViewById<TextView>(R.id.price).text  = umrah_hajj_pack.packagePrice
                binding.handleClickEvent {
                    onItemClickListener?.onItemClick(position, umrah_hajj_pack)
                }
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<UmrahHajjData>() {
            override fun areItemsTheSame(
                oldItem: UmrahHajjData,
                newItem: UmrahHajjData
            ): Boolean = oldItem.umrahPackageId == newItem.umrahPackageId

            override fun areContentsTheSame(
                oldItem: UmrahHajjData,
                newItem: UmrahHajjData
            ): Boolean = oldItem.umrahPackageId == newItem.umrahPackageId

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_umrah_hajj_package,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = hajj_packlist[position]
        if (item != null) {
            holder.bind(item, position)
        }
    }

    override fun getItemCount(): Int {
        return hajj_packlist.size
    }


    interface OnItemClickListener {
        fun onItemClick(
            postion: Int,
            umrah_pack_list: UmrahHajjData
        )
    }
}