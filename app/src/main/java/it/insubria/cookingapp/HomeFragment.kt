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


        ricetteModel.clear()
//        var ricetta = RicetteModel(1,
//            "pasta al pesto",
//            "content://media/external/images/media/1000000028",
//            "cuocio la pasta e dopo 10/12 minuti la scolo, la metto in pentola e ci metto il pesto",
//            5,
//            20,
//            "*",
//            "Pasta",
//            "Primo",
//            "Onnivora",
//            true)
//        ricetteModel.add(ricetta)
//        ricetta = RicetteModel(2, "Tortino al cioccolatoatoato", "default", "non so come si faccia bro, non sono un pasticcere", 5, 100, "***", "Tortino","Dolce", "Onnivora", true)
//        ricetteModel.add(ricetta)
//        val ricetta = RicetteModel(3,
//            "Tiramisù",
//            "content://media/external/images/media/1000000028",
//            "Per preparare il tiramisù preparate il caffé con la moka per ottenerne 300 g, poi zuccherate a piacere (noi abbiamo messo un cucchiaino) e lasciatelo raffreddare in una ciotolina bassa e ampia. Separate le uova dividendo gli albumi dai tuorli 1, ricordando che per montare bene gli albumi non dovranno presentare alcuna traccia di tuorlo. Montate i tuorli con le fruste elettriche, versando solo metà dose di zucchero 2. Non appena il composto sarà diventato chiaro e spumoso, e con le fruste ancora in funzione, potrete aggiungere il mascarpone, poco alla volta 3. ",
//            5,
//            35,
//            "***",
//            "Dolce",
//            "Sempre Buono",
//            "Onnivora",
//            true)
//        ricetteModel.add(ricetta)

        val dataModel = ViewModelProvider(requireActivity()).get(DataModel::class.java)
        val dbHelper = dataModel.dbHelper
        val dbr = dbHelper!!.readableDatabase


        val cursor = dbr.rawQuery("SELECT * FROM ricetta", null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
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

    override fun onItemClick(position: Int) {
        //Toast.makeText(requireContext(), "LOGGED OUT", Toast.LENGTH_SHORT).show()

        val ricetta = RicetteModel(
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