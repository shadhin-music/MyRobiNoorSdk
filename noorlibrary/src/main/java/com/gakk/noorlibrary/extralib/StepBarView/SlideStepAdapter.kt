package com.gakk.noorlibrary.extralib.StepBarView

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R

class SlideStepAdapter(
    ctx: Context,
    private val totalStep: Array<Int?>,
    private var activeStep:Int,
    private var stepRadius:Float = 40F,
    private var stepHeight:Float = 2.5F,
    private var stepTextSize:Float = 24F,
    private val enableTitile:Boolean

    ):

    RecyclerView.Adapter<SlideStepAdapter.ViewHolder>() {
    var inflater: LayoutInflater
    private val mContext: Context
    private lateinit var mCallback: slider_step_item_clickListner
    private val all_margin = stepRadius / totalStep.size


    init {
        inflater = LayoutInflater.from(ctx)
        mContext = ctx
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.slide_step_item, parent, false)
        return ViewHolder(view)
    }

     fun update_active_step(step:Int)
    {
        activeStep = step

    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var left_line = 0F
        var right_line = 0F
        val total_step_count = totalStep.size

        val display: DisplayMetrics? = mContext.resources.displayMetrics
        var width: Int = display?.widthPixels ?: 0
        width /= total_step_count

        holder.count.text = (position+1).toString()
        holder.count.layoutParams = LinearLayout.LayoutParams(toDips(stepRadius).toInt(), toDips(stepRadius).toInt())
        holder.count.textSize = toDips(stepTextSize)
        //holder.count.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,stepTextSize,display)

        //Log.e("STEPPER screen size",width.toString())
       // Log.e("STEPPER total",total_step_count.toString())
       // Log.e("STEPPER position",position.toString())



        if(position ==  0) {
            holder.stepLine.visibility = View.GONE
            holder.stepLine1.visibility = View.VISIBLE
            holder.stepLine1.alpha = 0.3F
            right_line = ((width/total_step_count) - all_margin).toFloat()
            left_line = 0F
            //setMargins(holder.stepText,0,0,(right_line).toInt(),0)

            Log.e("STEPPER 1st right",right_line.toString())

            //holder.stepText.setPadding(0,0,0,0)
        }
        else if(position == 1)
        {
            holder.stepLine.visibility = View.GONE
            holder.stepLine1.visibility = View.VISIBLE

            right_line =  (((width/total_step_count) /2) - (all_margin/2)).toFloat()
            left_line = 0F
            //setMargins(holder.stepText,0,0,0,0)
            //holder.stepText.setPadding(0,0,0,0)
            Log.e("STEPPER 2nd left",left_line.toString())
            Log.e("STEPPER 2nd right",right_line.toString())

        }
        else if(position == (total_step_count-1))
        {
            holder.stepLine1.visibility = View.GONE
            holder.stepLine.visibility = View.VISIBLE
            left_line = (((width/total_step_count) /2) - all_margin).toFloat()
            right_line = 0F
            Log.e("STEPPER 3rd left",left_line.toString())

            setMargins(holder.stepText,toDips(left_line ).toInt(),0,0,0)

        }
        else {

            holder.stepLine1.visibility = View.GONE

                if(position>2) {
                    holder.stepLine.visibility = View.VISIBLE
                    left_line = (((width / total_step_count) / 2) - all_margin).toFloat()
                }
            else {
                    holder.stepLine.visibility = View.GONE
                    left_line = 0F
                }

            right_line = 0F
            //right_line =  ((width/total_step_count) /2).toFloat()

            //Log.e("STEPPER other left",left_line.toString())
            //Log.e("STEPPER other right",right_line.toString())

        }

        /*if((left_line + right_line)<50F)
        {
            if(left_line != 0F && left_line<50)
                left_line = 50F

            if(right_line != 0F && right_line<50F)
                right_line = 50F
        }*/


        if(!enableTitile)
        {
                right_line = 50F
                left_line = 50F
        }

        //left_line = if(left_line <50F) 50F else left_line
        //right_line = if(right_line <50F) 50F else right_line


        holder.stepLine.layoutParams = LinearLayout.LayoutParams(toDips(left_line).toInt(),toDips(stepHeight).toInt())
        holder.stepLine1.layoutParams = LinearLayout.LayoutParams(toDips(right_line).toInt(),toDips(stepHeight).toInt())


        if(position <= activeStep) {

            holder.rootItem.alpha = 1F
            holder.stepText.alpha = 1F
            holder.count.alpha = 1F
            holder.stepLine.alpha = 0.4F
            holder.stepLine1.alpha = 0.4F

            if(activeStep == 0 && position == 0)
                holder.stepLine1.alpha = 0.14F
            else if(activeStep == 1 && position == 1) {
                holder.stepLine1.alpha = 0.14F
                holder.stepLine.alpha = 1F
            }
            else
            {
                holder.stepText.alpha =1F
                holder.stepLine1.alpha =1F
                holder.stepLine.alpha =1F
            }

        }
        else {

            holder.rootItem.alpha = 0.4F
            holder.stepText.alpha = 0.4F
            holder.stepLine.alpha = 0.4F
            holder.stepLine1.alpha = 0.4F

            if(position>=2)
                holder.stepLine.alpha = 0.4F
            if(position ==0) {
                holder.stepLine1.alpha = 0.4F
                holder.stepLine.alpha = 0.4F
            }

        }

        if(enableTitile)
        {
            val titles = mContext.resources.getStringArray(R.array.hajj_title)

                holder.stepText.text = titles[position].toString()
                holder.stepText.visibility = View.VISIBLE

        }
        else
            holder.stepText.visibility = View.GONE

        //holder.rootItem.bringChildToFront(holder.stepText)

    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = view.layoutParams as ViewGroup.MarginLayoutParams
            if(left>0)
            p.marginStart = left
            p.marginEnd = right
            view.requestLayout()
        }
    }

    override fun getItemCount(): Int {
        return totalStep.size
    }

     interface slider_step_item_clickListner
    {
        fun slider_step_clicked(step: Int)

    }

    fun setOnItemClick(mCallback: slider_step_item_clickListner?) {
        if (mCallback != null) {
            this.mCallback = mCallback
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var count: TextView
         var stepLine : View
         var stepLine1 : View
         var rootItem: LinearLayout
         var stepText:TextView


        init {
            count = itemView.findViewById(R.id.count)
            stepLine = itemView.findViewById(R.id.stepLine)
            stepLine1 = itemView.findViewById(R.id.stepLine1)
            rootItem = itemView.findViewById(R.id.rootview)
            stepText = itemView.findViewById(R.id.step_text)

            count.setOnClickListener {

                mCallback.slider_step_clicked(absoluteAdapterPosition)

            }
        }
    }

    fun toDips(value:Float): Float
    {
         return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value , mContext.resources.displayMetrics);
    }
}