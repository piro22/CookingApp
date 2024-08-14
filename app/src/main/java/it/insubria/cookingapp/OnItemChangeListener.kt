package it.insubria.cookingapp

interface OnItemChangeListener {
    fun onQuantityChanged(position: Int, newQuantity: Int)
    fun onUnitChanged(position: Int, newUnit: String)
}