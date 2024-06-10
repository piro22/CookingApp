package it.insubria.cookingapp

class RicetteModel(nome: String,
                    pathFoto: String,
                    preparazione: String,
                    porzioni: Int,
                    tempo: Int,
                    difficolta: String,
                    tipologia: String,
                    portata: String,
                    dieta: String,
                    etnicita: String,
                    preferito: Int) {
    val nome: String
    val pathFoto: String
    val preparazione: String
    val porzioni: Int
    val tempo: Int
    val difficolta: String
    val tipologia: String
    val portata: String
    val dieta: String
    val etnicita: String
    val preferito: Int

    init {
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