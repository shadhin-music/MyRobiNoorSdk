package com.gakk.noorlibrary.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.util.handleClickEvent


typealias DivisionCallbackFunc = (name:String,index:Int) -> Unit
internal class DivisionAdapter(val divisions:List<String>) :
    RecyclerView.Adapter<DivisionAdapter.DivisionViewHolder>() {
    var divisionCallbackFunc: DivisionCallbackFunc? = null
    var selectionControl: DivisionSelectionControl = DivisionSelectionControl()

    inner class DivisionViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        var view: View = layoutView
        fun setLayoutTag(layoutTag: Int?) {
            var tag: Int? = null
            tag?.let {
                selectionControl.getDivisionMap().remove(it)
            }
            tag = layoutTag
            tag?.let {
                selectionControl.getDivisionMap()[it] = view!!
            }
        }

    }







    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivisionAdapter.DivisionViewHolder {
        val binding=
            LayoutInflater.from(parent.context).inflate(
            R.layout.layout_roza_division,
            parent,
            false
        )
        return DivisionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DivisionViewHolder, position: Int) {
       val  tvDivisionName:AppCompatTextView = holder.view.findViewById(R.id.tvDivisionName)
        val  imgChecked:ImageView = holder.view.findViewById(R.id.imgChecked)
        holder.view?.let {
            tvDivisionName.setText(divisions.get(position))
        }
        holder.itemView.tag=position
        holder.setLayoutTag(position)
        when(selectionControl.getSelectedIndex()==position){
            true->imgChecked?.visibility=VISIBLE
            false->imgChecked?.visibility=INVISIBLE
        }
        holder.itemView.handleClickEvent {
            selectionControl.setSelectedIndex(position)
            selectionControl.updateSelection()

            divisionCallbackFunc?.invoke(divisions[position],position)
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

    private var divisionLayoutMap:HashMap<Int,View?>
    fun getDivisionMap()=divisionLayoutMap

    fun updateSelection(){

        Log.i("DivisionMap",divisionLayoutMap.size.toString())
        for(item in divisionLayoutMap){
              var layout=item.value
            val  imgChecked: ImageView? =layout?.findViewById(R.id.imgChecked)
              Log.i("DivisionMap","${layout!!.rootView.tag} && $selectedIndex")
              when(layout!!.rootView.tag== selectedIndex){
                  true->imgChecked?.visibility= VISIBLE
                  false->imgChecked?.visibility= INVISIBLE
              }

        }
    }

    init {
        selectedIndex=0
        divisionLayoutMap= HashMap()
    }
}