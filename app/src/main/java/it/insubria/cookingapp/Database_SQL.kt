package it.insubria.cookingapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Database_SQL(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dbRicetteV3.db"
        private const val DATABASE_VERSION = 3
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
            pathFoto BLOB,
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
        db.execSQL("INSERT INTO ingrediente VALUES ('farina'), ('cioccolato'), ('uova'), ('acqua'), ('latte'), ('linguine'), ('sale grosso'), ('pinoli'), ('aglio'), ('olio extravergine di oliva'), ('pecorino'), ('parmigiano reggiano'), ('basilico')")
        db.execSQL("INSERT INTO unita_di_misura VALUES ('- - -'), ('L'), ('dL'), ('cL'), ('mL'), ('Kg'), ('h'), ('g'), ('tsp'), ('tbsp'), ('cup'), ('pt'), ('qt'), ('gal'), ('Oz'), ('lb'), ('qb')")

        //INSERISCO TRE RICETTE
        val img = byteArrayOf()
        val sql = """
            INSERT INTO ricetta
            (nome, porzioni, tempo_di_preparazione, difficolta, tipologia, portata, dieta, etnicita, pathFoto, preparazione, preferito)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val statement = db.compileStatement(sql)

        // Bind dei valori
        statement.bindString(1, "Bavette al pesto")
        statement.bindLong(2, 4)
        statement.bindLong(3, 20)
        statement.bindString(4, "*")
        statement.bindString(5, "pasta")
        statement.bindString(6, "primo")
        statement.bindString(7, "onnivora")
        statement.bindString(8, "italiano")
        statement.bindBlob(9, img) // Inserisci il BLOB
        statement.bindString(10, "[[Passo]]Per preparare le bavette al pesto, incominciate con il pesto alla genovese; per preparare il pesto, ricordatevi che, una volta sfogliate, le foglie di basilico non vanno lavate sotto il getto d''acqua, ma pulite con un panno morbido oppure possono essere messe in una ciotola d’acqua fredda e sciacquate delicatamente.[[Passo]]Ponete quindi l’aglio sbucciato nel mortaio insieme a qualche grano di sale grosso. Cominciate a pestare e, quando l’aglio sarà ridotto in crema aggiungete le foglie di basilico insieme ad un pizzico di sale grosso.[[Passo]]A questo punto unite i pinoli e ricominciate a pestare per ridurre in crema.[[Passo]]Mettete sul fuoco una pentola con abbondante acqua salata e, quando l''acqua avrà sfiorato il bollore, fate cuocere la pasta.[[Passo]]Ora mettete il pesto in una padella ampia (a fuoco spento) e stemperatelo con un po'' di acqua di cottura della pasta.[[Passo]]Mescolate con una spatola e, non appena la pasta sarà pronta, aggiungetela direttamente al pesto")
        statement.bindLong(11, 1)

        // Esegui l'inserimento
        statement.executeInsert()

        val statement2 = db.compileStatement(sql)

        // Bind dei valori
        statement2.bindString(1, "Tortino al cioccolato")
        statement2.bindLong(2, 6)
        statement2.bindLong(3, 100)
        statement2.bindString(4, "***")
        statement2.bindString(5, "tortino")
        statement2.bindString(6, "dolce")
        statement2.bindString(7, "onnivora")
        statement2.bindString(8, "francese")
        statement2.bindBlob(9, img) // Inserisci il BLOB
        statement2.bindString(10, "[[Passo]]Per preparare le bavette al pesto, incominciate con il pesto alla genovese; per preparare il pesto, ricordatevi che, una volta sfogliate, le foglie di basilico non vanno lavate sotto il getto d''acqua, ma pulite con un panno morbido oppure possono essere messe in una ciotola d’acqua fredda e sciacquate delicatamente.")
        statement2.bindLong(11, 0)

        // Esegui l'inserimento
        statement2.executeInsert()

        //db.execSQL("INSERT INTO ricetta(nome , porzioni ,tempo_di_preparazione, difficolta, tipologia, portata, dieta, etnicita, pathFoto, preparazione, preferito) VALUES ('Tiramisù', 5, 80, '**', 'dolce', 'dolce', 'onnivora', 'italiano', $img, 'Per preparare il tiramisù preparate il caffé con la moka per ottenerne 300 g, poi zuccherate a piacere (noi abbiamo messo un cucchiaino) e lasciatelo raffreddare in una ciotolina bassa e ampia. Separate le uova dividendo gli albumi dai tuorli 1, ricordando che per montare bene gli albumi non dovranno presentare alcuna traccia di tuorlo. Montate i tuorli con le fruste elettriche, versando solo metà dose di zucchero 2. Non appena il composto sarà diventato chiaro e spumoso, e con le fruste ancora in funzione, potrete aggiungere il mascarpone, poco alla volta 3. ', 1)")

        //ASSOCIO GLI INGREDIENTI ALLE RICETTE UNA PER VOLTA
        db.execSQL("INSERT INTO ingredienti_ricetta (id_ricetta, ingrediente, quantita, unita_di_misura) VALUES (1, 'farina', 200, 'g'), (1, 'uova', 2, '- - -'), (1, 'latte', 350, 'ml')")
        db.execSQL("INSERT INTO ingredienti_ricetta (id_ricetta, ingrediente, quantita, unita_di_misura) VALUES (2, 'farina', 1, 'Kg'), (2, 'acqua', 2, 'qb'), (2, 'latte', 35, 'cl'), (2, 'uova', 30, '- - -')")
        //db.execSQL("INSERT INTO ingredienti_ricetta (id_ricetta, ingrediente, quantita, unita_di_misura) VALUES (3, 'farina', 1, 'Kg'), (3, 'acqua', 2, 'qb')")
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