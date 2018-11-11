package com.example.admin.echo1.Fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.admin.echo1.Activities.MyPhoto
import com.example.admin.echo1.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AboutUsFragment : Fragment() {

    var myPhoto: ImageView? = null
    var myName: TextView? = null
    var appDesc: TextView? = null

    var mContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity.title = "About us"
        var view = inflater.inflate(R.layout.fragment_about_us, container, false)
        myPhoto = view.findViewById(R.id.my_photo)
        myName = view.findViewById(R.id.my_name)
        appDesc = view.findViewById(R.id.app_desc)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        myPhoto?.setOnClickListener {
            startActivity(Intent(mContext, MyPhoto::class.java))
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }


}
