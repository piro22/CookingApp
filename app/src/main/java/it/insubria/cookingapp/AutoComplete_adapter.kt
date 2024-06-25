import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import it.insubria.cookingapp.R

class AutoComplete_adapter(
    context: Context,
    private val resource: Int,
    private val items: List<String>,
    private val database: SQLiteDatabase,
    private val tableName: String,
    private val columnName: String
) : ArrayAdapter<String>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val item = getItem(position)
        val textView = view.findViewById<TextView>(R.id.item_text)
        textView.text = item

        val deleteButton = view.findViewById<ImageButton>(R.id.delete_button)
        deleteButton.setOnClickListener {
            val itemToRemove = getItem(position)
            if (itemToRemove != null) {
                remove(itemToRemove)
                notifyDataSetChanged()
                deleteItemFromDatabase(itemToRemove)
            }
        }

        return view
    }

    private fun deleteItemFromDatabase(item: String) {
        database.delete(tableName, "$columnName = ?", arrayOf(item))
    }
}
