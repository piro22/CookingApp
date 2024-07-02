package it.insubria.cookingapp

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var prodotti : ArrayList<String>
private lateinit var quantita : ArrayList<Int>
var DOT = "-->  "

/**
 * A simple [Fragment] subclass.
 * Use the [ShoppingListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShoppingListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val ret = inflater.inflate(R.layout.fragment_shopping_list, container, false)
        val btnLista: ImageView = ret.findViewById(R.id.bottoneAddFood)
        val lista : ListView = ret.findViewById(R.id.listViewShopping)
        val txtFood : EditText = ret.findViewById(R.id.txtAddFood)
        val txtQuant : EditText = ret.findViewById(R.id.txtAddQuantita)


        //DATABASE
        //setto variabili  per database
        val dataModel = ViewModelProvider(requireActivity()).get(DataModel::class.java)
        val dbHelper = dataModel.dbHelper
        val dbr = dbHelper!!.readableDatabase
        val dbw = dbHelper!!.writableDatabase

        //INIZIALIZZO LA LISTA CHE METTERO' NELLA ListView
        prodotti = ArrayList<String>()
        quantita = ArrayList<Int>()

        // adapter per la listview
        val adapter = ShoppingListAdapter(requireContext(), dbr, prodotti, quantita)
        lista.adapter = adapter

        val button = ret.findViewById<Button>(R.id.buttonClearList)


        fun clearDatabase(adapter: ShoppingListAdapter) {
            val dbHelper = Database_SQL(requireContext())
            val dbw = dbHelper.writableDatabase

            // Clear the entire database table
            dbw.delete("listaSpesa", null, null)

            // Clear the list and notify the adapter
            prodotti.clear()
            quantita.clear()
            adapter.notifyDataSetChanged()
        }


        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirm Clear")
            .setMessage("Are you sure you want to clear this list?")
            .setPositiveButton("Yes") { _, _ ->
                clearDatabase(adapter)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Set an OnClickListener on the clear button
        button.setOnClickListener {
            dialog.show()
        }



        fun populateIngredientiList() {
            val cursor = dbr.rawQuery("SELECT ingrediente, quantita FROM listaSpesa", null)

            prodotti.clear()
            quantita.clear()

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val portataIndex = cursor.getColumnIndex("ingrediente")
                    val quantIndex = cursor.getColumnIndex("quantita")
                    if (portataIndex >= 0) {
                        val p = cursor.getString(portataIndex)
                        val q = cursor.getInt(quantIndex)
                        prodotti.add(p)
                        quantita.add(q)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
        }

        // Popola arrayListaPortate con i dati esistenti nel database all'avvio
        populateIngredientiList()




        //AGGIUNGO ITEM ALLA LISTA

        fun ingredienteEsiste(db: SQLiteDatabase, ingrediente: String): Boolean {
            val cursor =
                db.rawQuery("SELECT 1 FROM listaSpesa WHERE ingrediente = ?", arrayOf(ingrediente))
            val exists = cursor.moveToFirst()
            cursor.close()
            return exists
        }

        btnLista.setOnClickListener {

            btnLista.setBackgroundResource(R.drawable.custom_bkg_button_full)
            btnLista.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN)


            val food = txtFood.text.toString()
            val quantity = txtQuant.text.toString()
            if (food.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Testo vuoto o nullo", Toast.LENGTH_SHORT).show()
            } else {
                //se l'ingrediente esiste aggiungo 1 di quantita e basta
                if(ingredienteEsiste(dbr, food)){
                   //prima prendo la quantita dell'ingrediente
                    val cursor = dbr.rawQuery("SELECT quantita FROM listaSpesa WHERE ingrediente = ?", arrayOf(food))
                    if (cursor.moveToFirst()) {
                        val currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantita"))
                        var newQuantity = 0
                        if(quantity.isNullOrBlank())
                             newQuantity = currentQuantity + 1
                        else
                            newQuantity = currentQuantity + quantity.toInt()

                        //aggiungo 1 alla quantita presa
                        val values = ContentValues().apply {
                            put("quantita", newQuantity)
                        }
                        //aggiorno la query
                        dbw.update("listaSpesa", values, "ingrediente = ?", arrayOf(food))
                    }
                    cursor.close()

                    //aggiorno poi direttamente dal database
                    populateIngredientiList()

                }else {

                    if (quantity.isNullOrBlank()) {
                        val nuovoValore = ContentValues().apply {
                            put("ingrediente", food)
                            put("quantita", 1)
                        }

                        //aggiungo il nuovoValore all'interno del db
                        dbw.insert("listaSpesa", null, nuovoValore)

                    } else {
                        val numQ = quantity.toInt()
                        val nuovoValore = ContentValues().apply {
                            put("ingrediente", food)
                            put("quantita", numQ)
                        }

                        //aggiungo il nuovoValore all'interno del db
                        dbw.insert("listaSpesa", null, nuovoValore)
                    }

                    txtFood.setText("")
                    txtQuant.setText("")

                    prodotti.add("$DOT $food")
                    quantita.add(quantity.toInt())

                    adapter.notifyDataSetChanged()

                    Toast.makeText(requireContext(), "$food aggiunto", Toast.LENGTH_SHORT).show()
                }
            }

            Handler(Looper.getMainLooper()).postDelayed({
                // Ripristina il background a custom_bkg_button
                btnLista.setBackgroundResource(R.drawable.custom_bkg_button)
                // Ripristina la tinta del drawable a coquelicot
                btnLista.setColorFilter(ContextCompat.getColor(requireContext(), R.color.coquelicot), PorterDuff.Mode.SRC_IN)
            }, 500) // Ritardo di 500 millisecondi (0.5 secondi)

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
         * @return A new instance of fragment ShoppingListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShoppingListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}