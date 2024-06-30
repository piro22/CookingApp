package it.insubria.cookingapp

import AutoComplete_adapter
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidx.core.net.toUri
import android.database.sqlite.SQLiteDatabase
import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.net.URI
import java.util.Collections

class newRecipeActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var btnImmagine: ImageView
    private lateinit var imageViewFoto: ImageView
    private var uriFoto: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_newrecipe)


        // Find the main view by ID
        val mainView = findViewById<View>(R.id.main2)

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //inizializzazione variabili
        val editTitolo: EditText = findViewById(R.id.editTextText)
        val porzioni: EditText = findViewById(R.id.editPorzioni)
        val tempo: EditText = findViewById(R.id.editTempo)
        btnImmagine = findViewById(R.id.btnfoto)
        //a che serve que?
        imageViewFoto = findViewById(R.id.imageViewFoto)


        //DATABASE
//----------------------------------------------------------------------------------------------------------------------------

        //setto variabili  per database
        val dbHelper = Database_SQL(this)
        val dbr = dbHelper.readableDatabase
        val dbw = dbHelper.writableDatabase
//----------------------------------------------------------------------------------------------------------------------------


        //----------------PER POPOLARE LE TENDINE
        fun populateAutoCompleteTextView(
            autoCompleteTextView: AutoCompleteTextView,
            tableName: String,
            columnName: String,
            context: Context,
            database: SQLiteDatabase
        ): MutableList<String> {
            val cursor = database.rawQuery("SELECT $columnName FROM $tableName", null)

            val dataList = mutableListOf<String>()

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val columnIndex = cursor.getColumnIndex(columnName)
                    if (columnIndex >= 0) {
                        val value = cursor.getString(columnIndex)
                        dataList.add(value)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }

            val adapter = AutoComplete_adapter(
                context,
                R.layout.autocomplete_adapter_row,
                dataList,
                database,
                tableName,
                columnName
            )
            autoCompleteTextView.setAdapter(adapter)


            return dataList
        }


        //--------------------PER AGGIUNGERE NUOVO ELEMENTO ALLA TENDINA

        fun updateAutoCompleteTextView(
            context: Context,
            autoCompleteTextView: AutoCompleteTextView,
            dataList: MutableList<String>,
            tableName: String,
            columnName: String,
            database: SQLiteDatabase
        ) {
            val adapter = AutoComplete_adapter(
                context,
                R.layout.autocomplete_adapter_row,
                dataList,
                database,
                tableName,
                columnName
            )
            autoCompleteTextView.setAdapter(adapter)
        }

        fun addNewEntry(
            context: Context,
            editText: EditText,
            autoCompleteTextView: AutoCompleteTextView,
            dataList: MutableList<String>,
            tableName: String,
            columnName: String,
            db: SQLiteDatabase
        ) {
            val newEntry = editText.text.toString()

            if (newEntry.isEmpty()) {
                Toast.makeText(context, "Inserisci un valore valido", Toast.LENGTH_SHORT).show()
                return
            }


            if (!dataList.contains(newEntry)) {
                val newValue = ContentValues().apply {
                    put(columnName, newEntry)
                }
                db.insert(tableName, null, newValue)
                dataList.add(newEntry)
                updateAutoCompleteTextView(
                    context,
                    autoCompleteTextView,
                    dataList,
                    tableName,
                    columnName,
                    db
                )
            } else {
                Toast.makeText(context, "Elemento già presente", Toast.LENGTH_SHORT).show()
            }

            editText.text.clear()
        }


        // Gestione pop_up per portate + CONNESSIONE CON DB
