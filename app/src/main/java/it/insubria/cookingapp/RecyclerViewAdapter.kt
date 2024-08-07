package it.insubria.cookingapp

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import java.io.InputStream

class RecyclerViewAdapter(private val context: Context,
                          private val ricettaModel: ArrayList<RicetteModel>,
                          private val recyclerViewInterface: RecyclerViewInterface
                            ) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>()
{
    private var filteredItemList: ArrayList<RicetteModel> = ArrayList(ricettaModel)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.rec_view_row, parent, false)
        return RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.MyViewHolder, position: Int) {
        holder.txtName.text = filteredItemList.get(position).nome
        holder.difficolta.text = filteredItemList.get(position).difficolta
        holder.portata.text = filteredItemList.get(position).portata

        val path = filteredItemList.get(position).pathFoto
        if (path != "default") {
            // Determina il permesso da richiedere in base alla versione di Android
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                "android.permission.READ_MEDIA_IMAGES"
            } else {
                "android.permission.READ_EXTERNAL_STORAGE"
            }

            // Verifica se il permesso è stato concesso
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                // Usa ContentResolver per accedere in modo sicuro all'immagine
                val uri = Uri.parse(path)
                try {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    holder.imageV.setImageBitmap(bitmap)
                    inputStream?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                    holder.imageV.setImageResource(R.drawable.logo)
                    Log.d("non mette la foto scelta ma il logo", "non mette la foto scelta ma il logo")
                }
            } else {
                // Gestisci il caso in cui il permesso non è stato concesso
                holder.imageV.setImageResource(R.drawable.logo)
                Log.d("PERMESSO NEGATO", "Impossibile impostare l'immagine: permesso non concesso")
            }
        } else {
            // Imposta l'immagine di default
            holder.imageV.setImageResource(R.drawable.logo)
        }
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    fun filterNome(query: String) {
        if (query.isEmpty()) {
            filteredItemList = ricettaModel // Create a copy to avoid modifying the original
        } else {
            val temp = ricettaModel.filter { it.nome.contains(query, ignoreCase = true) }
            filteredItemList = temp.toCollection(ArrayList())
        }
        notifyDataSetChanged()
    }

    fun filterPortata(query: String) {
        if (query.isEmpty()) {
            filteredItemList = ricettaModel // Create a copy to avoid modifying the original
        } else {
            val temp = ricettaModel.filter { it.portata.equals(query) }
            filteredItemList = temp.toCollection(ArrayList())
        }
        notifyDataSetChanged()
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

            itemView.setOnClickListener{
                if(recyclerViewInterface != null){
                    var pos : Int = adapterPosition

                    if(pos != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(pos)
                    }
                }
            }
        }
    }
}