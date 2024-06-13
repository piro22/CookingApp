package it.insubria.cookingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

// Extends the ArrayAdapter class and inherits the getView method
class ListView_adapter(context: Context, private val items: MutableList<String>) :
    ArrayAdapter<String>(context, R.layout.row_ingredienti, items) {

    // Passes parameters: position, view, and viewGroup
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Recycle or create a new view as necessary
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.row_ingredienti, parent, false)

        // Find views
        val contatore: TextView = view.findViewById(R.id.numeri)
        val tendina: AutoCompleteTextView = view.findViewById(R.id.tendina)
        val ingrediente: TextView = view.findViewById(R.id.ingrediente)

        val shoppingIcon: ImageView = view.findViewById(R.id.calendarIcon)
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)

        // Set ingredient name and position
        ingrediente.text = items[position]
        contatore.text = (position + 1).toString()

        // Set click listeners for shopping and delete icons
        shoppingIcon.setOnClickListener {
            Toast.makeText(context, "Aggiungi ${items[position]} alla lista della spesa", Toast.LENGTH_SHORT).show()
        }

        deleteIcon.setOnClickListener {
            Toast.makeText(context, "Elimina ${items[position]}", Toast.LENGTH_SHORT).show()
            items.removeAt(position)
            notifyDataSetChanged()
        }

        // Return the current row view
        return view
    }
}