//----------------------------------------------------------------------------------------------------------------------------
        //creo il dialog (piccola finestra)
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_portate)

        //creo una lista dove aggiungo ogni volta una portata
        val arrayListaPortate: MutableList<String>
        val listaPortate: AutoCompleteTextView = findViewById(R.id.tendina)

        arrayListaPortate =
            populateAutoCompleteTextView(listaPortate, "portate", "portata", this, dbr)


        val btn_annulla: ImageView = dialog.findViewById(R.id.annullaBtn)
        btn_annulla.setOnClickListener {
            dialog.dismiss()
            Log.d("MainActivity2", "Dialogo chiuso")
        }

        val btn_salva: Button = dialog.findViewById(R.id.saveBtn)

        btn_salva.setOnClickListener {
            addNewEntry(
                context = this,
                editText = dialog.findViewById(R.id.aggiungi),
                autoCompleteTextView = listaPortate,
                dataList = arrayListaPortate,
                tableName = "portate",
                columnName = "portata",
                db = dbw
            )
            dialog.dismiss()
        }

        val btn_portate: Button = findViewById(R.id.btnAddPortate)


        btn_portate.setOnClickListener {
            dialog.window!!
            dialog.setCancelable(false)
            dialog.show()
        }

//----------------------------------------------------------------------------------------------------------------------------

        //POP UP DIETA + CONNESSIONE CON DATABASE
//----------------------------------------------------------------------------------------------------------------------------
        val dialogD = Dialog(this)
        dialogD.setContentView(R.layout.dialog_dieta)

//creo una lista dove aggiungo ogni volta una portata
        val arrayListaDieta: MutableList<String>

        val listaDieta: AutoCompleteTextView = findViewById(R.id.tendinaDieta)
        arrayListaDieta = populateAutoCompleteTextView(listaDieta, "dieta", "dieta", this, dbr)

        val btn_salvaD: Button = dialogD.findViewById(R.id.saveBtnD)
        btn_salvaD.setOnClickListener {
            addNewEntry(
                context = this,
                editText = dialogD.findViewById(R.id.aggiungiD),
                autoCompleteTextView = listaDieta,
                dataList = arrayListaDieta,
                tableName = "dieta",
                columnName = "dieta",
                db = dbw
            )
            dialogD.dismiss()
        }


        val btn_annullaD: ImageView = dialogD.findViewById(R.id.annullaBtnD)
        btn_annullaD.setOnClickListener {
            dialogD.dismiss()
            Log.d("MainActivity2", "Dialogo chiuso")
        }


//MANCA IL TASTO PER ELIMINARE LA DIETA DALLA TENDINA
        val btn_dieta: Button = findViewById(R.id.btnAddDieta)

        btn_dieta.setOnClickListener {
            dialogD.window!!
            dialogD.setCancelable(false)
            dialogD.show()
        }
//----------------------------------------------------------------------------------------------------------------------------


        //POP UP ETNIA + CONNESSIONE CON DATABASE
//----------------------------------------------------------------------------------------------------------------------------
        //creo il dialog (piccola finestra)
        val dialogE = Dialog(this)
        dialogE.setContentView(R.layout.dialog_etnia)


        //creo una lista dove aggiungo ogni volta una portata
        val arrayListaEtnia: MutableList<String>


// All'avvio dell'attività
        val listaEtnia: AutoCompleteTextView = findViewById(R.id.tendinaEtnia)
        arrayListaEtnia =
            populateAutoCompleteTextView(listaEtnia, "etnicita", "etnicita", this, dbr)


// Pulsante di salvataggio
        val btn_salvaE: Button = dialogE.findViewById(R.id.saveBtnE)

        btn_salvaE.setOnClickListener {
            addNewEntry(
                context = this,
                editText = dialogE.findViewById(R.id.aggiungiE),
                autoCompleteTextView = listaEtnia,
                dataList = arrayListaEtnia,
                tableName = "etnicita",
                columnName = "etnicita",
                db = dbw
            )
            dialogE.dismiss()
        }


// Pulsante di annullamento

        val btn_annullaE: ImageView = dialogE.findViewById(R.id.annullaBtnE)

        btn_annullaE.setOnClickListener {
            dialogE.dismiss()
            Log.d("MainActivity2", "Dialogo chiuso")
        }


        //MANCA IL TASTO PER ELIMINARE LA PORTATA DALLA TENDINA
        val btn_Etnia: Button = findViewById(R.id.btnAddEtnia)

        btn_Etnia.setOnClickListener {
            dialogE.window!!
            dialogE.setCancelable(false)
            dialogE.show()
        }


