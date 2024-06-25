package it.insubria.cookingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

//adapter una lista di elementi di tipo String
//listener: Un'interfaccia per gestire i clic sull'icona di cancellazione
class RecyclerView_ListaProcedimento(private val listaProcedimenti: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView_ListaProcedimento.ViewHolder>() {


    // classe ViewHolder: serve per rappresentare la vista per un singolo elemento della lista,
    //memorizzo gli elementi UI di ogni elemento, quando istanzio un oggetto ViewHOlder
    // sto rappresentando la vista di un singolo elemento nell'eleenco
    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.row)
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)
        val calendarIcon: ImageView = view.findViewById(R.id.calendarIcon)
        val contatore: TextView = view.findViewById(R.id.counterTextView)

        fun aumentaContatore(procedimento: String, posizione: Int) {
            contatore.text="Passo $posizione"
            textView.text=procedimento

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
    override fun onBindViewHolder(view: ViewHolder, posizione: Int) {

        val procedimento = listaProcedimenti[posizione]

        view.aumentaContatore(procedimento, posizione)

        view.deleteIcon.setOnClickListener{
            view.rimuoviProcedura(posizione)
        }






    }}