package it.insubria.cookingapp

import android.app.Dialog
import android.content.ContentValues
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class newRecipeActivity : AppCompatActivity() {


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


        // Gestione pop_up per portate + CONNESSIONE CON DB
//----------------------------------------------------------------------------------------------------------------------------
        //creo il dialog (piccola finestra)
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_portate)

        //creo una lista dove aggiungo ogni volta una portata
        var arrayListaPortate: MutableList<String> = mutableListOf()


        //serve per popolare la tendina
        fun populatePortateList() {
            val cursor = dbr.rawQuery("SELECT portata FROM portate", null)

            arrayListaPortate.clear()

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val portataIndex = cursor.getColumnIndex("portata")
                    if (portataIndex >= 0) {
                        val p = cursor.getString(portataIndex)
                        arrayListaPortate.add(p)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
        }

        // Popola arrayListaPortate con i dati esistenti nel database all'avvio
        populatePortateList()


        //prendo la tendina
        val listaPortate: AutoCompleteTextView = findViewById(R.id.tendina)

        //creo un adapter per passare i valori dell'array delle portate all'interno della tendina
        val adapterTendina = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            arrayListaPortate.toTypedArray()
        )
        //per aggiornare la vista(tendina)
        listaPortate.setAdapter(adapterTendina)


        val btn_annulla: ImageView = dialog.findViewById(R.id.annullaBtn)
        btn_annulla.setOnClickListener {
            dialog.dismiss()
            Log.d("MainActivity2", "Dialogo chiuso")
        }

        val btn_salva: Button = dialog.findViewById(R.id.saveBtn)
        btn_salva.setOnClickListener {
            val nuovaPortata = (dialog.findViewById<EditText>(R.id.aggiungi)).text.toString()

            // Controlla se la nuova portata esiste già nella lista
            if (!arrayListaPortate.contains(nuovaPortata)) {
                val nuovoValore = ContentValues().apply {
                    put("portata", nuovaPortata)
                }
                //aggiungo il nuovoValore all'interno del db
                dbw.insert("portate", null, nuovoValore)
                //aggiungo all'array la nuovaPortata
                arrayListaPortate.add(nuovaPortata)

                // Aggiorna l'adapter dell'AutoCompleteTextView
                val updatedAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    arrayListaPortate.toTypedArray()
                )
                //per aggiornare la vista(tendina)
                listaPortate.setAdapter(updatedAdapter)
            } else {
                Log.d("MainActivity2", "Portata già presente")
            }

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

        //serve per popolare la tendina
        fun populateDietaList() {
            val cursor = dbr.rawQuery("SELECT dieta FROM dieta", null)

            arrayListaDieta.clear() // Correctly clear the dieta list

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val dietaIndex = cursor.getColumnIndex("dieta")
                    if (dietaIndex >= 0) {
                        val d = cursor.getString(dietaIndex)
                        arrayListaDieta.add(d)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
        }

// Popola arrayListaDieta con i dati esistenti nel database all'avvio
        populateDietaList()

        val listaDieta: AutoCompleteTextView =
            findViewById(R.id.tendinaDieta) // Correct ID for dieta dropdown

//creo un adapter per passare i valori dell'array delle portate all'interno della tendina
        val adapterTendinaD = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            arrayListaDieta.toTypedArray()
        )
