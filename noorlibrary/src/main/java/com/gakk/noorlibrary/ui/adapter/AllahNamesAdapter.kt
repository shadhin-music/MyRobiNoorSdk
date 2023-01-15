package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.LayoutAllahNameBinding
import com.gakk.noorlibrary.databinding.LayoutAllahNameHeaderBinding
import com.gakk.noorlibrary.model.names.Data
import com.gakk.noorlibrary.util.FragmentProvider
import com.gakk.noorlibrary.util.PAGE_ALLAH_NAME_DETAILS
import com.gakk.noorlibrary.util.handleClickEvent


const val HEADER_ALLAH_NAME=0
const val ALLAH_NAME=1

class AllahNamesAdapter(list:List<Data>,detailsCallBack: DetailsCallBack): RecyclerView.Adapter<AllahNamesAdapter.AllahNamesViewHolder>() {

    val mList=list
    val mDetailsCallBack=detailsCallBack

    inner class AllahNamesViewHolder:RecyclerView.ViewHolder{

        var header:LayoutAllahNameHeaderBinding?=null
        constructor(binding: LayoutAllahNameHeaderBinding):super(binding.root){
            header=binding
        }

        var content:LayoutAllahNameBinding?=null
        constructor(binding: LayoutAllahNameBinding):super(binding.root){
            content=binding
            content?.let { it ->
                it.root.handleClickEvent {
                    
                    val fragment=FragmentProvider.getFragmentByName(
                        PAGE_ALLAH_NAME_DETAILS,
                        detailsActivityCallBack = mDetailsCallBack,
                        selectedLiteratureIndex =adapterPosition-1,
                        listNamesOfAllah = mList
                    )
                    mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllahNamesViewHolder {
            when(viewType){
                HEADER_ALLAH_NAME->{
                    var binding:LayoutAllahNameHeaderBinding=DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.layout_allah_name_header,
                        parent,
                        false
                    )
                    return  AllahNamesViewHolder(binding)
                }
                else->{
                    var binding:LayoutAllahNameBinding=DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.layout_allah_name,
                        parent,
                        false
                    )
                    return  AllahNamesViewHolder(binding)
                }
            }

    }

    override fun getItemViewType(position: Int)=when(position){
        0-> HEADER_ALLAH_NAME
        else-> ALLAH_NAME
    }

    override fun onBindViewHolder(holder: AllahNamesViewHolder, position: Int) {
        holder.content?.allahName=mList.get(position-1)
    }

    override fun getItemCount(): Int {
        return mList.size+1
    }
}