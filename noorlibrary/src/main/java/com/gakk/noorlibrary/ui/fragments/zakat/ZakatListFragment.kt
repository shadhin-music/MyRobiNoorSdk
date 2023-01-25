package com.gakk.noorlibrary.ui.fragments.zakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.roomdb.RoomRepository
import com.gakk.noorlibrary.data.roomdb.ZakatRoomDatabase
import com.gakk.noorlibrary.model.zakat.ZakatDataModel
import com.gakk.noorlibrary.ui.adapter.ZakatListAdapter
import com.gakk.noorlibrary.ui.fragments.ZakatCalculationObserver
import com.gakk.noorlibrary.viewModel.ZakatViewModel
import kotlinx.coroutines.launch
import java.io.Serializable

internal class ZakatListFragment : Fragment(), DeleteOperation {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var viewModel: ZakatViewModel
    private lateinit var repository: RoomRepository
    private var adapter: ZakatListAdapter? = null
    private lateinit var listZakat: RecyclerView

    val database by lazy { ZakatRoomDatabase.getDatabase(requireContext()) }
    val repositoryRoom by lazy { RoomRepository(database.zakatDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ZakatListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_zakat_list,
            container, false
        )

        repository = repositoryRoom

        listZakat = view.findViewById(R.id.listZakat)

        viewModel = ViewModelProvider(
            this@ZakatListFragment,
            ZakatViewModel.FACTORY(repository)
        ).get(ZakatViewModel::class.java)


        viewModel.allData.observe(viewLifecycleOwner) {

            adapter = ZakatListAdapter(it, this)
            listZakat.adapter = adapter
            ZakatCalculationObserver.attatchAdapter(adapter)
        }
        return view
    }


    override fun deleteData(data: ZakatDataModel) {
        lifecycleScope.launch {
            viewModel.delete(data)
        }
    }

}

interface DeleteOperation : Serializable {
    fun deleteData(data: ZakatDataModel)
}