//per aggiornare la vista(tendina)
        listaDieta.setAdapter(adapterTendinaD)

        val btn_annullaD: ImageView = dialogD.findViewById(R.id.annullaBtnD)
        btn_annullaD.setOnClickListener {
            dialogD.dismiss()
            Log.d("MainActivity2", "Dialogo chiuso")
        }

        val btn_salvaD: Button = dialogD.findViewById(R.id.saveBtnD) // Correct reference to dialogD
        btn_salvaD.setOnClickListener {
            val nuovaDieta = (dialogD.findViewById<EditText>(R.id.aggiungiD)).text.toString()

            // Controlla se la nuova dieta esiste già nella lista
            if (!arrayListaDieta.contains(nuovaDieta)) {
                val nuovoValore = ContentValues().apply {
                    put("dieta", nuovaDieta)
                }
                //aggiungo il nuovoValore all'interno del db
                dbw.insert("dieta", null, nuovoValore)
                //aggiungo all'array la nuovaDieta
                arrayListaDieta.add(nuovaDieta)

                // Aggiorna l'adapter dell'AutoCompleteTextView
                val updatedAdapterD = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    arrayListaDieta.toTypedArray()
                )
                //per aggiornare la vista(tendina)
                listaDieta.setAdapter(updatedAdapterD)
            } else {
                Log.d("MainActivity2", "Dieta già presente")
            }

            dialogD.dismiss()
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

        //serve per popolare la tendina
        fun populateEtniaList() {
            val cursor = dbr.rawQuery("SELECT etnicita FROM etnicita", null)

            arrayListaEtnia.clear()

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val EtniaIndex = cursor.getColumnIndex("etnicita")
                    if (EtniaIndex >= 0) {
                        val p = cursor.getString(EtniaIndex)
                        arrayListaEtnia.add(p)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
        }

        // Popola arrayListaPortate con i dati esistenti nel database all'avvio
        populateEtniaList()

        val listaEtnia: AutoCompleteTextView = findViewById(R.id.tendinaEtnia)

        //creo un adapter per passare i valori dell'array delle portate all'interno della tendina
        val adapterEtnia = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            arrayListaEtnia.toTypedArray()
        )
        //per aggiornare la vista(tendina)
        listaEtnia.setAdapter(adapterEtnia)

        val btn_annullaE: ImageView = dialogE.findViewById(R.id.annullaBtnE)
        btn_annullaE.setOnClickListener {
            dialogE.dismiss()
            Log.d("MainActivity2", "Dialogo chiuso")
        }

        val btn_salvaE: Button = dialogE.findViewById(R.id.saveBtnE) // Correct reference to dialogD
        btn_salvaE.setOnClickListener {
            val nuovaEtnia = (dialogE.findViewById<EditText>(R.id.aggiungiE)).text.toString()

            // Controlla se la nuova dieta esiste già nella lista
            if (!arrayListaEtnia.contains(nuovaEtnia)) {
                val nuovoValore = ContentValues().apply {
                    put("etnicita", nuovaEtnia)
                }
                //aggiungo il nuovoValore all'interno del db
                dbw.insert("etnicita", null, nuovoValore)
                //aggiungo all'array la nuovaDieta
                arrayListaEtnia.add(nuovaEtnia)

                // Aggiorna l'adapter dell'AutoCompleteTextView
                val updatedAdapterE = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    arrayListaEtnia.toTypedArray()
                )
                //per aggiornare la vista(tendina)
                listaEtnia.setAdapter(updatedAdapterE)
            } else {
                Log.d("MainActivity2", "Etnia già presente")
            }

            dialogE.dismiss()
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

        //serve per popolare la tendina
        fun populateTipoList() {
            val cursor = dbr.rawQuery("SELECT tipologia FROM tipologia", null)

            arrayListaTipo.clear()

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val tipoIndex = cursor.getColumnIndex("tipologia")
                    if (tipoIndex >= 0) {
                        val p = cursor.getString(tipoIndex)
                        arrayListaTipo.add(p)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
        }

        // Popola arrayListaPortate con i dati esistenti nel database all'avvio
        populateTipoList()

        val listaTipo: AutoCompleteTextView = findViewById(R.id.tendinaTipo)

        //creo un adapter per passare i valori dell'array delle portate all'interno della tendina
        val adapterTipo = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            arrayListaTipo.toTypedArray()
        )
        //per aggiornare la vista(tendina)
        listaTipo.setAdapter(adapterTipo)

        val btn_annullaT: ImageView = dialogT.findViewById(R.id.annullaBtnT)
        btn_annullaT.setOnClickListener {
            dialogT.dismiss()
            Log.d("MainActivity2", "Dialogo chiuso")
        }


        val btn_salvaT: Button = dialogT.findViewById(R.id.saveBtnT) // Correct reference to dialogD
        btn_salvaT.setOnClickListener {
            val nuovaTipologia = (dialogT.findViewById<EditText>(R.id.aggiungiT)).text.toString()

            // Controlla se la nuova dieta esiste già nella lista
            if (!arrayListaTipo.contains(nuovaTipologia)) {
                val nuovoValore = ContentValues().apply {
                    put("tipologia", nuovaTipologia)
                }
                //aggiungo il nuovoValore all'interno del db
                dbw.insert("tipologia", null, nuovoValore)
                //aggiungo all'array la nuovaDieta
                arrayListaTipo.add(nuovaTipologia)

                // Aggiorna l'adapter dell'AutoCompleteTextView
                val updatedAdapterT = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    arrayListaTipo.toTypedArray()
                )
                //per aggiornare la vista(tendina)
                listaTipo.setAdapter(updatedAdapterT)
            } else {
                Log.d("MainActivity2", "Tipologia già presente")
            }

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
        fun populateDifficoltaList() {
            val cursor = dbr.rawQuery("SELECT difficolta FROM difficolta", null)

            arrayListaDifficolta.clear()

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
        }

        // Popola arrayListaPortate con i dati esistenti nel database all'avvio
        populateDifficoltaList()


        val listaDiff: AutoCompleteTextView = findViewById(R.id.tendinaDifficolta)


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
        /*
                input.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        val testo = input.text.toString().trim()
                        inserisciInLista(testo)
                    }
                    true
        */


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


        //per popolare gli t5


        //----------------------------------------------------------------------------------------------------------------------------


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
                    "default",
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
                dbw.execSQL("INSERT INTO ricetta(nome , porzioni ,tempo_di_preparazione, difficolta, tipologia, portata, dieta, etnicita, pathFoto, preparazione, preferito) VALUES ('$titoloFinale', $numPorzioni, $numTempo, '$txtDifficolta', '$txtTipologia', '$txtPortata', '$txtDieta', '$txtEtnia', 'default', '$preparazione', 0)")
            }

            //TODO fare qualcosa per prendere gli ingredienti e le quantità
            // poi inserirli uno alla volta con anche l'ID della ricetta

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


