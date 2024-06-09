package it.insubria.cookingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class ListView_adapter (context: Context, private val items: List<String>) :
    ArrayAdapter<String>(context, R.layout.row_ingredienti, items){

        override fun getView (position: Int, convertView: View?, parent: ViewGroup): View {
            // Ottiene la vista esistente, o ne crea una nuova se necessario
            val view: View = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.row_ingredienti, parent, false)


            val contatore: TextView = view.findViewById(R.id.numeri)
            val tendina : AutoCompleteTextView = view.findViewById(R.id.tendina)
            val ingrediente : TextView = view.findViewById(R.id.ingrediente)

            val shoppingIcon : ImageView = view.findViewById(R.id.listViewShopping)
            val deleteIcon : ImageView = view.findViewById(R.id.deleteIcon)


            var item= contatore.text.toString().toInt()

            // Ritorna la vista per la riga corrente
            return view


        }







        }