//----------------------------------------------------------------------------------------------------------------------------


        //POP UP TIPO + CONNESSIONE CON DATABASE
//----------------------------------------------------------------------------------------------------------------------------
        //creo il dialog (piccola finestra)
        val dialogT = Dialog(this)
        dialogT.setContentView(R.layout.dialog_tipo)

        //creo una lista dove aggiungo ogni volta una portata
        val arrayListaTipo: MutableList<String>


        val listaTipo: AutoCompleteTextView = findViewById(R.id.tendinaTipo)
        arrayListaTipo =
            populateAutoCompleteTextView(listaTipo, "tipologia", "tipologia", this, dbr)


        val btn_annullaT: ImageView = dialogT.findViewById(R.id.annullaBtnT)
        btn_annullaT.setOnClickListener {
            dialogT.dismiss()
            Log.d("MainActivity2", "Dialogo chiuso")
        }


        val btn_salvaT: Button = dialogT.findViewById(R.id.saveBtnT) // Correct reference to dialogD
        btn_salvaT.setOnClickListener {
            addNewEntry(
                context = this,
                editText = dialogT.findViewById(R.id.aggiungiT),
                autoCompleteTextView = listaTipo,
                dataList = arrayListaTipo,
                tableName = "tipologia",
                columnName = "tipologia",
                db = dbw
            )
            dialogT.dismiss()
        }


        val btn_Tipologia: Button = findViewById(R.id.btnAddTipo)

        btn_Tipologia.setOnClickListener {
            dialogT.window!!
            dialogT.setCancelable(false)
            dialogT.show()
        }


//----------------------------------------------------------------------------------------------------------------------------


        //DIFFICOLTA + CONNESSIONE DB
//----------------------------------------------------------------------------------------------------------------------------

        val tendinaDifficolta: AutoCompleteTextView = findViewById(R.id.tendinaDifficolta)
        fun populateDifficoltaList(difficoltaView: AutoCompleteTextView) {

            val arrayListaDifficolta: MutableList<String> = mutableListOf()
            val cursor = dbr.rawQuery("SELECT difficolta FROM difficolta", null)

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val difficoltaIndex = cursor.getColumnIndex("difficolta")
                    if (difficoltaIndex >= 0) {
                        val p = cursor.getString(difficoltaIndex)
                        arrayListaDifficolta.add(p)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }

            // Creo un adapter per passare i valori dell'array delle difficoltà all'interno della tendina
            val adapterDiff = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                arrayListaDifficolta
            )

            // Imposto l'adapter per l'AutoCompleteTextView
            difficoltaView.setAdapter(adapterDiff)
        }


        // Popolo la lista di difficoltà all'avvio dell'attività
        populateDifficoltaList(tendinaDifficolta)


//----------------------------------------------------------------------------------------------------------------------------


        //AGGIUNGERE PROCEDURE
