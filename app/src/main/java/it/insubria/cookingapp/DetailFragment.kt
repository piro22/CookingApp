package it.insubria.cookingapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import java.io.InputStream

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
    private lateinit var ingredientiNome: MutableList<String>
    private lateinit var ingredientiQuantita: MutableList<Float>
    private lateinit var ingredientiUnita: MutableList<String>
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

        ingredientiNome = mutableListOf()
        ingredientiQuantita = mutableListOf()
        ingredientiUnita = mutableListOf()

        val favoriteIcon: ImageView = ret.findViewById(R.id.favoriteIcon)
        val btnModifica: Button = ret.findViewById(R.id.buttonModifica)
        val textTitolo: TextView = ret.findViewById(R.id.titoloRicetta)
        val textDiff: TextView = ret.findViewById(R.id.textDiff)
        val textPortata: TextView = ret.findViewById(R.id.textPortata)
        val textTipologia: TextView = ret.findViewById(R.id.textTipologia)
        val textDieta: TextView = ret.findViewById(R.id.textDieta)
        val txtTempo: TextView = ret.findViewById(R.id.txtTempo)
        val editPorzioni: EditText = ret.findViewById(R.id.editPorzioni)
        val textPreparazione: TextView = ret.findViewById(R.id.textPreparazione)
        val imgRicetta: ImageView = ret.findViewById(R.id.imgRicetta)


        var txtIngredienti = ret.findViewById<TextView>(R.id.listaIngredienti)

        val dataModel = ViewModelProvider(requireActivity()).get(DataModel::class.java)
        val dbH = dataModel.dbHelper
        //prendo tutti gli ingredienti associati alla ricetta
        val dbr = dbH!!.readableDatabase


        if (ricetta != null) {
            val idRecipe = ricetta!!.id

            //PER TITOLO CHE SCORRE
            textTitolo.text = ricetta!!.nome
            textTitolo.isSelected = true

            //INSERISCO TUTTI I VALORI nelle view
            val t1 = "Difficoltà:\n${ricetta!!.difficolta}"
            textDiff.text = soloInizioGrassetto("Difficoltà:", t1)
            //      textDiff.text = "Difficoltà:\n ${ricetta.difficolta}"

            val t2 = "Portata:\n ${ricetta!!.portata}"
            textPortata.text = soloInizioGrassetto("Poratata:", t2)
            //textPortata.text = "Portata:\n ${ricetta.portata}"

            val t3 = "Tipologia:\n ${ricetta!!.tipologia}"
            textTipologia.text = soloInizioGrassetto("Tipologia:", t3)
            //textTipologia.text = "Tipologia:\n ${ricetta.tipologia}"

            val t4 = "Dieta:\n ${ricetta!!.dieta}"
            textDieta.text = soloInizioGrassetto("Dieta:", t4)
            //textDieta.text = "Dieta:\n ${ricetta.dieta}"

            txtTempo.text = "Tempo preparazione: ${ricetta!!.tempo} min"

            editPorzioni.setText(ricetta!!.porzioni.toString())
            porzioniTemp = ricetta!!.porzioni


            textPreparazione.text = parsePreparazione(ricetta!!.preparazione)

            val foto = ricetta!!.pathFoto
            if (foto.size != 0) {
                val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.size)
                imgRicetta.setImageBitmap(bitmap)

            }else{
                imgRicetta.setImageResource(R.drawable.logo)
            }

            //sarebbe il campo preferito all'interno del db
            val preferito = ricetta!!.preferito

            if (preferito == 0) {
                favoriteIcon.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                favoriteIcon.setImageResource(R.drawable.grade_24dp_fill1_wght400_grad0_opsz24)
            }


            leggiIngredienti(dbr, idRecipe, ingredientiNome, ingredientiQuantita, ingredientiUnita)
            //ora compongo il testo per gli ingredienti e lo assegno alla TextView
            txtIngredienti.text = componiTestoIngredienti()

        }
//-----------------------------------------------------------------------------------------------------


        //quello che mi restituisce
        lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // Gestione del risultato
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val idRicetta = data?.getIntExtra("id_ricetta", -1)
                if (idRicetta != null && idRicetta != -1) {

                    // Query per recuperare gli ingredienti usando idRicetta

                    val cur: Cursor = dbr.rawQuery(
                        "SELECT * FROM ricetta WHERE id = ?",
                        arrayOf(idRicetta.toString())
                    )

                    //val ingredientsList = mutableListOf<String>()

                    if (cur.moveToFirst()) {
                        val nome = cur.getString(cur.getColumnIndexOrThrow("nome"))
                        val porzioni = cur.getInt(cur.getColumnIndexOrThrow("porzioni"))
                        val difficolta = "Difficoltà:\n" + cur.getString(cur.getColumnIndexOrThrow("difficolta"))
                        val tipologia = "Tipologia:\n" + cur.getString(cur.getColumnIndexOrThrow("tipologia"))
                        val portata = "Portata:\n" + cur.getString(cur.getColumnIndexOrThrow("portata"))
                        val dieta = "Dieta:\n" + cur.getString(cur.getColumnIndexOrThrow("dieta"))
                        val tempo = cur.getInt(cur.getColumnIndexOrThrow("tempo_di_preparazione"))
                        val preparazione = cur.getString(cur.getColumnIndexOrThrow("preparazione"))
                        val foto = cur.getBlob(cur.getColumnIndexOrThrow("pathFoto"))


                        textTitolo.text = nome
                        textDiff.text = soloInizioGrassetto("Difficoltà:", difficolta)
                        textPortata.text = soloInizioGrassetto("Portata:", portata)
                        textTipologia.text = soloInizioGrassetto("Tipologia:", tipologia)
                        textDieta.text = soloInizioGrassetto("Dieta:", dieta)
                        txtTempo.text = "Tempo preparazione: $tempo min"
                        editPorzioni.setText(porzioni.toString())
                        textPreparazione.text = parsePreparazione(preparazione)

                        if (foto.size != 0) {
                            val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.size)
                            imgRicetta.setImageBitmap(bitmap)

                        }else{
                            imgRicetta.setImageResource(R.drawable.logo)
                        }

                        val idRecipe = ricetta!!.id

                        ingredientiNome.clear()
                        ingredientiQuantita.clear()
                        ingredientiUnita.clear()
                        leggiIngredienti(dbr, idRecipe, ingredientiNome, ingredientiQuantita, ingredientiUnita)
                        txtIngredienti.text = componiTestoIngredienti()

                        cur.close()

                    }
            }


        }}


        btnModifica.setOnClickListener {
            val intent = Intent(requireContext(), newRecipeActivity::class.java)
            intent.putExtra("id_ricetta", ricetta!!.id)
            activityResultLauncher.launch(intent)
        }


