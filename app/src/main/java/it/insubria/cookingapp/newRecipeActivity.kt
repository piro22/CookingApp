package it.insubria.cookingapp

import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
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

class newRecipeActivity : AppCompatActivity()  {


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_newrecipe)


            // Find the main view by ID
            val mainView = findViewById<View>(R.id.main)

            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }





            //DATABASE

//----------------------------------------------------------------------------------------------------------------------------

            //setto variabili  per database
            val dbHelper = Database(this)
            val db = dbHelper.readableDatabase
//----------------------------------------------------------------------------------------------------------------------------







            // Gestione pop_up per portate
//----------------------------------------------------------------------------------------------------------------------------


            val dialog = Dialog(this)
            dialog.setContentView(R.layout.pop_up_portate)

            var arrayPortate: MutableList<String>  = mutableListOf()




            fun populatePortateList() {

                val cursor = db.rawQuery("SELECT portata FROM portate", null)
                arrayPortate.clear()
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val portataIndex = cursor.getColumnIndex("portata")
                        if (portataIndex >= 0) {
                            val p = cursor.getString(portataIndex)
                            arrayPortate.add(p)
                        }
                    } while (cursor.moveToNext())
                    cursor.close()
                }
            }

            // Popola arrayPortate con i dati esistenti nel database all'avvio
            populatePortateList()



            val listaPortate: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView2)
            val adapterAutoComplete = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayPortate.toTypedArray())
            listaPortate.setAdapter(adapterAutoComplete)




            val btn_salva: Button = dialog.findViewById(R.id.save)
            val btn_annulla: Button = dialog.findViewById(R.id.annulla)


            btn_annulla.setOnClickListener {
                dialog.dismiss()
                Log.d("MainActivity2", "Dialogo chiuso")
            }

            btn_salva.setOnClickListener {
                val aggiungi = dialog.findViewById<EditText>(R.id.aggiungi)
                val newPortata = aggiungi.text.toString()

                // Controlla se la nuova portata esiste già nella lista
                if (!arrayPortate.contains(newPortata)) {
                    val values = ContentValues().apply {
                        put("portata", newPortata)
                    }
                    db.insert("portate", null, values)
                    arrayPortate.add(newPortata)

                    // Aggiorna l'adapter dell'AutoCompleteTextView
                    val updatedAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayPortate.toTypedArray())
                    listaPortate.setAdapter(updatedAdapter)
                } else {
                    Log.d("MainActivity2", "Portata già presente")
                }

                dialog.dismiss()
            }




            val btn_portate: Button = findViewById(R.id.btnPortate)






            btn_portate.setOnClickListener {
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.setCancelable(false)
                dialog.show()
            }

//----------------------------------------------------------------------------------------------------------------------------








            //AGGIUNGERE PROCEDURE

//----------------------------------------------------------------------------------------------------------------------------

            val listaProcedimenti : ArrayList<String> = ArrayList()
            val btn_done: Button = findViewById(R.id.btnDone)
            val textProcedimento: EditText = findViewById(R.id.TextProcedimento)
            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            val adapter :RecyclerView_newRecipe
            adapter = RecyclerView_newRecipe(listaProcedimenti, object : RecyclerView_newRecipe.OnDeleteIconClickListener {
                override fun onDeleteIconClick(position: Int) {
                    // Implementazione per la rimozione dell'elemento dalla lista di procedimenti e l'aggiornamento dell'adapter
                    listaProcedimenti.removeAt(position)
                    //adapter.notifyItemRemoved(position)
                    //adapter.notifyItemRangeChanged(position, listaProcedimenti.size)
                }
            })

            recyclerView.adapter = adapter


            recyclerView.layoutManager = LinearLayoutManager(this)


            btn_done.setOnClickListener {
                val nuovoProcedimento = textProcedimento.text.toString()
                listaProcedimenti.add(nuovoProcedimento)
                adapter.notifyItemInserted(listaProcedimenti.size - 1)
                textProcedimento.text = null
            }




//------------------------------------------------------------------------------------------------------------------------------





            //aggiungere gli ingredienti
//----------------------------------------------------------------------------------------------------------------------------
            val input = findViewById<EditText>(R.id.input)
            val ArrayListaIngredienti = ArrayList<String>()
            val adapters = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ArrayListaIngredienti)
            val listViewIngredients = findViewById<ListView>(R.id.listviewl)

            val enter = findViewById<ImageView>(R.id.aggiungi)
            listViewIngredients.setAdapter(adapters)

            fun removeItem( position: Int){
               // ArrayListaIngredienti.removeAt(position)
                listViewIngredients.setAdapter(adapters)
            }

            //questo metodo viene ereditato da delle inerfacce
            listViewIngredients.setOnItemLongClickListener{ parent, view, position, id ->
                val selectedItem = adapters.getItem(position)
                removeItem(position)
                // Esempio di azione da eseguire
                Toast.makeText(this, "Hai premuto a lungo l'elemento: $selectedItem", Toast.LENGTH_SHORT).show()

                // Restituisci true per indicare che l'evento è stato gestito correttamente
                true
            }

            fun inserisciInLista(testo: String){
                if(testo.isNotEmpty()){

                    listViewIngredients.setAdapter(adapters)
                    //ArrayListaIngredienti.add(testo) // Aggiungi il testo alla lista degli ingredienti
                    adapter.notifyDataSetChanged()
                    // Aggiorna l'adapter della ListView

                    Toast.makeText(this, "Added: $testo", Toast.LENGTH_SHORT).show()
                    input.text.clear()
                }}





            input.setOnEditorActionListener{ _, actionId,_ ->
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    val testo = input.text.toString().trim()
                    inserisciInLista(testo)
                }
                true

            }


            enter.setOnClickListener{
                val testo = input.text.toString().trim()
                inserisciInLista(testo)

            }




            //----------------------------------------------------------------------------------------------------------------------------

        }


    }


