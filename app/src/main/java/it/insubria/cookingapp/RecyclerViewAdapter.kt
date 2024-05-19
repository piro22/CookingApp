package it.insubria.cookingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private val context: Context,
                          private val ricettaModel: ArrayList<RicetteModel>,
                          private val recyclerViewInterface: RecyclerViewInterface
                            ) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rec_view_row, parent, false)
        return RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.MyViewHolder, position: Int) {
        holder.txtName.text = ricettaModel.get(position).nome
        holder.difficolta.text = ricettaModel.get(position).difficolta
        holder.portata.text = ricettaModel.get(position).portata
        holder.imageV.setImageURI(ricettaModel.get(position).pathFoto.toUri())
    }

    override fun getItemCount(): Int {
        return ricettaModel.size
    }


    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) : RecyclerView.ViewHolder(itemView) {
        // Puoi definire qui i componenti di layout dell'elemento ViewHolder
        lateinit var imageV : ImageView
        lateinit var txtName : TextView
        lateinit var difficolta : TextView
        lateinit var portata : TextView

        init {
            imageV = itemView.findViewById(R.id.imageView)
            txtName = itemView.findViewById(R.id.textView)
            difficolta = itemView.findViewById(R.id.textView2)
            portata = itemView.findViewById(R.id.textView3)

//            itemView.setOnClickListener{
//                if(recyclerViewInterface != null){
//                    var pos : Int = adapterPosition
//
//                    if(pos != RecyclerView.NO_POSITION){
//                        recyclerViewInterface.onItemClick(pos)
//                    }
//                }
//            }
        }

    }
}