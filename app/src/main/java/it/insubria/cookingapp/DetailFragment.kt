package it.insubria.cookingapp

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var ricetta: RicetteModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    fun setRicetta(model: RicetteModel){
        this.ricetta = model
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val ret = inflater.inflate(R.layout.fragment_detail, container, false)

        //PER TITOLO CHE SCORRE
        val textTitolo: TextView = ret.findViewById(R.id.titoloRicetta)
        textTitolo.text = ricetta.nome
        textTitolo.isSelected = true

        //INSERISCO TUTTI I VALORI nelle view
        val textDiff: TextView = ret.findViewById(R.id.textDiff)
        val t1 = "Difficoltà:\n${ricetta.difficolta}"
        textDiff.text = soloInizioGrassetto("Difficoltà:", t1)
//      textDiff.text = "Difficoltà:\n ${ricetta.difficolta}"

        val textPortata: TextView = ret.findViewById(R.id.textPortata)
        val t2 = "Portata:\n ${ricetta.portata}"
        textPortata.text = soloInizioGrassetto("Poratata:", t2)
        //textPortata.text = "Portata:\n ${ricetta.portata}"

        val textTipologia: TextView = ret.findViewById(R.id.textTipologia)
        val t3 = "Tipologia:\n ${ricetta.tipologia}"
        textTipologia.text = soloInizioGrassetto("Tipologia:", t3)
        //textTipologia.text = "Tipologia:\n ${ricetta.tipologia}"

        val textDieta: TextView = ret.findViewById(R.id.textDieta)
        val t4 = "Dieta:\n ${ricetta.dieta}"
        textDieta.text = soloInizioGrassetto("Dieta:", t4)
        //textDieta.text = "Dieta:\n ${ricetta.dieta}"

        val txtTempo: TextView = ret.findViewById(R.id.txtTempo)
        txtTempo.text = "Tempo preparazione: ${ricetta.tempo} min"

        val editPorzioni: EditText = ret.findViewById(R.id.editPorzioni)
        editPorzioni.setText(ricetta.porzioni.toString())

        val textPreparazione: TextView = ret.findViewById(R.id.textPreparazione)
        textPreparazione.text = ricetta.preparazione

        val imgRicetta: ImageView = ret.findViewById(R.id.imgRicetta)
        if(!ricetta.pathFoto.equals("default")){
            imgRicetta.setImageURI(ricetta.pathFoto.toUri())
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
         * @return A new instance of fragment DetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun soloInizioGrassetto(inizio: String, testo: String): SpannableString {
        // Crea un SpannableString con il testo
        val spannableString = SpannableString(testo)

        // Trova l'indice di fine del testo da mettere in grassetto
        val end = inizio.length

        // Applica il StyleSpan per il grassetto sulla parte desiderata
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Tipo di stile da applicare (grassetto)
            0, // Indice di inizio del grassetto
            end, // Indice di fine del grassetto
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Modalità di applicazione dello span
        )
        return spannableString
    }
}