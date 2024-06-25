package it.insubria.cookingapp

import AutoComplete_adapter

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.app.Activity
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import java.net.URI
import java.util.Collections

class newRecipeActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var btnImmagine: ImageView
    private lateinit var imageViewFoto: ImageView
    private lateinit var uriFoto: Uri

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






        //DATABASE
//----------------------------------------------------------------------------------------------------------------------------

        //setto variabili  per database
        val dbHelper = Database_SQL(this)
        val dbr = dbHelper.readableDatabase
        val dbw = dbHelper.writableDatabase
//----------------------------------------------------------------------------------------------------------------------------


        //----------------PER POPOLARE LE TENDINE
        fun populateAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView, tableName: String, columnName: String, context: Context, database: SQLiteDatabase): MutableList<String> {
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
        )
        {


                val newEntry = editText.text.toString()

                if (!dataList.contains(newEntry)) {
                    val newValue = ContentValues().apply {
                        put(columnName, newEntry)
                    }
                    db.insert(tableName, null, newValue)
                    dataList.add(newEntry)
                    updateAutoCompleteTextView(context, autoCompleteTextView, dataList, tableName, columnName, db)
                } else {
                    Log.d("MainActivity", "Etnia già presente")
                }


        }





        // Gestione pop_up per portate + CONNESSIONE CON DB
//----------------------------------------------------------------------------------------------------------------------------
        //creo il dialog (piccola finestra)
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_portate)

        //creo una lista dove aggiungo ogni volta una portata
        var arrayListaPortate: MutableList<String> = mutableListOf()
        val listaPortate: AutoCompleteTextView = findViewById(R.id.tendina)

        arrayListaPortate= populateAutoCompleteTextView(listaPortate, "portate", "portata", this, dbr)


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


        //MANCA IL TASTO PER ELIMINARE LA PORTATA DALLA TENDINA
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
        var arrayListaDieta: MutableList<String> = mutableListOf()

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
        var arrayListaEtnia: MutableList<String> = mutableListOf()




// All'avvio dell'attività
        val listaEtnia: AutoCompleteTextView = findViewById(R.id.tendinaEtnia)
        arrayListaEtnia = populateAutoCompleteTextView(listaEtnia, "etnicita", "etnicita", this, dbr)



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
        var arrayListaTipo: MutableList<String> = mutableListOf()


        val listaTipo: AutoCompleteTextView = findViewById(R.id.tendinaTipo)
        arrayListaTipo=populateAutoCompleteTextView(listaTipo, "tipologia", "tipologia", this, dbr )


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


        //MANCA IL TASTO PER ELIMINARE LA PORTATA DALLA TENDINA
        val btn_Tipologia: Button = findViewById(R.id.btnAddTipo)

        btn_Tipologia.setOnClickListener {
            dialogT.window!!
            dialogT.setCancelable(false)
            dialogT.show()
        }


//----------------------------------------------------------------------------------------------------------------------------


        //DIFFICOLTA + CONNESSIONE DB
//----------------------------------------------------------------------------------------------------------------------------
        //creo una lista dove aggiungo ogni volta una portata
        var arrayListaDifficolta: MutableList<String> = mutableListOf()


        // Popola arrayListaPortate con i dati esistenti nel database all'avvio
        val listaDiff: AutoCompleteTextView = findViewById(R.id.tendinaDifficolta)

        arrayListaDifficolta=populateAutoCompleteTextView(listaDiff, "difficolta", "difficolta", this, dbr )


        //creo un adapter per passare i valori dell'array delle portate all'interno della tendina
        val adapterDiff = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            arrayListaDifficolta.toTypedArray()
        )
        //per aggiornare la vista(tendina)
        listaDiff.setAdapter(adapterDiff)
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
            aggiungiProcedimento.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_IN)

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
                aggiungiProcedimento.setColorFilter(ContextCompat.getColor(applicationContext, R.color.coquelicot), PorterDuff.Mode.SRC_IN)
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
                var fromPosizione: Int = viewHolder.adapterPosition
                var toPosizione: Int = target.adapterPosition


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
            bottoneIngrediente.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white), PorterDuff.Mode.SRC_IN)

            // Get text from EditText
            val ingredient = input.text.toString()

            // Check if input is not empty
            if (ingredient.isNotEmpty()) {
                // Add ingredient to the list
                ArrayListaIngredienti.add(ingredient)

                // Notify the adapter of data change
                adapt.notifyDataSetChanged()

                setListViewHeightBasedOnItems(listViewIngredients, adapt);

                // Clear the input field
                input.text.clear()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                // Ripristina il background a custom_bkg_button
                bottoneIngrediente.setBackgroundResource(R.drawable.custom_bkg_button)
                // Ripristina la tinta del drawable a coquelicot
                bottoneIngrediente.setColorFilter(ContextCompat.getColor(applicationContext, R.color.coquelicot), PorterDuff.Mode.SRC_IN)
            }, 500) // Ritardo di 500 millisecondi (0.5 secondi)
        }


        //PER QUANDO VIENE CLICCATO IL CAMPO MODIFICA SU DETAIL FRAGMENT
