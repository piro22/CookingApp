package it.insubria.cookingapp

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), RecyclerViewInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val ricetteModel: ArrayList<RicetteModel> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)



        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val ret = inflater.inflate(R.layout.fragment_home, container, false)

        val ricetta = RicetteModel(1, "pasta al pesto", "content://media/external/images/media/1000000028", "cuocio la pasta e dopo 10/12 minuti la scolo, la metto in pentola e ci metto il pesto", 5, 20, "*", "Pasta","Primo", "Onnivora", true)
        ricetteModel.add(ricetta)
        val ricetta2 = RicetteModel(1, "Tortino al cioccolato", "default", "non so come si faccia bro, non sono un pasticcere", 5, 100, "***", "Tortino","Dolce", "Onnivora", true)
        ricetteModel.add(ricetta2)

        val recView : RecyclerView = ret.findViewById(R.id.mRecyclerView)

        //POSSO METTERE QUI UNA FUNZIONE PER RIEMPIRE LA LISTA ricetteModel

        var adapter : RecyclerViewAdapter = RecyclerViewAdapter(requireContext(), ricetteModel, this)
        recView.adapter = adapter
        recView.layoutManager = LinearLayoutManager(requireContext())


        val fab = ret.findViewById<FloatingActionButton>(R.id.btn_fab)


        fab.setOnClickListener {
            val intent =Intent(requireActivity(), newRecipeActivity::class.java)
            startActivity(intent)
        }




        return ret





    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }




    }





}