package it.insubria.cookingapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
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

    private val ricetteModel: ArrayList<RicetteModel> = ArrayList()
    private var arrayListaPortate: MutableList<String> = mutableListOf()

    private lateinit var dataModel: DataModel
    private lateinit var listaPortate: AutoCompleteTextView
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var recView : RecyclerView

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
        dataModel = ViewModelProvider(requireActivity()).get(DataModel::class.java)

        recView = ret.findViewById(R.id.mRecyclerView)
        //leggo il db e prendo le ricette
        readFromDB()

        val fab = ret.findViewById<FloatingActionButton>(R.id.btn_fab)

        fab.setOnClickListener {
            val intent =Intent(requireActivity(), newRecipeActivity::class.java)
            startActivity(intent)
        }


        //POPOLARE LA TENDINA PER LE PORTATE
        listaPortate = ret.findViewById(R.id.filtroPortate)
        popolaTendina()

        //------------------------------------------------------------------------------------------------------------------------------
        //PER FILTRI

        //filtro nome
        val editText: EditText = ret.findViewById(R.id.txtRicerca)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filterNome(s.toString())
                //Log.d("FILTRO FILTRO FILTRO FILTRO FILTRO FILTRO FILTRO ", "${s.toString()}")
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        //filtro portata
        listaPortate.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            if (selectedItem.equals("- - -")) {
                adapter.filterPortata("")
            } else {
                adapter.filterPortata(selectedItem)
            }
        }
        //-------------------------------------------------------------------------------------------------------------------------------

        return ret
    }

    private fun readFromDB() {
        ricetteModel.clear()

        val dbHelper = dataModel.dbHelper
        val dbr = dbHelper!!.readableDatabase


        val cursor = dbr.rawQuery("SELECT * FROM ricetta", null)

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

        adapter = RecyclerViewAdapter(requireContext(), ricetteModel, this)
        recView.adapter = adapter
        recView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun popolaTendina(){
        //POPOLO TENDINA FILTRO PORTATE---------------------------------------------------------------
        var dbHelper = dataModel.dbHelper
        var dbr = dbHelper!!.readableDatabase
        val cursorPort = dbr.rawQuery("SELECT portata FROM portate", null)

        arrayListaPortate.clear()

        if (cursorPort != null && cursorPort.moveToFirst()) {
            do {
                val portataIndex = cursorPort.getColumnIndex("portata")
                if (portataIndex >= 0) {
                    val p = cursorPort.getString(portataIndex)
                    arrayListaPortate.add(p)
                }
            } while (cursorPort.moveToNext())
            cursorPort.close()
        }


        //creo un adapter per passare i valori dell'array delle portate all'interno della tendina
        val adapterTendina = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            arrayListaPortate.toTypedArray()
        )
        //per aggiornare la vista(tendina)
        listaPortate.setAdapter(adapterTendina)
        //------------------------------------------------------------------------------------------------
    }

    override fun onResume() {
        super.onResume()
        popolaTendina()
        readFromDB()
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