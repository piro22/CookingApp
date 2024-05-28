package it.insubria.cookingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var prodotti : ArrayList<String>
var DOT = "-->  "

/**
 * A simple [Fragment] subclass.
 * Use the [ShoppingListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShoppingListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val ret = inflater.inflate(R.layout.fragment_shopping_list, container, false)
        val btnLista: Button = ret.findViewById(R.id.bottoneAddFood)
        val lista : ListView = ret.findViewById(R.id.listViewShopping)
        val txtFood : EditText = ret.findViewById(R.id.txtAddFood)


        //INIZIALIZZO LA LISTA CHE METTERO' NELLA ListView
        prodotti = ArrayList<String>()

        prodotti.add("$DOT cacao")
        prodotti.add("$DOT panna")
        prodotti.add("$DOT farina")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT bastoncini findus però quelli alla sogliola vado a capo")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT acqua")
        prodotti.add("$DOT fine")
        prodotti.add("$DOT fine")


        var adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, prodotti)
        lista.adapter = adapter


        val button = ret.findViewById<Button>(R.id.buttonClearList)

        // Create a confirmation dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirm Clear")
            .setMessage("Are you sure you want to clear this list?")
            .setPositiveButton("Yes") { _, _ ->
                prodotti.clear()
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Set an OnClickListener on the button
        button.setOnClickListener {
            dialog.show()
        }


        //AGGIUNGO ITEM ALLA LISTA
        btnLista.setOnClickListener {
            val food = txtFood.text.toString()
            if (food.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Testo vuoto o nullo", Toast.LENGTH_SHORT).show()
            } else {
//                dbw = databaseHelper.writableDatabase
//                dbw.execSQL("INSERT INTO lista VALUES(\"$food\")")
//                dbw.close()

                txtFood.setText("")

                prodotti.add("$DOT $food")

                adapter.notifyDataSetChanged()

                Toast.makeText(requireContext(), "$food aggiunto", Toast.LENGTH_SHORT).show()
            }
        }

        return ret
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShoppingListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShoppingListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}