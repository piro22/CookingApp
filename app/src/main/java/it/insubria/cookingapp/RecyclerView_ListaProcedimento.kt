package it.insubria.cookingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//adapter una lista di elementi di tipo String
//listener: Un'interfaccia per gestire i clic sull'icona di cancellazione
class RecyclerView_ListaProcedimento(private val listaProcedimenti: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView_ListaProcedimento.ViewHolder>() {

    private var counter = 0


    // classe ViewHolder: serve per rappresentare la vista per un singolo elemento della lista,
    //memorizzo gli elementi UI di ogni elemento, quando istanzio un oggetto ViewHOlder
    // sto rappresentando la vista di un singolo elemento nell'eleenco
    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.row)
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)
        val calendarIcon: ImageView = view.findViewById(R.id.calendarIcon)
        val counterTextView: TextView = view.findViewById(R.id.counterTextView)

        fun aumentaContatore() {
            counter++
            counterTextView.text = "Passo $counter"
        }

        fun rimuoviProcedura(posizione:Int){
            listaProcedimenti.removeAt(posizione)
            notifyItemRemoved(posizione)
        }


    }


    //Crea una nuova vista per un elemento della lista.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_procedimenti, parent, false)

        //restituisce un oggetto di tipo ViewHolder
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaProcedimenti.size
    }



    //metodo chiamato ogni volta che la RecyclerView ha bisogno di aggiornare il contenuto di un elemento nell'elenco
    //Prende come argomenti un oggetto ViewHolder e una posizione, che indica la posizione dell'elemento nell'elenco.
    override fun onBindViewHolder(view: ViewHolder, position: Int) {

        val procedimento = listaProcedimenti[position]

        // Imposta il testo della TextView con l'elemento corrispondente
        view.textView.text = procedimento

        // Chiamo il metodo bind per aggiornare il contatore
        view.aumentaContatore()

        view.deleteIcon.setOnClickListener{
            view.rimuoviProcedura(position)
        }

        }



    }