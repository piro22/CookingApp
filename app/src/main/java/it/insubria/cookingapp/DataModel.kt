package it.insubria.cookingapp

import androidx.lifecycle.ViewModel

class DataModel() : ViewModel() {
    var ricetta: RicetteModel? = null
    var dbHelper: Database_SQL? = null

    //per unità di misura da utilizzare
    var volume: String? = "default"
    var peso: String? = "default"
}