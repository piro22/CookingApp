package it.insubria.cookingapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoritesFragment : Fragment(), RecyclerViewInterface {
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

        val ret = inflater.inflate(R.layout.fragment_favorites, container, false)


        ricetteModel.clear()
        // Inflate the layout for this fragment
        val dataModel = ViewModelProvider(requireActivity()).get(DataModel::class.java)
        val dbHelper = dataModel.dbHelper
        val dbr = dbHelper!!.readableDatabase


        val cursor = dbr.rawQuery("SELECT * FROM ricetta WHERE preferito=1", null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex("id")
                val nomeIndex = cursor.getColumnIndex("nome")
                val pathIndex = cursor.getColumnIndex("pathFoto")
                val preparazioneIndex = cursor.getColumnIndex("preparazione")
                val porzioniIndex = cursor.getColumnIndex("porzioni")
                val tempoIndex = cursor.getColumnIndex("tempo_di_preparazione")
                val difficoltaIndex = cursor.getColumnIndex("difficolta")
                val tipologiaIndex = cursor.getColumnIndex("tipologia")
                val portataIndex = cursor.getColumnIndex("portata")
                val dietaIndex = cursor.getColumnIndex("dieta")
                val preferitoIndex = cursor.getColumnIndex("preferito")
                val etnicitaIndex = cursor.getColumnIndex("etnicita")

                val tempRic: RicetteModel = RicetteModel(
                    cursor.getInt(idIndex),
                    cursor.getString(nomeIndex),
                    cursor.getString(pathIndex),
                    cursor.getString(preparazioneIndex),
                    cursor.getInt(porzioniIndex),
                    cursor.getInt(tempoIndex),
                    cursor.getString(difficoltaIndex),
                    cursor.getString(tipologiaIndex),
                    cursor.getString(portataIndex),
                    cursor.getString(dietaIndex),
                    cursor.getString(etnicitaIndex),
                    cursor.getInt(preferitoIndex)
                )

                ricetteModel.add(tempRic)

            } while (cursor.moveToNext())
            cursor.close()
        }




        val recView : RecyclerView = ret.findViewById(R.id.mRecyclerView)

        //POSSO METTERE QUI UNA FUNZIONE PER RIEMPIRE LA LISTA ricetteModel

        var adapter : RecyclerViewAdapter = RecyclerViewAdapter(requireContext(), ricetteModel, this)
        recView.adapter = adapter
        recView.layoutManager = LinearLayoutManager(requireContext())


        val fab = ret.findViewById<FloatingActionButton>(R.id.btn_fab)


        fab.setOnClickListener {
            val intent = Intent(requireActivity(), newRecipeActivity::class.java)
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
         * @return A new instance of fragment FavoritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(position: Int) {
        val ricetta = RicetteModel(
            ricetteModel.get(position).id,
            ricetteModel.get(position).nome,
            ricetteModel.get(position).pathFoto,
            ricetteModel.get(position).preparazione,
            ricetteModel.get(position).porzioni,
            ricetteModel.get(position).tempo,
            ricetteModel.get(position).difficolta,
            ricetteModel.get(position).tipologia,
            ricetteModel.get(position).portata,
            ricetteModel.get(position).dieta,
            ricetteModel.get(position).etnicita,
            ricetteModel.get(position).preferito)

        val ricettaViewModel = ViewModelProvider(requireActivity()).get(DataModel::class.java)
        ricettaViewModel.ricetta = ricetta

//        val detail: DetailFragment = DetailFragment()
//        detail.setRicetta(ricetta)

        parentFragmentManager.beginTransaction().replace(R.id.fragment_container, DetailFragment()).addToBackStack(null).commit()
    }
}