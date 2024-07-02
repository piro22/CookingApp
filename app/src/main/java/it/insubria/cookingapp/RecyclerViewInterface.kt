package it.insubria.cookingapp

interface RecyclerViewInterface {
    fun onItemClick(position : Int)
    fun onItemLongClick(position: Int): Boolean
}