//------------------------------------------------------------------------------------------------
        //
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
        if (!ingredientiQuantita.isEmpty() || !ingredientiNome.isEmpty() || !ingredientiUnita.isEmpty()) {
            if (!ricettaViewModel.peso.equals("default")) {
                cambiaPeso()
            }
            if (!ricettaViewModel.peso.equals("default")) {
                cambiaVolume()
            }
            txtIngredienti.text = componiTestoIngredienti()
        }
        //-----------------------------------------------------------------------------------------------


        //METODO PER CAMBIARE LE PROPORZIONI
        val buttonPorzioni: Button = ret.findViewById(R.id.buttonPorzioni)

        buttonPorzioni.setOnClickListener {
            val porz = editPorzioni.text.toString()
            //controllo che ci sia scritto qualcosa e che ricetta esista
            if (!porz.isEmpty()) {

                //Log.d("CAMBIO PORZIONI", "---------Da ${porzioniTemp} a $porz---------\n")

                var newPorz = porz.toInt()

                //controllo che quello inserito e quello che ho in ricetta siano diversi
                if (newPorz != porzioniTemp) {
                    //qui faccio le proporzioni e cambio tutte le quantità

                    for (i in 0 until ingredientiQuantita.size) {



                        if (!ingredientiUnita[i].equals("qb")) {
                            ingredientiQuantita[i] =
                                (ingredientiQuantita[i] / porzioniTemp) * newPorz
                        }



                    }
                    porzioniTemp = newPorz
                    txtIngredienti.text = componiTestoIngredienti()
                }
            }
        }


        return ret
    }

    private fun parsePreparazione(preparazione: String): String {
        var ret = ""

        val arraySplit = preparazione.split("[[Passo]]")

        for (i in 1 until arraySplit.size) {
            if (i != arraySplit.size - 1)
                ret = ret + "Passo ${i - 1}: \n" + arraySplit[i] + "\n\n"
            else
                ret = ret + "Passo ${i - 1}: \n" + arraySplit[i]
        }
        return ret
    }

    private fun componiTestoIngredienti(): String {
        var ret = ""
        for (i in 0 until ingredientiNome.size) {
            ret = ret + ingredientiNome[i] + " " + ingredientiQuantita[i] + " " +
                    ingredientiUnita[i] + "\n"
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

    private  fun leggiIngredienti(db: SQLiteDatabase, idRicetta : Int, ingredientiNome: MutableList<String>, ingredientiQuantita:MutableList<Float>,  ingredientiUnita:MutableList<String> ) {
        val cursor = db.rawQuery(
            "SELECT * FROM ingredienti_ricetta WHERE id_ricetta = ?",
            arrayOf(idRicetta.toString())
        )
        //PRENDENDOLI DAL DB
        if (cursor.moveToFirst()) {
            do {

                val ingrediente =
                    cursor.getString(cursor.getColumnIndexOrThrow("ingrediente"))
                val quantita = cursor.getInt(cursor.getColumnIndexOrThrow("quantita"))
                val unitaDiMisura =
                    cursor.getString(cursor.getColumnIndexOrThrow("unita_di_misura"))

                ingredientiNome.add(ingrediente)
                ingredientiQuantita.add(quantita.toFloat())
                ingredientiUnita.add(unitaDiMisura)

            } while (cursor.moveToNext())
        }
        cursor.close()


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
        var metrico: List<String> = listOf("g", "h", "Kg")

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
                        "L" -> volMetrToImp(tmp * 1000.0f, i)
                        "dL" -> volMetrToImp(tmp * 100.0f, i)
                        "cL" -> volMetrToImp(tmp * 10.0f, i)
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
                        "gal" -> volImpToMetr(tmp * 760.0f, i)
                        "qt" -> volImpToMetr(tmp * 190.0f, i)
                        "pt" -> volImpToMetr(tmp * 94.0f, i)
                        "cup" -> volImpToMetr(tmp * 48.0f, i)
                        "tbsp" -> volImpToMetr(tmp * 3.0f, i)
                        "tsp" -> volImpToMetr(tmp, i)
                    }

                    print("--- Trasformato ${ingredientiQuantita[i]} ${ingredientiUnita[i]} \n")
                }
            }
        }

    }

    private fun volMetrToImp(millilitri: Float, i: Int) {
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