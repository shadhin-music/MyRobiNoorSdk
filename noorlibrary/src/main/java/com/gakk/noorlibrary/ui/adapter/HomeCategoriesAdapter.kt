package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.MainCallback
import com.gakk.noorlibrary.model.BottomSheetItem
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.isNetworkConnected

@Keep
internal class HomeCategoriesAdapter(
    bottomSheetItemList: List<BottomSheetItem>, callback: MainCallback
) : RecyclerView.Adapter<HomeCategoriesAdapter.ViewHolder>() {
    var bottomSheetItemList: List<BottomSheetItem>
    private val mCallBack: MainCallback

    init {
        this.bottomSheetItemList = bottomSheetItemList
        mCallBack = callback
    }

    inner class ViewHolder
        (binding: View) : RecyclerView.ViewHolder(binding) {

        init {
            binding.let {

                it.handleClickEvent {
                    val title = FragmentDestinationMap.getDestinationFragmentName(
                        bottomSheetItemList.get(adapterPosition).title, it.context!!
                    )
                    if (isNetworkConnected(it.context)) {
                        title?.let { it1 ->
                            mCallBack.openDetailsActivityWithPageName(
                                it1
                            )
                        }
                    } else {
                        mCallBack.showToastMessage("Please check internet connection!")
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_categories_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bottomSheetItem = bottomSheetItemList[position]

        val title = holder.itemView.findViewById<AppCompatTextView>(R.id.tvCategory)
        val image = holder.itemView.findViewById<AppCompatImageView>(R.id.ivCategory)
        title.text = bottomSheetItem.title
        image.setImageResource(bottomSheetItem.resId)
    }

    override fun getItemCount(): Int {
        return bottomSheetItemList.size
    }
}

