import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inu.bus.R
import com.inu.bus.recycler.RecyclerAdapterSchoolBus
import kotlinx.android.synthetic.main.fragment_swipepull_recycler.*

class SchoolBusFragment : android.support.v4.app.Fragment(){

    private lateinit var mStrBusStop : String
    private lateinit var mContext : Context
    val mAdapter by lazy { RecyclerAdapterSchoolBus("school") }

    companion object {
        fun newInstance(context: Context, stopName: String): SchoolBusFragment {
            val fragment = SchoolBusFragment()
            fragment.mStrBusStop = stopName
            fragment.mContext = context
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_swipepull_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_fragment_node_arrival_recycler.adapter = mAdapter

    }

}