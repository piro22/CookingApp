package it.insubria.cookingapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Database_SQL(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "myapp.db"
        private const val DATABASE_VERSION = 2
    }

    //onCreate per inizializzare le tabelle
    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
        populateTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(
            "Database Upgrade",
            "onUpgrade: Database version changed from $oldVersion to $newVersion"
        )
        dropTables(db)
        onCreate(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL(
            """
        CREATE TABLE portate (
            portata TEXT PRIMARY KEY
        )
    """
        )

        db.execSQL(
            """
        CREATE TABLE dieta (
            dieta TEXT PRIMARY KEY
        )
    """
        )

        db.execSQL(
            """
        CREATE TABLE difficolta (
            difficolta TEXT PRIMARY KEY
        )
    """
        )

        db.execSQL(
            """
        CREATE TABLE tipologia (
            tipologia TEXT PRIMARY KEY
        )
    """
        )

        db.execSQL(
            """
        CREATE TABLE etnicita (
            etnicita TEXT PRIMARY KEY
        )
    """
        )

        db.execSQL(
            """
        CREATE TABLE ingrediente (
            nome TEXT PRIMARY KEY
        )
    """
        )

        db.execSQL(
            """
        CREATE TABLE unita_di_misura (
            unita TEXT PRIMARY KEY
        )
    """
        )


        db.execSQL(
            """
        CREATE TABLE ricetta (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT NOT NULL,
            porzioni INTEGER DEFAULT 4,
            tempo_di_preparazione INTEGER DEFAULT -1,
            difficolta TEXT,
            tipologia TEXT,
            portata TEXT,
            dieta TEXT,
            etnicita TEXT,
            pathFoto TEXT DEFAULT 'default',
            preparazione TEXT NOT NULL,
            preferito INTEGER DEFAULT 0,
            FOREIGN KEY (difficolta) REFERENCES difficolta(difficolta),
            FOREIGN KEY (tipologia) REFERENCES tipologia(tipologia),
            FOREIGN KEY (portata) REFERENCES portate(portata),
            FOREIGN KEY (dieta) REFERENCES dieta(dieta),
            FOREIGN KEY (etnicita) REFERENCES etnicita(etnicita)
        )
    """
        )


        db.execSQL(
            """
        CREATE TABLE ingredienti_ricetta (
            id_ricetta INTEGER,
            ingrediente TEXT,
            quantita INTEGER DEFAULT -1,
            unita_di_misura TEXT,
            PRIMARY KEY (id_ricetta, ingrediente),
            FOREIGN KEY (id_ricetta) REFERENCES ricetta(id),
            FOREIGN KEY (ingrediente) REFERENCES ingrediente(nome),
            FOREIGN KEY (unita_di_misura) REFERENCES unita_di_misura(unita)
        )
    """
        )


        db.execSQL(
            """
              CREATE TABLE listaSpesa (
                    id_ricetta INTEGER PRIMARY KEY AUTOINCREMENT,
                    ingrediente text NOT NULL,
                    quantita INTEGER DEFAULT -1
              )
          """
        )
    }


    private fun populateTables(db: SQLiteDatabase) {
        //POPOLO TABELLE UTILI PER RICETTA
        db.execSQL("INSERT INTO portate VALUES ('- - -'), ('antipasto'), ('primo'), ('secondo'), ('dolce')")
        db.execSQL("INSERT INTO dieta VALUES ('- - -'), ('onnivora'), ('vegana'), ('carnivora'), ('vegetariana')")
        db.execSQL("INSERT INTO tipologia VALUES ('- - -'), ('pasta'), ('carne'), ('pesce'), ('tortino'), ('generico'), ('dolce')")
        db.execSQL("INSERT INTO etnicita VALUES ('- - -'), ('italiano'), ('cinese'), ('francese'), ('indiano')")
        db.execSQL("INSERT INTO difficolta VALUES ('*'), ('**'), ('***'), ('****'), ('*****')")

        //POPOLO LISTA DELLA SPESA
        db.execSQL("INSERT INTO listaSpesa (ingrediente, quantita) VALUES ('Farfalle', 2), ('Pasta Pizza', 2), ('Ravioli', 2), ('Banane', 5), ('Insalata', 1)")

        //TABELLE PER INGREDIENTI
        db.execSQL("INSERT INTO ingrediente VALUES ('Farina'), ('Cioccolato'), ('uova'), ('acqua'), ('latte')")
        db.execSQL("INSERT INTO unita_di_misura VALUES ('- - -'), ('L'), ('dL'), ('cL'), ('mL'), ('Kg'), ('h'), ('g'), ('tsp'), ('tbsp'), ('cup'), ('pt'), ('qt'), ('gal'), ('Oz'), ('lb'), ('qb')")

        //INSERISCO TRE RICETTE
        db.execSQL("INSERT INTO ricetta(nome , porzioni ,tempo_di_preparazione, difficolta, tipologia, portata, dieta, etnicita, pathFoto, preparazione, preferito) VALUES ('pasta al pesto', 5, 20, '*', 'pasta', 'primo', 'onnivora', 'italiano', 'content://media/external/images/media/1000000028', 'cuocio la pasta e dopo 10/12 minuti la scolo, la metto in pentola e ci metto il pesto', 1)")
        db.execSQL("INSERT INTO ricetta(nome , porzioni ,tempo_di_preparazione, difficolta, tipologia, portata, dieta, etnicita, pathFoto, preparazione, preferito) VALUES ('tortino al cioccolato', 6, 100, '***', 'tortino', 'dolce', 'onnivora', 'francese', 'default', 'non so come si faccia bro, non sono un pasticcere', 0)")
        db.execSQL("INSERT INTO ricetta(nome , porzioni ,tempo_di_preparazione, difficolta, tipologia, portata, dieta, etnicita, pathFoto, preparazione, preferito) VALUES ('tiramisù', 5, 80, '**', 'dolce', 'dolce', 'onnivora', 'italiano', 'content://media/external/images/media/1000000028', 'Per preparare il tiramisù preparate il caffé con la moka per ottenerne 300 g, poi zuccherate a piacere (noi abbiamo messo un cucchiaino) e lasciatelo raffreddare in una ciotolina bassa e ampia. Separate le uova dividendo gli albumi dai tuorli 1, ricordando che per montare bene gli albumi non dovranno presentare alcuna traccia di tuorlo. Montate i tuorli con le fruste elettriche, versando solo metà dose di zucchero 2. Non appena il composto sarà diventato chiaro e spumoso, e con le fruste ancora in funzione, potrete aggiungere il mascarpone, poco alla volta 3. ', 1)")

        //ASSOCIO GLI INGREDIENTI ALLE RICETTE UNA PER VOLTA
        db.execSQL("INSERT INTO ingredienti_ricetta (id_ricetta, ingrediente, quantita, unita_di_misura) VALUES (1, 'farina', 200, 'g'), (1, 'uova', 2, '- - -'), (1, 'latte', 350, 'ml')")
        db.execSQL("INSERT INTO ingredienti_ricetta (id_ricetta, ingrediente, quantita, unita_di_misura) VALUES (2, 'farina', 1, 'Kg'), (2, 'acqua', 2, 'qb'), (2, 'latte', 35, 'cl'), (2, 'uova', 30, '- - -')")
        db.execSQL("INSERT INTO ingredienti_ricetta (id_ricetta, ingrediente, quantita, unita_di_misura) VALUES (3, 'farina', 1, 'Kg'), (3, 'acqua', 2, 'qb')")
    }

    private fun dropTables(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS ingredienti_ricetta")

        db.execSQL("DROP TABLE IF EXISTS ricetta")

        db.execSQL("DROP TABLE IF EXISTS listaSpesa")
        db.execSQL("DROP TABLE IF EXISTS portate")
        db.execSQL("DROP TABLE IF EXISTS dieta")
        db.execSQL("DROP TABLE IF EXISTS tipologia")
        db.execSQL("DROP TABLE IF EXISTS etnicita")
        db.execSQL("DROP TABLE IF EXISTS ingrediente")
        db.execSQL("DROP TABLE IF EXISTS untita_di_misura")
    }
}