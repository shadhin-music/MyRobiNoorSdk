package com.gakk.noorlibrary.extralib.country_code_picker

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.loadJSONFromAsset
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class ccp(
    private val on_ccp_click: OnCcpClickListener,
): ccpAdapter.OnItemClickListener {
    var dialog: Dialog? = null
    private  var ccp_adapter : ccpAdapter ? = null
    private lateinit var mContext: Context

    fun setup_ccp(getContext:Context, ccpBtn: LinearLayout)
    {
        mContext = getContext

        ccpBtn.handleClickEvent {

            val display: DisplayMetrics? = mContext.resources.displayMetrics
            var width: Int = display?.widthPixels ?: 0
            var height: Int = display?.heightPixels ?: 0
            width -= (width * 10) / 100
            height -= (height * 10) / 100

            // Initialize dialog
            dialog = mContext?.let { Dialog(it) };

            // set custom dialog
            dialog?.setContentView(R.layout.dialog_ccp);


            // set transparent background
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));


            // Initialize and assign variable
            val search_ccp = dialog?.findViewById<EditText>(R.id.search_ccp)
            val listview = dialog?.findViewById<RecyclerView>(R.id.list_view)
            val dismissBtn = dialog?.findViewById<ImageButton>(R.id.closeBtn)

            dismissBtn?.handleClickEvent {
                dialog?.dismiss()
            }

            // Initialize array adapter

            val data: ArrayList<CCPmodel> = fetch_country_json()

            ccp_adapter = mContext.let { ccpAdapter(data, this@ccp) }

            val linearLayoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)

            val dividerItemDecoration = DividerItemDecoration(
                mContext,
                linearLayoutManager.getOrientation()
            )

            listview?.addItemDecoration(dividerItemDecoration)

            listview?.layoutManager = linearLayoutManager

            dialog?.window?.setLayout(width, height);

            // set adapter
            listview?.adapter = ccp_adapter

            // show dialog
            dialog?.show()

            search_ccp?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    ccp_adapter?.filter?.filter(s)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }

    }


    interface OnCcpClickListener {
        fun onItemClick(
            postion: Int,
            ccp_list: ArrayList<CCPmodel>
        )
    }



    override fun onItemClick(postion: Int, ccp_list: ArrayList<CCPmodel>) {
        dialog?.dismiss()
        on_ccp_click.onItemClick(postion,ccp_list)
    }

    private fun fetch_country_json(): ArrayList<CCPmodel> {
        val ccpArray : ArrayList<CCPmodel> = ArrayList()
        val countryIS: InputStream? = mContext.assets.open("countrys.json")
        val json = countryIS?.let { loadJSONFromAsset(it) }
        json?.let {

            val countryJsonArray: JSONArray = JSONArray(it)

            for (item in 0 until  countryJsonArray.length()) {

                val jsonInfo: String? = countryJsonArray.getString(item)
                val `object` = jsonInfo?.let { JSONObject(it) }
                if (`object` != null) {

                    val tempArray = CCPmodel(
                        `object`.getString("countryCode"),
                        `object`.getString("countryName"),
                        `object`.getString("dial_code")
                    )

                    ccpArray.add(tempArray)
                }

            }

        }

        return ccpArray

    }
}