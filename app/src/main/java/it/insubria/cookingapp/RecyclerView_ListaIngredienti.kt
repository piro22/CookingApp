package it.insubria.cookingapp

import android.content.ContentValues
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RecyclerView_ListaIngredienti(
    private val context: Context,
    private val items: MutableList<String>,
    private val quant: MutableList<Int>,
    private val unita: MutableList<String>,
    private val listener: OnItemChangeListener
) : RecyclerView.Adapter<RecyclerView_ListaIngredienti.ViewHolder>() {

    private var arrayListaUnita: MutableList<String> = mutableListOf()

    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val quantita: EditText
        val ingrediente: TextView
        val unit: AutoCompleteTextView
        val shoppingIcon: ImageView
        val deleteIcon: ImageView

        init{
            quantita = view.findViewById(R.id.tendina)
            ingrediente = view.findViewById(R.id.ingrediente)
            unit = view.findViewById(R.id.tendinaUnita)
            shoppingIcon = view.findViewById(R.id.calendarIcon)
            deleteIcon = view.findViewById(R.id.deleteIcon)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView_ListaIngredienti.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_ingredienti, parent, false)

        //restituisce un oggetto di tipo ViewHolder
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView_ListaIngredienti.ViewHolder, position: Int) {
        holder.ingrediente.text = items[position]
        holder.quantita.setText(quant[position].toString())
        holder.unit.setText(unita[position])

        // Database setup
        val dbHelper = Database_SQL(context)
        val dbr = dbHelper.readableDatabase
        val dbw = dbHelper.writableDatabase

        //popola tendina---------------------------------------------------------------------------------------
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

        val adapterTendina = ArrayAdapter(
            context,
            android.R.layout.simple_dropdown_item_1line,
            arrayListaUnita.toTypedArray()
        )
        holder.unit.setAdapter(adapterTendina)
        //-----------------------------------------------------------------------------------------------------


        //funzione per salvare cambiamento in quantità
        holder.quantita.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val newQuantity = s.toString().toIntOrNull()
                if (newQuantity != null) {
                    listener.onQuantityChanged(holder.adapterPosition, newQuantity)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //funzione per salvare cambiamento in unità
        holder.unit.setOnItemClickListener { parent, view, unitposition, id ->
            val selectedUnit = parent.getItemAtPosition(unitposition).toString()
            Log.d("ERRORE", "$selectedUnit posizione: $position")
            listener.onUnitChanged(position, selectedUnit)
        }



        //OnClickListeners per shopping e delete icons-------------------------------------------------------------
        holder.shoppingIcon.setOnClickListener {
            val i = holder.ingrediente.text.toString()
            val q = holder.quantita.text.toString()
            var nuovoValore = ContentValues().apply {}

            if (q.isEmpty()) {
                nuovoValore = ContentValues().apply {
                    put("ingrediente", i)
                    put("quantita", 1)
                }
            } else {
                nuovoValore = ContentValues().apply {
                    put("ingrediente", i)
                    put("quantita", q.toInt())
                }
            }

            dbw.insert("listaSpesa", null, nuovoValore)
            Toast.makeText(context, "Aggiungi ${items[position]} alla lista della spesa", Toast.LENGTH_SHORT).show()
        }


        holder.deleteIcon.setOnClickListener {
            val ingredientToDelete = items[position]

            val whereClause = "ingrediente = ?"
            val whereArgs = arrayOf(ingredientToDelete)
            dbw.delete("listaSpesa", whereClause, whereArgs)

            items.removeAt(position)
            quant.removeAt(position)
            unita.removeAt(position)
            notifyItemRemoved(position)

            Toast.makeText(context, "Elimina $ingredientToDelete", Toast.LENGTH_SHORT).show()
        }
        //-------------------------------------------------------------------------------------------------------

    }

    override fun getItemCount(): Int {
        return items.size
    }

}