//----------------------------------------------------------------------------------------------------------------------------
        //lista da passare alla classe RecyclerView_listaProcedimento
        val listaProcedimenti: ArrayList<String> = ArrayList()
        //UI
        val recyclerViewProcedimento = findViewById<RecyclerView>(R.id.recyclerViewProcedure)
        //quello della classe
        val adapterProcedimento = RecyclerView_ListaProcedimento(listaProcedimenti)

        //gli passo adapter fatto da me, quindi un'istanza della classe
        recyclerViewProcedimento.adapter = adapterProcedimento
        //questo per come disporre il layout
        recyclerViewProcedimento.layoutManager = LinearLayoutManager(this)


        val aggiungiProcedimento: ImageView = findViewById(R.id.aggiungiProcedura)
        val textProcedimento: EditText = findViewById(R.id.TextProcedimento)
        aggiungiProcedimento.setOnClickListener {

            // Cambia la tinta e il bkg del drawable
            aggiungiProcedimento.setBackgroundResource(R.drawable.custom_bkg_button_full)
            aggiungiProcedimento.setColorFilter(
                ContextCompat.getColor(
                    getApplicationContext(),
                    R.color.white
                ), PorterDuff.Mode.SRC_IN
            )

            val temp = textProcedimento.text.toString()
            if (temp.isNotEmpty()) {
                textProcedimento.text.clear()

                listaProcedimenti.add(temp)
                // Chiamata al metodo per aggiornare la vista
                adapterProcedimento.notifyDataSetChanged()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                // Ripristina il background a custom_bkg_button
                aggiungiProcedimento.setBackgroundResource(R.drawable.custom_bkg_button)
                // Ripristina la tinta del drawable a coquelicot
                aggiungiProcedimento.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.coquelicot
                    ), PorterDuff.Mode.SRC_IN
                )
            }, 500) // Ritardo di 500 millisecondi (0.5 secondi)
        }


        //drag and drop
        //SimpleCallBack è una classe contenuta all'interno di ItemTouchHelper
        //object è un'istanza anonima della classe ItemTouchHelper.SimpleCallBack, come se fosse Rettangolo r = new Rettangolo

        val simpleCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosizione: Int = viewHolder.adapterPosition
                val toPosizione: Int = target.adapterPosition


                Collections.swap(listaProcedimenti, fromPosizione, toPosizione)
                recyclerView.adapter?.notifyItemMoved(fromPosizione, toPosizione)

                return true

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direzione: Int) {

            }


        }

        val item = ItemTouchHelper(simpleCallBack)
        item.attachToRecyclerView(recyclerViewProcedimento)


//------------------------------------------------------------------------------------------------------------------------------


        //aggiungere gli ingredienti
//----------------------------------------------------------------------------------------------------------------------------
        val input = findViewById<EditText>(R.id.input)
        val ArrayListaIngredienti = mutableListOf<String>()
        val adapt = ListView_adapter(this, ArrayListaIngredienti)
        val listViewIngredients = findViewById<ListView>(R.id.listviewl)

        val bottoneIngrediente: ImageView = findViewById(R.id.aggiungiIngrediente)


        listViewIngredients.adapter = adapt



        bottoneIngrediente.setOnClickListener {

            // Cambia la tinta e il bkg del drawable
            bottoneIngrediente.setBackgroundResource(R.drawable.custom_bkg_button_full)
            bottoneIngrediente.setColorFilter(
                ContextCompat.getColor(
                    getApplicationContext(),
                    R.color.white
                ), PorterDuff.Mode.SRC_IN
            )

            // Get text from EditText
            val ingredient = input.text.toString()

            // Check if input is not empty
            if (ingredient.isNotEmpty()) {
                // Add ingredient to the list
                ArrayListaIngredienti.add(ingredient)

                // Notify the adapter of data change
                adapt.notifyDataSetChanged()

                setListViewHeightBasedOnItems(listViewIngredients, adapt)

                // Clear the input field
                input.text.clear()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                // Ripristina il background a custom_bkg_button
                bottoneIngrediente.setBackgroundResource(R.drawable.custom_bkg_button)
                // Ripristina la tinta del drawable a coquelicot
                bottoneIngrediente.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.coquelicot
                    ), PorterDuff.Mode.SRC_IN
                )
            }, 500) // Ritardo di 500 millisecondi (0.5 secondi)
        }


        //PER QUANDO VIENE CLICCATO IL CAMPO MODIFICA SU DETAIL FRAGMENT
