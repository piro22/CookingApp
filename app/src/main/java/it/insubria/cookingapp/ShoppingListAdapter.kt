package it.insubria.cookingapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ShoppingListAdapter(
    context: Context,
    val dbr: SQLiteDatabase,
    private val items: MutableList<String>,
    private val quant: MutableList<Int>
) : ArrayAdapter<String>(context, R.layout.row_shopping_list, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Inflate a new view or reuse an existing one
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.row_shopping_list, parent, false)

        // Get references to the UI components
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)
        val ingrediente: TextView = view.findViewById(R.id.ingrediente)
        val quantita: TextView = view.findViewById(R.id.quantita)

        // Set the text for the ingredient and quantity (assuming items contains both)
        ingrediente.text = items[position]

        quantita.text = quant[position].toString() // Assuming you have separate data for quantity

        // Set up the delete icon click listener
        deleteIcon.setOnClickListener {

            dbr.delete("listaSpesa", "ingrediente = ?", arrayOf(items[position]))


            items.removeAt(position)
            quant.removeAt(position)
            notifyDataSetChanged()
        }

        // Return the prepared view for this position
        return view
    }
}
