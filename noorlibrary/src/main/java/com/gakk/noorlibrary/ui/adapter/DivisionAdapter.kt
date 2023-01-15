package com.gakk.noorlibrary.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.LayoutRozaDivisionBinding
import com.gakk.noorlibrary.databinding.RowListItemDuaBinding
import com.gakk.noorlibrary.util.Util
import com.gakk.noorlibrary.util.handleClickEvent


typealias DivisionCallbackFunc = (name:String,index:Int) -> Unit
class DivisionAdapter(val divisions:List<String>) :
    RecyclerView.Adapter<DivisionAdapter.DivisionViewHolder>() {
    var divisionCallbackFunc:DivisionCallbackFunc?= null
    var selectionControl:DivisionSelectionControl = DivisionSelectionControl()
    inner class DivisionViewHolder:RecyclerView.ViewHolder{

        var binding:LayoutRozaDivisionBinding?=null
        var tag: Int? = null
        constructor(binding: LayoutRozaDivisionBinding):super(binding.root){
            this.binding=binding
            this.binding?.root?.let {

                it.handleClickEvent {
                    selectionControl.setSelectedIndex(adapterPosition)
                    selectionControl.updateSelection()

                    divisionCallbackFunc?.invoke(divisions[absoluteAdapterPosition],absoluteAdapterPosition)
                }
            }
        }

        fun setLayoutTag(layoutTag: Int?) {
            tag?.let {
                selectionControl.getDivisionMap().remove(it)
            }
            tag = layoutTag
            tag?.let {
                selectionControl.getDivisionMap()[it] = binding
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivisionViewHolder {
        val binding: LayoutRozaDivisionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_roza_division,
            parent,
            false
        )
        return DivisionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DivisionViewHolder, position: Int) {
        holder.binding?.let {
            it.tvDivisionName.setText(divisions.get(position))
        }
        holder.binding?.root?.tag=position
        holder.setLayoutTag(position)
        when(selectionControl.getSelectedIndex()==position){
            true->holder.binding?.imgChecked?.visibility=VISIBLE
            false->holder.binding?.imgChecked?.visibility=INVISIBLE
        }
    }

    override fun getItemCount()=divisions.size
}

class DivisionSelectionControl{

    private var selectedIndex:Int
    fun getSelectedIndex()=selectedIndex
    fun setSelectedIndex(value:Int){
        selectedIndex=value
    }

    private var divisionLayoutMap:HashMap<Int,LayoutRozaDivisionBinding?>
    fun getDivisionMap()=divisionLayoutMap

    fun updateSelection(){
        Log.i("DivisionMap",divisionLayoutMap.size.toString())
        for(item in divisionLayoutMap){
              var layout=item.value
              Log.i("DivisionMap","${layout!!.root.tag} && $selectedIndex")
              when(layout!!.root.tag== selectedIndex){
                  true->layout?.imgChecked?.visibility= VISIBLE
                  false->layout?.imgChecked?.visibility= INVISIBLE
              }

        }
    }

    init {
        selectedIndex=0
        divisionLayoutMap= HashMap()
    }
}