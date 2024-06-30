package it.insubria.cookingapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var listaElimina: List<String>

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
        val ret = inflater.inflate(R.layout.fragment_settings, container, false)


        //per impostare le unita di misura per il VOLUME
        var radioGroupVolume: RadioGroup = ret.findViewById(R.id.radioGroupVolume)
        var radioButton0Volume: RadioButton = ret.findViewById(R.id.radioButton0Volume)
        var radioButton1Volume: RadioButton = ret.findViewById(R.id.radioButton1Volume)
        var radioButton2Volume: RadioButton = ret.findViewById(R.id.radioButton2Volume)

        radioGroupVolume.setOnCheckedChangeListener { group, checkedId ->
            val ricettaViewModel = ViewModelProvider(requireActivity()).get(DataModel::class.java)

            val selectedRadioButtonId = radioGroupVolume.checkedRadioButtonId
            if (selectedRadioButtonId.equals(radioButton1Volume.id)) {
                ricettaViewModel.volume = "metrico"
            }else if (selectedRadioButtonId.equals(radioButton2Volume.id)){
                ricettaViewModel.volume = "imperiale"
            }else{
                ricettaViewModel.volume = "default"
            }

            //Log.d("CAMBIO SISTEMA VOLUMEEEEEEE", "${ricettaViewModel.volume}")
        }
        //-------------------------------------------------------------------------------------------


        //per impostare le unita di misura per il PESO
        var radioGroupPeso: RadioGroup = ret.findViewById(R.id.radioGroupPeso)
        var radioButton0Peso: RadioButton = ret.findViewById(R.id.radioButton0Peso)
        var radioButton1Peso: RadioButton = ret.findViewById(R.id.radioButton1Peso)
        var radioButton2Peso: RadioButton = ret.findViewById(R.id.radioButton2Peso)

        radioGroupPeso.setOnCheckedChangeListener { group, checkedId ->
            val ricettaViewModel = ViewModelProvider(requireActivity()).get(DataModel::class.java)

            val selectedRadioButtonId = radioGroupPeso.checkedRadioButtonId
            if (selectedRadioButtonId.equals(radioButton1Peso.id)) {
                ricettaViewModel.peso = "metrico"
            }else if (selectedRadioButtonId.equals(radioButton2Peso.id)){
                ricettaViewModel.peso = "imperiale"
            }else{
                ricettaViewModel.peso = "default"
            }

            //Log.d("CAMBIO SISTEMA PESOOOOOOOOOO", "${ricettaViewModel.peso}")
        }
        //-------------------------------------------------------------------------------------------


        //IMPOSTO QUALE BOTTONE SELEZIONARE IN BASE A DATAMODEL
        val viewModel = ViewModelProvider(requireActivity()).get(DataModel::class.java)
        when (viewModel.peso) {
            "metrico" -> radioButton1Peso.isChecked = true
            "imperiale" -> radioButton2Peso.isChecked = true
            else -> {
                radioButton0Peso.isChecked = true
            }
        }

        when (viewModel.volume) {
            "metrico" -> radioButton1Volume.isChecked = true
            "imperiale" -> radioButton2Volume.isChecked = true
            else -> {
                radioButton0Volume.isChecked = true
            }
        }
        //-------------------------------------------------------------------------------------------


        //per inserire nella ListView gli elementi della categoria selezionata
        var radioGroupElimina: RadioGroup = ret.findViewById(R.id.radioGroupElimina)
        var radioButton0Elimina: RadioButton = ret.findViewById(R.id.radioButton0Elimina)
        var radioButton1Elimina: RadioButton = ret.findViewById(R.id.radioButton1Elimina)
        var radioButton2Elimina: RadioButton = ret.findViewById(R.id.radioButton2Elimina)
        var radioButton3Elimina: RadioButton = ret.findViewById(R.id.radioButton3Elimina)

        radioGroupElimina.setOnCheckedChangeListener { group, checkedId ->

            val selectedRadioButtonId = radioGroupPeso.checkedRadioButtonId
            if (selectedRadioButtonId.equals(radioButton0Elimina.id)) {
                //TODO caricare dal DB la lista di PORTATE
            }else if (selectedRadioButtonId.equals(radioButton1Elimina.id)){
                //TODO caricare dal DB la lista di TIPOLOGIE
            }else if(selectedRadioButtonId.equals(radioButton2Elimina.id)){
                //TODO caricare dal DB la lista di ETNIE
            }else{
                //TODO caricare dal DB la lista di DIETE
            }

            //Log.d("CAMBIO SISTEMA PESOOOOOOOOOO", "${ricettaViewModel.peso}")
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
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



}