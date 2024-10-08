package it.insubria.cookingapp

class RicetteModel(id: Int,
    nome: String,
                    pathFoto: ByteArray,
                    preparazione: String,
                    porzioni: Int,
                    tempo: Int,
                    difficolta: String,
                    tipologia: String,
                    portata: String,
                    dieta: String,
                    etnicita: String,
                    preferito: Int) {
    val id: Int
    val nome: String
    val pathFoto: ByteArray
    val preparazione: String
    val porzioni: Int
    val tempo: Int
    val difficolta: String
    val tipologia: String
    val portata: String
    val dieta: String
    val etnicita: String
    var preferito: Int

    init {
        this.id= id
        this.nome = nome
        this.pathFoto = pathFoto
        this.preparazione = preparazione
        this.porzioni = porzioni
        this.tempo = tempo
        this.difficolta = difficolta
        this.tipologia = tipologia
        this.portata = portata
        this.dieta = dieta
        this.etnicita = etnicita
        this.preferito = preferito
    }
}