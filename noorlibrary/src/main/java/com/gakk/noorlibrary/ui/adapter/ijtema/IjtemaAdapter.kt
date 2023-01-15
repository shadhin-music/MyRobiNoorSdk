package com.gakk.noorlibrary.ui.adapter.ijtema

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.ItemIjtemaBinding
import com.gakk.noorlibrary.databinding.ItemIjtemaLiveBinding
import com.gakk.noorlibrary.databinding.LayoutHorizontalListV2Binding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.ui.activity.YoutubePlayerActivity
import com.gakk.noorlibrary.ui.adapter.IslamicVideoOrPlayListAdapter
import com.gakk.noorlibrary.ui.fragments.LiteratureItemClickCallBack
import com.gakk.noorlibrary.ui.fragments.ijtema.IjtemaControl
import com.gakk.noorlibrary.util.IS_IJTEMA_LIVE_VIDEO
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.handleClickEvent

class IjtemaAdapter(
    val literatureList: MutableList<Literature>,
    detailsCallBack: DetailsCallBack,
    itemClickCallBack: LiteratureItemClickCallBack?,
    val videoList: MutableList<Data>,
    val catId: String,
    val subcatId: String,
    ijtemaControl: IjtemaControl
) :
    RecyclerView.Adapter<IjtemaAdapter.IjtemaViewHolder>() {

    val ITEM_VIDEO = 0
    val ITEM_LIVE = 1
    val ITEM_CONTENT = 2
    val mDetailsCallBack = detailsCallBack
    var ijtemaItemCount: Int
    val mItemClickCallBack = itemClickCallBack
    val mIjtemaControl = ijtemaControl

    init {
        ijtemaItemCount = literatureList.size
    }

    inner class IjtemaViewHolder : RecyclerView.ViewHolder {
        var listBinding: LayoutHorizontalListV2Binding? = null

        constructor(binding: LayoutHorizontalListV2Binding) : super(binding.root) {
            listBinding = binding
        }

        var bindinglive: ItemIjtemaLiveBinding? = null

        constructor(itemView: ItemIjtemaLiveBinding) : super(itemView.root) {
            bindinglive = itemView
        }

        var bindingIjtema: ItemIjtemaBinding? = null

        constructor(itemView: ItemIjtemaBinding) : super(itemView.root) {
            bindingIjtema = itemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IjtemaViewHolder {
        when (viewType) {
            ITEM_VIDEO -> {
                val binding: LayoutHorizontalListV2Binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_horizontal_list_v2,
                    parent,
                    false
                )
                return IjtemaViewHolder(binding)
            }

            ITEM_LIVE -> {
                val binding: ItemIjtemaLiveBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_ijtema_live,
                    parent,
                    false
                )
                return IjtemaViewHolder(binding)
            }

            else -> {
                val binding: ItemIjtemaBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_ijtema,
                    parent,
                    false
                )
                return IjtemaViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: IjtemaViewHolder, position: Int) {

        when (holder.itemViewType) {
            ITEM_VIDEO -> {

                holder.listBinding?.rvHorizontalList?.layoutManager =
                    LinearLayoutManager(
                        holder.listBinding?.root?.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )

                holder.listBinding?.rvHorizontalList?.adapter =
                    IslamicVideoOrPlayListAdapter(
                        videoList = videoList,
                        detailsCallBack = mDetailsCallBack,
                        videoCatId = catId,
                        videoSubCatId = subcatId
                    )
            }

            ITEM_LIVE -> {
                holder.bindinglive?.item = ImageFromOnline("btn_live_ijtema.png")
                holder.bindinglive?.clIjtema?.handleClickEvent {

                    if (BaseApplication.IJTEMA_LIVE_VIDEO_ID?.isEmpty() == true) {
                        mIjtemaControl.showDialog()
                    } else {
                        holder.bindinglive?.clIjtema?.context?.startActivity(
                            Intent(
                                holder.bindinglive?.clIjtema?.context,
                                YoutubePlayerActivity::class.java
                            ).apply {
                                putExtra(IS_IJTEMA_LIVE_VIDEO, true)
                            }
                        )
                    }
                }
            }
            ITEM_CONTENT -> {
                val listItem = literatureList.get(position - 2)
                val serial = (position - 2) + 1

                holder.bindingIjtema?.literature = listItem
                holder.bindingIjtema?.number?.text =
                    TimeFormtter.getNumberByLocale(TimeFormtter.getNumber(serial).toString())

                holder.bindingIjtema?.root?.handleClickEvent {
                    mItemClickCallBack?.goToListeratureDetailsFragment(position - 2, false)
                }
            }

            else -> {

            }
        }

    }

    override fun getItemCount(): Int {
        return 2 + ijtemaItemCount

    }

    fun getIjtemaList(): MutableList<Literature> {
        return literatureList
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                ITEM_VIDEO
            }
            1 -> {
                ITEM_LIVE
            }
            else -> {
                ITEM_CONTENT
            }
        }
    }
}