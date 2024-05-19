package it.insubria.cookingapp

class RicetteModel(ID: Int,
                    nome: String,
                    pathFoto: String,
                    preparazione: String,
                    porzioni: Int,
                    tempo: Int,
                    difficolta: String,
                    tipologia: String,
                    portata: String,
                    dieta: String,
                    preferito: Boolean) {
    val ID: Int
    val nome: String
    val pathFoto: String
    val preparazione: String
    val porzioni: Int
    val tempo: Int
    val difficolta: String
    val tipologia: String
    val portata: String
    val dieta: String
    val preferito: Boolean

    init {
        this.ID = ID
        this.nome = nome
        this.pathFoto = pathFoto
        this.preparazione = preparazione
        this.porzioni = porzioni
        this.tempo = tempo
        this.difficolta = difficolta
        this.tipologia = tipologia
        this.portata = portata
        this.dieta = dieta
        this.preferito = preferito
    }
}