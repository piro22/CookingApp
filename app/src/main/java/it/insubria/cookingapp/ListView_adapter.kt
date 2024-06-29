package it.insubria.cookingapp

import AutoComplete_adapter
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout

// Extends the ArrayAdapter class and inherits the getView method
class ListView_adapter(context: Context, private val items: MutableList<String>) :
    ArrayAdapter<String>(context, R.layout.row_ingredienti, items) {
    private var arrayListaUnita: MutableList<String> = mutableListOf()

    // Passes parameters: position, view, and viewGroup
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Recycle or create a new view as necessary
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_ingredienti, parent, false)

        // Find views

        val quantita: EditText = view.findViewById(R.id.tendina)
        val ingrediente: TextView = view.findViewById(R.id.ingrediente)
        val unit: AutoCompleteTextView = view.findViewById(R.id.tendinaUnita)
        val shoppingIcon: ImageView = view.findViewById(R.id.calendarIcon)
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)

        // Set ingredient name and position
        ingrediente.text = items[position]



        //database
        //setto variabili  per database
        val dbHelper = Database_SQL(context)
        val dbr = dbHelper.readableDatabase
        val dbw = dbHelper.writableDatabase


        //POPOLO LA TENDINA
        val cursorPort = dbr.rawQuery("SELECT unita FROM unita_di_misura", null)

        arrayListaUnita.clear()

        if (cursorPort != null && cursorPort.moveToFirst()) {
            do {
                val unitaIndex = cursorPort.getColumnIndex("unita")
                if (unitaIndex >= 0) {
                    val u = cursorPort.getString(unitaIndex)
                    arrayListaUnita.add(u)
                }
            } while (cursorPort.moveToNext())
            cursorPort.close()
        }


        //creo un adapter per passare i valori dell'array delle portate all'interno della tendina
        val adapterTendina = ArrayAdapter(
            context,
            android.R.layout.simple_dropdown_item_1line,
            arrayListaUnita.toTypedArray()
        )
        //per aggiornare la vista(tendina)
        unit.setAdapter(adapterTendina)
        //------------------------------------------------------------------------------------------------


        val i = ingrediente.text.toString()
        // Set click listeners for shopping and delete icons
        shoppingIcon.setOnClickListener {
            val nuovoValore = ContentValues().apply {
                put("ingrediente", i)
            }
            //aggiungo il nuovoValore all'interno del db
            dbw.insert("listaSpesa", null, nuovoValore)


            Toast.makeText(context, "Aggiungi ${items[position]} alla lista della spesa", Toast.LENGTH_SHORT).show()
        }

        deleteIcon.setOnClickListener {

            val ingredientToDelete = items[position]

            // Delete the item from the database
            val whereClause = "ingrediente = ?"
            val whereArgs = arrayOf(ingredientToDelete)
            dbw.delete("listaSpesa", whereClause, whereArgs)

            // Remove the item from the list and notify the adapter
            items.removeAt(position)
            notifyDataSetChanged()

            Toast.makeText(context, "Elimina $ingredientToDelete", Toast.LENGTH_SHORT).show()


        }

        // Return the current row view
        return view
    }
}