//----------------------------------------------------------------------------------------------------------------------------
        val intent = intent
        val idRicetta = intent.getIntExtra("id_ricetta", -1)

        //valore che serve per capire quae azione far svolgere al bottone salvaTutto
        //var provieneDaIntent : Boolean = false DA ELIMINARE

        if (idRicetta != -1) {


// Popola i campi con i dati ricevuti
            val cursor: Cursor =
                dbr.rawQuery("SELECT * FROM ricetta WHERE id = ?", arrayOf(idRicetta.toString()))

            //PERCHEORTHROW SENZA DA ERRORI
            if (cursor.moveToFirst()) {
                val nomeRicetta = cursor.getString(cursor.getColumnIndexOrThrow("nome"))
                val porzioniRicetta = cursor.getInt(cursor.getColumnIndexOrThrow("porzioni"))
                val difficolta = cursor.getString(cursor.getColumnIndexOrThrow("difficolta"))
                val tipologia = cursor.getString(cursor.getColumnIndexOrThrow("tipologia"))
                val portata = cursor.getString(cursor.getColumnIndexOrThrow("portata"))
                val dieta = cursor.getString(cursor.getColumnIndexOrThrow("dieta"))
                val etnicita = cursor.getString(cursor.getColumnIndexOrThrow("etnicita"))
                val tempoPrep = cursor.getInt(cursor.getColumnIndexOrThrow("tempo_di_preparazione"))
                val preparazione = cursor.getString(cursor.getColumnIndexOrThrow("preparazione"))
                val pathFoto = cursor.getString(cursor.getColumnIndexOrThrow("pathFoto"))

                listaTipo.setText(tipologia)
                listaPortate.setText(portata)
                listaDieta.setText(dieta)
                listaEtnia.setText(etnicita)
                tendinaDifficolta.setText(difficolta)
                tempo.setText(tempoPrep.toString())
                editTitolo.setText(nomeRicetta)
                porzioni.setText(porzioniRicetta.toString())



                if (pathFoto != "default") {
                    btnImmagine.setImageURI(pathFoto.toUri())
                }

                /*
                            //per passare i dati in maniera corretta all'adapter una volta recuperati dal db
                            val segments = preparazione.split("[[passo]]")

                            val listaProcedure = mutableListOf<String>()

                            for (segment in segments) {
                                listaProcedure.add(segment.trim())
                            }

                            recyclerViewProcedimento.adapter = adapterProcedimento*/

            }
            cursor.close()

            //popolo gli ingredienti
            /*val cur: Cursor = dbr.rawQuery("SELECT * FROM ingredienti_ricetta WHERE id_ricetta = ?", arrayOf(idRicetta.toString()))

            val ingredientsList = mutableListOf<String>()

            if (cur.moveToFirst()) {
                do {
                    val ingrediente = cur.getString(cur.getColumnIndexOrThrow("ingrediente"))
                    val quantita = cur.getInt(cur.getColumnIndexOrThrow("quantita"))
                    val unitaDiMisura = cur.getString(cur.getColumnIndexOrThrow("unita_di_misura"))

                    // Create a string representation of the ingredient
                    val ingredientString = "$quantita $unitaDiMisura di $ingrediente"
                    ingredientsList.add(ingredientString)

                    // Logging all'interno del ciclo
                    Log.d("Ingredienti", ingredientString)

                } while (cur.moveToNext())

                // Creazione e impostazione dell'adapter dopo il ciclo
                val adapter = ListView_adapter(this, ingredientsList)
                val listViewIngredients = findViewById<ListView>(R.id.listviewl)
                listViewIngredients.adapter = adapter
            }

            cur.close()*/
        }

        //----------------------------------------------------------------------------------------------------------------------------


        //SCEGLIERE IMMAGINE-------------------------------------------------------------------------------------------------
        val pathImg = "default"
        btnImmagine.setOnClickListener {
            chooseImageFromGallery()
        }


        //-------------------------------------------------------------------------------------------------------------------


        fun salvataggio(): RicetteModel? {

            val dialogError = Dialog(this)
            dialogError.setContentView(R.layout.dialog_errore)
            var messaggioErroreModificabile = "Errore"

            val btnok: Button = dialogError.findViewById(R.id.buttonOK)
            val txtErrore: TextView = dialogError.findViewById(R.id.messaggioErrore)
            btnok.setOnClickListener {
                dialogError.dismiss()
            }

            var ricettaDaSalvare: RicetteModel? = null
            //se questa variabile diventa false allora si blocca il procedimento
            var esito = true


            //CONTROLLO SUL TITOLO

            val titoloFinale = editTitolo.text.toString()
            if (titoloFinale.trim().isEmpty()) {
                esito = false

                messaggioErroreModificabile = "Il nome non può essere vuoto"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULLA PORTATA
            val txtPortata = listaPortate.text.toString()
            if (txtPortata.isEmpty()) {
                esito = false

                messaggioErroreModificabile = "Scegliere una portata"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULLA TIPOLOGIA

            val txtTipologia = listaTipo.text.toString()
            if (txtTipologia.isEmpty()) {
                esito = false

                messaggioErroreModificabile = "Scegliere una tipologia"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULLA DIETA
            val txtDieta = listaDieta.text.toString()
            if (txtDieta.isEmpty()) {
                esito = false

                messaggioErroreModificabile = "Scegliere una dieta"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULL'ETNIA

            val txtEtnia = listaEtnia.text.toString()
            if (txtEtnia.isEmpty()) {
                esito = false

                messaggioErroreModificabile = "Scegliere un'etnia"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULLA DIFFICOLTA

            val txtDifficolta = tendinaDifficolta.text.toString()
            if (txtDifficolta.isEmpty()) {
                esito = false

                messaggioErroreModificabile = "Scegliere una difficoltà"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SUL NUMERO DI PORZIONI
            val txtPorzioni = porzioni.text.toString()
            var numPorzioni = 0
            if (txtPorzioni.isEmpty()) {
                esito = false

                messaggioErroreModificabile =
                    "Bisogna inserire un numero di porzioni per questa ricetta"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            } else {
                numPorzioni = txtPorzioni.toInt()
            }

            //CONTROLLO SUL TEMPO
            val txtTempo = tempo.text.toString()
            var numTempo = 0
            if (txtTempo.isEmpty()) {
                esito = false

                messaggioErroreModificabile =
                    "Bisogna inserire un tempo di preparazione totale per questa ricetta"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            } else {
                numTempo = txtTempo.toInt()
            }


            //CONTROLLO SUGLI INGREDIENTI
            if (ArrayListaIngredienti.isEmpty()) {
                esito = false

                messaggioErroreModificabile = "Inserire minimo un ingrediente per questa ricetta"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULLA PREPARAZIONE
            var preparazione = ""
            if (listaProcedimenti.isEmpty()) {
                esito = false

                messaggioErroreModificabile = "Inserire una procedura per questa ricetta"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            } else {
                preparazione = componiProcedura(listaProcedimenti)
            }

            if (esito) {
                ricettaDaSalvare = RicetteModel(
                    -1,
                    titoloFinale,
                    uriFoto.toString(),
                    preparazione,
                    numPorzioni,
                    numTempo,
                    txtDifficolta,
                    txtTipologia,
                    txtPortata,
                    txtDieta,
                    txtEtnia,
                    0
                )


            }
            return ricettaDaSalvare
        }


        //-----------------------------------------------------------------------------------------------------

        val btnSalva: Button = findViewById(R.id.btnSalvaTutto)
        btnSalva.setOnClickListener {

            val ricetta = salvataggio()

            if (idRicetta != -1) {


                Log.d("22222222222222222222222333333333333333333", "modifica")
                Log.d("22222222222222222222222244444444444444", "${ricetta?.nome}")
                //controllo che il return di salvataggio() sia != da null
                if (ricetta != null) {
                    // per AGGIORNARE sul database
                    val updateQuery = """
                    UPDATE ricetta 
                    SET nome = '${ricetta?.nome}', 
                    porzioni = '${ricetta?.porzioni}', 
                    tempo_di_preparazione ='${ricetta?.tempo}', 
                    difficolta = '${ricetta?.difficolta}', 
                    tipologia = '${ricetta?.tipologia}', 
                    portata = '${ricetta?.portata}', 
                    dieta = '${ricetta?.dieta}', 
                    etnicita = '${ricetta?.etnicita}', 
                    pathFoto = '${ricetta?.pathFoto}',
                    preparazione = '${ricetta?.preparazione}'
                    WHERE id = '${idRicetta}'
                    """
                    dbw.execSQL(updateQuery)

                    // Mostra un messaggio di successo o esegui altre azioni necessarie dopo l'update
                    Toast.makeText(this, "Dati salvati con successo", Toast.LENGTH_SHORT).show()


                    //per passare i valori a DETAILFRAGMENT
                    val resultIntent = Intent()
                    resultIntent.putExtra("id_ricetta", idRicetta)
                    Log.d("666666666666666666666", "$idRicetta")
                    resultIntent.putExtra("nome", ricetta?.nome)
                    resultIntent.putExtra("porzioni", ricetta?.porzioni)
                    resultIntent.putExtra("tempo_di_preparazione", ricetta?.tempo)
                    resultIntent.putExtra("difficolta", ricetta?.difficolta)
                    resultIntent.putExtra("tipologia", ricetta?.tipologia)
                    resultIntent.putExtra("portata", ricetta?.portata)
                    resultIntent.putExtra("dieta", ricetta?.dieta)
                    resultIntent.putExtra("etnicita", ricetta?.etnicita)
                    resultIntent.putExtra("pathFoto", ricetta?.pathFoto)
                    resultIntent.putExtra("preparazione", ricetta?.preparazione)


                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                    Log.d("AGGIORNAMENTO FATTO", "INSERITA RICETTA NUOVAAAAAAAA")
                } else {
                    Log.d("AGGIORNAMENTO FALLITO", "NON INSERITO RICETTA")
                }
            } else {
                //controllo che il return di salvataggio() sia != da null
                if (ricetta != null) {
                    Log.d("384898728738328789238983", "${ricetta?.nome}")
                    dbw.execSQL("INSERT INTO ricetta(nome , porzioni ,tempo_di_preparazione, difficolta, tipologia, portata, dieta, etnicita, pathFoto, preparazione, preferito) VALUES ( '${ricetta?.nome}', '${ricetta?.porzioni}', '${ricetta?.tempo}', '${ricetta?.difficolta}', '${ricetta?.tipologia}', '${ricetta?.portata}', '${ricetta?.dieta}', '${ricetta?.etnicita}', '${ricetta?.pathFoto}', '${ricetta?.preparazione}', 0)")
                    Log.d("INSERIMENTO FATTO", "INSERITA RICETTA NUOVAAAAAAAA")
                } else {
                    Log.d("INSERIMENTO FALLITO", "NON INSERITO RICETTA")
                }
            }
        }
    }


    //TODO fare qualcosa per prendere gli ingredienti e le quantità
    // poi inserirli uno alla volta con anche l'ID della ricetta


    private fun chooseImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            selectedImageUri?.let {
                uriFoto = selectedImageUri.toString()

                val layoutParams = imageViewFoto.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = 500 // o qualsiasi altra dimensione desiderata
                imageViewFoto.layoutParams = layoutParams

                imageViewFoto.setImageURI(selectedImageUri)
            }
        }else{
            uriFoto = "default"
        }
    }

    //funzione per comporre la procedura a partire dai passi inseriti dal'utenteto
    private fun componiProcedura(listaProcedimenti: ArrayList<String>): String {
        var ret = ""

        for (i in listaProcedimenti) {
            ret = ret + "[[Passo]]$i"
            //Log.d("11111111111111111111111111111111111111111111111111", "$ret")
        }

        return ret
    }


    //FUNZIONE CHE CAMBIA LA DIMENSIONE DELLA LIST VIEW IN BASE AL NUMERO DI ELEMENTI
    private fun setListViewHeightBasedOnItems(listView: ListView, adapter: ListView_adapter) {
        if (adapter == null) {
            return
        }

        var totalHeight = 0
        for (i in 0 until adapter.count) {
            val listItem = adapter.getView(i, null, listView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += listItem.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (adapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }


}