//----------------------------------------------------------------------------------------------------------------------------
        val intent = intent
        val idRicetta = intent.getIntExtra("id_ricetta", -1)


// Popola i campi con i dati ricevuti
        val cursor: Cursor = dbr.rawQuery("SELECT * FROM ricetta WHERE id = ?", arrayOf(idRicetta.toString()))

        if (cursor.moveToFirst()) {
            val nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"))
            val porzioni = cursor.getInt(cursor.getColumnIndexOrThrow("porzioni"))
            val difficolta = cursor.getString(cursor.getColumnIndexOrThrow("difficolta"))
            val tipologia = cursor.getString(cursor.getColumnIndexOrThrow("tipologia"))
            val portata = cursor.getString(cursor.getColumnIndexOrThrow("portata"))
            val dieta = cursor.getString(cursor.getColumnIndexOrThrow("dieta"))
            val etnicita = cursor.getString(cursor.getColumnIndexOrThrow("etnicita"))
            val tempo = cursor.getInt(cursor.getColumnIndexOrThrow("tempo_di_preparazione"))

            val nome_ricetta = findViewById<EditText>(R.id.editTextText)
            val difficoltaView = findViewById<AutoCompleteTextView>(R.id.tendinaDifficolta)
            val tipologiaView = findViewById<AutoCompleteTextView>(R.id.tendinaTipo)
            val portataView = findViewById<AutoCompleteTextView>(R.id.tendina)
            val dietaView = findViewById<AutoCompleteTextView>(R.id.tendinaDieta)
            val etnicitaView = findViewById<AutoCompleteTextView>(R.id.tendinaEtnia)
            val num_porzioni = findViewById<EditText>(R.id.editPorzioni)
            val num_tempo= findViewById<EditText>(R.id.editTempo)

            nome_ricetta.setText(nome)
            difficoltaView.setText(difficolta)
            tipologiaView.setText(tipologia)
            portataView.setText(portata)
            dietaView.setText(dieta)
            etnicitaView.setText(etnicita)
            num_porzioni.setText(porzioni.toString())
            num_tempo.setText(tempo.toString())

            // Richiama la funzione di popolamento per aggiornare l'adapter
            populateAutoCompleteTextView(etnicitaView, "etnicita", "etnicita", this, dbr)
            //updateAutoCompleteTextView(this, etnicitaView, arrayListaEtnia, "etnicita", "etnicita", dbr)

            populateAutoCompleteTextView(dietaView, "dieta", "dieta", this, dbr)
            populateAutoCompleteTextView(tipologiaView, "tipologia", "tipologia", this, dbr)
            populateAutoCompleteTextView(portataView, "portate", "portata", this, dbr)
            populateAutoCompleteTextView(difficoltaView, "difficolta", "difficolta", this, dbr)
        }
        cursor.close()


        //popolo gli ingredienti
        val cur: Cursor = dbr.rawQuery("SELECT * FROM ingredienti_ricetta WHERE id_ricetta = ?", arrayOf(idRicetta.toString()))

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

        cur.close()



        //----------------------------------------------------------------------------------------------------------------------------



        //SCEGLIERE IMMAGINE-------------------------------------------------------------------------------------------------

        btnImmagine = findViewById(R.id.btnfoto)
        imageViewFoto = findViewById(R.id.imageViewFoto)
        val pathImg: String = "default"
        btnImmagine.setOnClickListener{
            chooseImageFromGallery()
        }

        //-------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------
        //SALVARE TUTTO SU DB
        val dialogError = Dialog(this)
        dialogError.setContentView(R.layout.dialog_errore)
        var messaggioErroreModificabile = "Errore"

        val btnok: Button = dialogError.findViewById(R.id.buttonOK)
        val txtErrore: TextView = dialogError.findViewById(R.id.messaggioErrore)
        btnok.setOnClickListener {
            dialogError.dismiss()
        }


        //BOTTONE PER SALVARE TUTTI I DATI, PRIMA SI FANNO I CANTROLLI
        val btnSalva: Button = findViewById(R.id.btnSalvaTutto)
        btnSalva.setOnClickListener{
            //se questa variabile diventa false allora si blocca il procedimento
            var esito: Boolean = true


            //CONTROLLO SUL TITOLO
            val editTitolo: EditText = findViewById(R.id.editTextText)
            var titoloFinale = editTitolo.text.toString()
            if(titoloFinale.trim().isEmpty()) {
                esito = false

                messaggioErroreModificabile = "Il nome non può essere vuoto"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULLA PORTATA
            val portata: AutoCompleteTextView = findViewById(R.id.tendina)
            var txtPortata = portata.text.toString()
            if(txtPortata.isEmpty()){
                esito = false

                messaggioErroreModificabile = "Scegliere una portata"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULLA TIPOLOGIA
            val tipologia : AutoCompleteTextView = findViewById(R.id.tendinaTipo)
            var txtTipologia = tipologia.text.toString()
            if(txtTipologia.isEmpty()){
                esito = false

                messaggioErroreModificabile = "Scegliere una tipologia"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULLA DIETA
            val dieta : AutoCompleteTextView = findViewById(R.id.tendinaDieta)
            var txtDieta = dieta.text.toString()
            if(txtDieta.isEmpty()){
                esito = false

                messaggioErroreModificabile = "Scegliere una dieta"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULL'ETNIA
            val etnia : AutoCompleteTextView = findViewById(R.id.tendinaEtnia)
            var txtEtnia = etnia.text.toString()
            if(txtEtnia.isEmpty()){
                esito = false

                messaggioErroreModificabile = "Scegliere un'etnia"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULLA DIFFICOLTA
            val difficolta : AutoCompleteTextView = findViewById(R.id.tendinaDifficolta)
            var txtDifficolta = difficolta.text.toString()
            if(txtDifficolta.isEmpty()){
                esito = false

                messaggioErroreModificabile = "Scegliere una difficoltà"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SUL NUMERO DI PORZIONI
            val porzioni : EditText = findViewById(R.id.editPorzioni)
            var txtPorzioni = porzioni.text.toString()
            var numPorzioni: Int = 0
            if(txtPorzioni.isEmpty()){
                esito = false

                messaggioErroreModificabile = "Bisogna inserire un numero di porzioni per questa ricetta"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }else{
                numPorzioni = txtPorzioni.toInt()
            }

            //CONTROLLO SUL TEMPO
            val tempo : EditText = findViewById(R.id.editTempo)
            var txtTempo = tempo.text.toString()
            var numTempo: Int = 0
            if(txtTempo.isEmpty()){
                esito = false

                messaggioErroreModificabile = "Bisogna inserire un tempo di preparazione totale per questa ricetta"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }else{
                numTempo = txtTempo.toInt()
            }


            //CONTROLLO SUGLI INGREDIENTI
            if(ArrayListaIngredienti.isEmpty()){
                esito = false

                messaggioErroreModificabile = "Inserire minimo un ingrediente per questa ricetta"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }

            //CONTROLLO SULLA PREPARAZIONE
            var preparazione: String = ""
            if(listaProcedimenti.isEmpty()){
                esito = false

                messaggioErroreModificabile = "Inserire una procedura per questa ricetta"
                txtErrore.text = messaggioErroreModificabile

                dialogError.window!!
                dialogError.setCancelable(false)
                dialogError.show()
            }else{
                preparazione = componiProcedura(listaProcedimenti)
            }


            //TODO controllare il path della foto, se è null mettere 'default'

            //se tutto va bene creo la ricetta e la carico sul DB
            if(esito) {
                var ricetta = RicetteModel(
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

                //Log.d("222222222222222222222222222222222222222222", "qui dovrebbe essere dopo")

                //TODO cambiare il path con quello selezionato
                dbw.execSQL("INSERT INTO ricetta(nome , porzioni ,tempo_di_preparazione, difficolta, tipologia, portata, dieta, etnicita, pathFoto, preparazione, preferito) VALUES ('$titoloFinale', $numPorzioni, $numTempo, '$txtDifficolta', '$txtTipologia', '$txtPortata', '$txtDieta', '$txtEtnia', '${uriFoto.toString()}', '$preparazione', 0)")
            }

            //TODO fare qualcosa per prendere gli ingredienti e le quantità
            // poi inserirli uno alla volta con anche l'ID della ricetta

        }


    }

    private fun chooseImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            selectedImageUri?.let {
                uriFoto = selectedImageUri

                val layoutParams = imageViewFoto.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = 500 // o qualsiasi altra dimensione desiderata
                imageViewFoto.layoutParams = layoutParams

                imageViewFoto.setImageURI(selectedImageUri)
            }
        }else{
            uriFoto = "default".toUri()
        }
    }

    //funzione per comporre la procedura a partire dai passi inseriti dal'utenteto
    private fun componiProcedura(listaProcedimenti: ArrayList<String>): String {
        var ret = ""

        for(i in listaProcedimenti){
            ret = ret + "[[Passo]]$i"
            //Log.d("11111111111111111111111111111111111111111111111111", "$ret")
        }

        return ret
    }


    //FUNZIONE CHE CAMBIA LA DIMENSIONE DELLA LIST VIEW IN BASE AL NUMERO DI ELEMENTI
    private fun setListViewHeightBasedOnItems(listView: ListView, adapter: ListView_adapter) {
        if (adapter == null) {
            return;
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


