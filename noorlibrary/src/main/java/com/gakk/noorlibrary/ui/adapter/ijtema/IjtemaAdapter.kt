package com.gakk.noorlibrary.ui.adapter.ijtema

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.callbacks.DetailsCallBack
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

internal class IjtemaAdapter(
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

    inner class IjtemaViewHolder (layoutView: View): RecyclerView.ViewHolder(layoutView) {
        var view: View = layoutView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IjtemaViewHolder {
        when (viewType) {
            ITEM_VIDEO -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.layout_horizontal_list_v2,parent,false)
                return IjtemaViewHolder(view)

            }

            ITEM_LIVE -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.item_ijtema_live,parent,false)
                return IjtemaViewHolder(view)

            }

            else -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.item_ijtema,parent,false)
                return IjtemaViewHolder(view)

            }
        }
    }

    override fun onBindViewHolder(holder: IjtemaViewHolder, position: Int) {

        when (holder.itemViewType) {
            ITEM_VIDEO -> {
               val rvHorizontalList = holder.view.findViewById<RecyclerView>(R.id.rvHorizontalList)
                rvHorizontalList?.layoutManager =
                    LinearLayoutManager(
                       holder.itemView.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )

             rvHorizontalList?.adapter =
                    IslamicVideoOrPlayListAdapter(
                        videoList = videoList,
                        detailsCallBack = mDetailsCallBack,
                        videoCatId = catId,
                        videoSubCatId = subcatId
                    )
            }

            ITEM_LIVE -> {
                val  clIjtema: ConstraintLayout = holder.itemView.findViewById(R.id.clIjtema)
                val imageView = holder.view.findViewById<AppCompatImageView>(R.id.appCompatImageView6)
                val item = ImageFromOnline("btn_live_ijtema.png")
                Glide.with(holder.itemView.context).load(item.fullImageUrl).into(imageView)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE
              clIjtema?.handleClickEvent {

                    if (BaseApplication.IJTEMA_LIVE_VIDEO_ID?.isEmpty() == true) {
                        mIjtemaControl.showDialog()
                    } else {
                      clIjtema?.context?.startActivity(
                            Intent(
                            clIjtema?.context,
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
                val number :AppCompatTextView = holder.itemView.findViewById(R.id.number)
                val  tvTitle:AppCompatTextView = holder.itemView.findViewById(R.id.tvTitle)
                tvTitle.text =listItem.title

               number?.text =
                    TimeFormtter.getNumberByLocale(TimeFormtter.getNumber(serial).toString())

                holder.itemView.handleClickEvent {
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