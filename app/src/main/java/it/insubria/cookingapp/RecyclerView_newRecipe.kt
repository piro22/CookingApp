package it.insubria.cookingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//adattatore per una lista di elementi di tipo String
//listener: Un'interfaccia per gestire i clic sull'icona di cancellazione
class RecyclerView_newRecipe
    (private val procedimenti: ArrayList<String> ,  private val listener: OnDeleteIconClickListener) :
        RecyclerView.Adapter<RecyclerView_newRecipe.ViewHolder>() {

        private var counter = 0

        //implementata in mainActivity2

        interface OnDeleteIconClickListener {
            fun onDeleteIconClick(position: Int)
        }


        // rappresenta la vista per un singolo elemento della lista
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.row)
            val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)
            val calendarIcon: ImageView = view.findViewById(R.id.calendarIcon)
            val counterTextView: TextView = view.findViewById(R.id.counterTextView)


            fun bind() {
                counter++
                counterTextView.text = "Passo $counter"
            }
        }



        //Crea una nuova vista per un elemento della lista.
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_procedimenti, parent, false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return procedimenti.size
        }

        override fun onBindViewHolder(view: ViewHolder, position: Int) {
            val p = procedimenti[position]
            view.textView.text = p
            // Chiamo il metodo bind per aggiornare il contatore
            view.bind()


            view.deleteIcon.setOnClickListener{
                listener.onDeleteIconClick(position)
            }



            view.calendarIcon.setOnClickListener {
                // Azioni da eseguire quando l'icona del calendario viene cliccata
            }
        }


    }

