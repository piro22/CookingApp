package it.insubria.cookingapp

import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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


        // Gestione pop_up per portate
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



        val btn_annulla: Button = dialog.findViewById(R.id.annullaBtn)
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


        val widthInDp = 250
        val heightInDp = 250
        val scale = this.resources.displayMetrics.density
        val widthInPx = (widthInDp * scale + 0.5f).toInt()
        val heightInPx = (heightInDp * scale + 0.5f).toInt()

        btn_portate.setOnClickListener {
            dialog.window!!.setLayout(
                widthInPx,
                heightInPx
            )
            dialog.setCancelable(false)
            dialog.show()
        }

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
        recyclerViewProcedimento.adapter= adapterProcedimento
        //questo per come disporre il layout
        recyclerViewProcedimento.layoutManager = LinearLayoutManager(this)


        val btn_done: Button = findViewById(R.id.btnDone)
        val textProcedimento: EditText = findViewById(R.id.TextProcedimento)
        btn_done.setOnClickListener{
            val temp=textProcedimento.text.toString()

            listaProcedimenti.add(temp)
            // Chiamata al metodo per aggiornare la vista
            adapterProcedimento.notifyDataSetChanged()

        }






//------------------------------------------------------------------------------------------------------------------------------


        //aggiungere gli ingredienti
//----------------------------------------------------------------------------------------------------------------------------
        val input = findViewById<EditText>(R.id.input)
        val ArrayListaIngredienti = ArrayList<String>()
        val adapters =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ArrayListaIngredienti)
        val listViewIngredients = findViewById<ListView>(R.id.listviewl)

        val enter = findViewById<ImageView>(R.id.aggiungi)
        listViewIngredients.setAdapter(adapters)

        fun removeItem(position: Int) {
            // ArrayListaIngredienti.removeAt(position)
            listViewIngredients.setAdapter(adapters)
        }

        //questo metodo viene ereditato da delle inerfacce
        listViewIngredients.setOnItemLongClickListener { parent, view, position, id ->
            val selectedItem = adapters.getItem(position)
            removeItem(position)
            // Esempio di azione da eseguire
            Toast.makeText(
                this,
                "Hai premuto a lungo l'elemento: $selectedItem",
                Toast.LENGTH_SHORT
            ).show()

            // Restituisci true per indicare che l'evento è stato gestito correttamente
            true
        }

        fun inserisciInLista(testo: String) {
            if (testo.isNotEmpty()) {

                listViewIngredients.setAdapter(adapters)
                ArrayListaIngredienti.add(testo) // Aggiungi il testo alla lista degli ingredienti
                adapters.notifyDataSetChanged()
                // Aggiorna l'adapter della ListView

                Toast.makeText(this, "Added: $testo", Toast.LENGTH_SHORT).show()
                input.text.clear()
            }
        }





        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val testo = input.text.toString().trim()
                inserisciInLista(testo)
            }
            true

        }


        enter.setOnClickListener {
            val testo = input.text.toString().trim()
            inserisciInLista(testo)

        }


        //----------------------------------------------------------------------------------------------------------------------------

    }


}


