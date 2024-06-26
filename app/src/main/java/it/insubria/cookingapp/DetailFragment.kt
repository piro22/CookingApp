package it.insubria.cookingapp

import android.content.ContentValues
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider

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

    private lateinit var ricettaViewModel: DataModel
    private var ricetta: RicetteModel? = null
    private var ingredientiNome: MutableList<String> = mutableListOf("pippo", "franco", "topolino")
    private var ingredientiQuantita: MutableList<Float> = mutableListOf(23.0f, 10.5f, 2.8f)
    private var ingredientiUnita: MutableList<String> = mutableListOf("cL", "h", "cup")
    private var porzioniTemp: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

//    fun setRicetta(model: RicetteModel){
//        this.ricetta = model
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val ret = inflater.inflate(R.layout.fragment_detail, container, false)

        ricettaViewModel = ViewModelProvider(requireActivity()).get(DataModel::class.java)


        // Recupera i dati dal ViewModel
        ricetta = ricettaViewModel.ricetta
        val dbHelper = ricettaViewModel.dbHelper
        //perche devo fare ret. e non subiro findIdviewby

        val favoriteIcon: ImageView = ret.findViewById(R.id.favoriteIcon)
        val btnModifica: Button = ret.findViewById(R.id.buttonModifica)





        if (ricetta != null) {

            //PER TITOLO CHE SCORRE
            val textTitolo: TextView = ret.findViewById(R.id.titoloRicetta)
            textTitolo.text = ricetta!!.nome
            textTitolo.isSelected = true

            //INSERISCO TUTTI I VALORI nelle view
            val textDiff: TextView = ret.findViewById(R.id.textDiff)
            val t1 = "Difficoltà:\n${ricetta!!.difficolta}"
            textDiff.text = soloInizioGrassetto("Difficoltà:", t1)
            //      textDiff.text = "Difficoltà:\n ${ricetta.difficolta}"

            val textPortata: TextView = ret.findViewById(R.id.textPortata)
            val t2 = "Portata:\n ${ricetta!!.portata}"
            textPortata.text = soloInizioGrassetto("Poratata:", t2)
            //textPortata.text = "Portata:\n ${ricetta.portata}"

            val textTipologia: TextView = ret.findViewById(R.id.textTipologia)
            val t3 = "Tipologia:\n ${ricetta!!.tipologia}"
            textTipologia.text = soloInizioGrassetto("Tipologia:", t3)
            //textTipologia.text = "Tipologia:\n ${ricetta.tipologia}"

            val textDieta: TextView = ret.findViewById(R.id.textDieta)
            val t4 = "Dieta:\n ${ricetta!!.dieta}"
            textDieta.text = soloInizioGrassetto("Dieta:", t4)
            //textDieta.text = "Dieta:\n ${ricetta.dieta}"

            val txtTempo: TextView = ret.findViewById(R.id.txtTempo)
            txtTempo.text = "Tempo preparazione: ${ricetta!!.tempo} min"

            val editPorzioni: EditText = ret.findViewById(R.id.editPorzioni)
            editPorzioni.setText(ricetta!!.porzioni.toString())
            porzioniTemp = ricetta!!.porzioni

            val textPreparazione: TextView = ret.findViewById(R.id.textPreparazione)
            textPreparazione.text = ricetta!!.preparazione


            val imgRicetta: ImageView = ret.findViewById(R.id.imgRicetta)
            if (!ricetta!!.pathFoto.equals("default")) {
                imgRicetta.setImageURI(ricetta!!.pathFoto.toUri())
            }

            //sarebbe il campo preferito all'interno del db
            var preferito = ricetta!!.preferito

            if (preferito == 0) {
                favoriteIcon.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                favoriteIcon.setImageResource(R.drawable.grade_24dp_fill1_wght400_grad0_opsz24)
            }


        }

        btnModifica.setOnClickListener {
            val intent = Intent(requireContext(), newRecipeActivity::class.java)

            intent.putExtra("id_ricetta", ricetta!!.id)

            startActivity(intent)
        }






        favoriteIcon.setOnClickListener {

            val dbw = dbHelper!!.writableDatabase

            //sarebbe il campo preferito all'interno del db
            //var preferito = ricetta!!.preferito

            if (ricetta!!.preferito == 0) {
                favoriteIcon.setImageResource(R.drawable.grade_24dp_fill1_wght400_grad0_opsz24)
                ricetta!!.preferito = 1
            } else {
                favoriteIcon.setImageResource(R.drawable.baseline_favorite_24)
                ricetta!!.preferito = 0
            }


            val nuovoValore = ContentValues().apply {
                put("preferito", ricetta!!.preferito)
            }


            val rowsAffected =
                dbw.update("ricetta", nuovoValore, "id=?", arrayOf(ricetta!!.id.toString()))
            dbw.close()
        }


        //METODO PER CAMBIARE LE UNITA' DI MISURA IN CIO' CHE HA SELEZIONATO L'UTENTE
        if(!ingredientiQuantita.isEmpty() || !ingredientiNome.isEmpty() || !ingredientiUnita.isEmpty()){
            if(!ricettaViewModel.peso.equals("default")){
                cambiaPeso()
            }
            if(!ricettaViewModel.peso.equals("default")){
                cambiaVolume()
            }
        }
        //-----------------------------------------------------------------------------------------------


        //METODO PER CAMBIARE LE PROPORZIONI
        val buttonPorzioni: Button = ret.findViewById(R.id.buttonPorzioni)
        val editPorzioni: EditText = ret.findViewById(R.id.editPorzioni)

        buttonPorzioni.setOnClickListener {
            val porz = editPorzioni.text.toString()
            //controllo che ci sia scritto qualcosa e che ricetta esista
            if(!porz.isEmpty()){

                Log.d("CAMBIO PORZIONI","---------Da ${porzioniTemp} a $porz---------\n")

                var newPorz = porz.toInt()

                //controllo che quello inserito e quello che ho in ricetta siano diversi
                if(newPorz != porzioniTemp){
                    //qui faccio le proporzioni e cambio tutte le quantità

                    for(i in 0 until ingredientiQuantita.size){

                        Log.d("CAMBIO PORZIONI","cambio ${ingredientiNome[i]} ${ingredientiQuantita[i]} ${ingredientiUnita[i]}\n")

                        if(!ingredientiUnita[i].equals("qb")){
                            ingredientiQuantita[i] = (ingredientiQuantita[i]/porzioniTemp) * newPorz
                        }

                        Log.d("CAMBIO PORZIONI","cambio ${ingredientiNome[i]} ${ingredientiQuantita[i]} ${ingredientiUnita[i]}\n")

                    }
                    porzioniTemp = newPorz

                }
            }
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

    private fun cambiaPeso() {
        var imperiali: List<String> = listOf("lb", "Oz")
        var metrico: List<String> = listOf("g", "h", "Kg", "kg")

        if (ricettaViewModel.peso.equals("imperiale")) {
            for (i in 0 until ingredientiQuantita.size) {
                // se l'unita di misura non è contenuta nella lista delle unita imperiali
                // va modificata
                if (!imperiali.contains(ingredientiUnita[i])) {

                    print("Trasformo ${ingredientiQuantita[i]} ${ingredientiUnita[i]} in imperiale")

                    if (ingredientiUnita[i].equals("g")) {

                        var tmp = ingredientiQuantita[i]

                        //se supera il kilo allora lo converto in lb
                        //altrimenti in Oz
                        if (tmp > 1000.0f) {
                            ingredientiQuantita[i] = tmp / 453.59f
                            ingredientiUnita[i] = "lb"
                        } else {
                            ingredientiQuantita[i] = tmp / 28.35f
                            ingredientiUnita[i] = "Oz"
                        }

                    } else if (ingredientiUnita[i].equals("h")) {

                        var tmp = (ingredientiQuantita[i] * 100)

                        //se supera il kilo allora lo converto in lb
                        //altrimenti in Oz
                        if (tmp > 1000.0f) {
                            ingredientiQuantita[i] = tmp / 453.59f
                            ingredientiUnita[i] = "lb"
                        } else {
                            ingredientiQuantita[i] = tmp / 28.35f
                            ingredientiUnita[i] = "Oz"
                        }

                    } else if (ingredientiUnita[i].equals("Kg")) {

                        var tmp = (ingredientiQuantita[i] * 1000)

                        //se supera il kilo allora lo converto in lb
                        //altrimenti in Oz
                        if (tmp > 1000.0f) {
                            ingredientiQuantita[i] = tmp / 453.59f
                            ingredientiUnita[i] = "lb"
                        } else {
                            ingredientiQuantita[i] = tmp / 28.35f
                            ingredientiUnita[i] = "Oz"
                        }
                    }


                    print("--- Trasformato ${ingredientiQuantita[i]} ${ingredientiUnita[i]} \n")

                }
            }
        } else if (ricettaViewModel.peso.equals("metrico")) {
            for (i in 0 until ingredientiQuantita.size) {
                if (!metrico.contains(ingredientiUnita[i])) {

                    print("Trasformo ${ingredientiQuantita[i]} ${ingredientiUnita[i]} in metrico")

                    if (ingredientiUnita[i].equals("Oz")) {

                        var tmp = ingredientiQuantita[i] * 28.35f


                        if (tmp > 1000.0f) {
                            ingredientiQuantita[i] = tmp / 1000.0f
                            ingredientiUnita[i] = "Kg"
                        } else if (tmp > 100.0f) {
                            ingredientiQuantita[i] = tmp / 100.0f
                            ingredientiUnita[i] = "h"
                        } else {
                            ingredientiQuantita[i] = tmp
                            ingredientiUnita[i] = "g"
                        }


                    } else if (ingredientiUnita[i].equals("lb")) {

                        var tmp = ingredientiQuantita[i] * 453.59f

                        if (tmp > 1000.0f) {
                            ingredientiQuantita[i] = tmp / 1000.0f
                            ingredientiUnita[i] = "Kg"
                        } else if (tmp > 100.0f) {
                            ingredientiQuantita[i] = tmp / 100.0f
                            ingredientiUnita[i] = "h"
                        } else {
                            ingredientiQuantita[i] = tmp
                            ingredientiUnita[i] = "g"
                        }

                    }

                    print("--- Trasformato ${ingredientiQuantita[i]} ${ingredientiUnita[i]} \n")
                }
            }
        }
    }


    private fun cambiaVolume() {
        var imperiali: List<String> = listOf("tsp", "tbsp", "cup", "pt", "qt", "gal")
        var metrico: List<String> = listOf("L", "mL", "cL", "dL")

        if (ricettaViewModel.volume.equals("imperiale")) {
            for (i in 0 until ingredientiQuantita.size) {
                // se l'unita di distro non è contenuta nella lista delle unita imperiali
                // va modificata
                if (!imperiali.contains(ingredientiUnita[i])) {
                    print("Trasformo ${ingredientiQuantita[i]} ${ingredientiUnita[i]} in imperiale")

                    var tmp = ingredientiQuantita[i]

                    when (ingredientiUnita[i]) {
                        "L" -> volMetrToImp(tmp*1000.0f, i)
                        "dL" -> volMetrToImp(tmp*100.0f, i)
                        "cL" -> volMetrToImp(tmp*10.0f, i)
                        "mL" -> volMetrToImp(tmp, i)
                    }

                    print("--- Trasformato ${ingredientiQuantita[i]} ${ingredientiUnita[i]} \n")
                }
            }
        } else if (ricettaViewModel.volume.equals("metrico")) {
            for (i in 0 until ingredientiQuantita.size) {
                // se l'unita di misura non è contenuta nella lista delle unita metriche
                // va modificata
                if (!metrico.contains(ingredientiUnita[i])) {
                    print("Trasformo ${ingredientiQuantita[i]} ${ingredientiUnita[i]} in metrico")

                    val tmp = ingredientiQuantita[i]

                    when (ingredientiUnita[i]) {
                        "gal" -> volImpToMetr(tmp*760.0f, i)
                        "qt" -> volImpToMetr(tmp*190.0f, i)
                        "pt" -> volImpToMetr(tmp*94.0f, i)
                        "cup" -> volImpToMetr(tmp*48.0f, i)
                        "tbsp" -> volImpToMetr(tmp*3.0f, i)
                        "tsp" -> volImpToMetr(tmp, i)
                    }

                    print("--- Trasformato ${ingredientiQuantita[i]} ${ingredientiUnita[i]} \n")
                }
            }
        }

    }

    private fun volMetrToImp(millilitri: Float, i: Int){
        when {
            millilitri >= 3800.0f -> {
                ingredientiQuantita[i] = millilitri / 3800.0f
                ingredientiUnita[i] = "gal"
            }
            millilitri >= 950.0f -> {
                ingredientiQuantita[i] = millilitri / 950.0f
                ingredientiUnita[i] = "qt"
            }
            millilitri >= 470.0f -> {
                ingredientiQuantita[i] = millilitri / 470.0f
                ingredientiUnita[i] = "pt"
            }
            millilitri >= 240.0f -> {
                ingredientiQuantita[i] = millilitri / 240.0f
                ingredientiUnita[i] = "cup"
            }
            millilitri >= 15.0f -> {
                ingredientiQuantita[i] = millilitri / 15.0f
                ingredientiUnita[i] = "tbsp"
            }
            else -> {
                ingredientiQuantita[i] = millilitri / 5.0f
                ingredientiUnita[i] = "tsp"
            }
        }
    }

    private fun volImpToMetr(tsp: Float, i: Int) {
        val ml = tsp * 5.0f

        when {
            ml >= 1500.0f -> {
                ingredientiQuantita[i] = ml / 1000.0f
                ingredientiUnita[i] = "L"
            }
            else -> {
                ingredientiQuantita[i] = ml
                ingredientiUnita[i] = "mL"
            }
        }
    }


}