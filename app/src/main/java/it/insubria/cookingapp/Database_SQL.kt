package it.insubria.cookingapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Database_SQL (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "myapp.db"
            private const val DATABASE_VERSION = 1
        }

        //onCreate per inizializzare le tabelle
        override fun onCreate(db: SQLiteDatabase) {
            createTables(db)
            populateTables(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.d("Database Upgrade", "onUpgrade: Database version changed from $oldVersion to $newVersion")
            dropTables(db)
            onCreate(db)
        }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE portate (
            portata TEXT PRIMARY KEY
        )
    """)

        db.execSQL("""
        CREATE TABLE dieta (
            dieta TEXT PRIMARY KEY
        )
    """)

        db.execSQL("""
        CREATE TABLE difficolta (
            difficolta TEXT PRIMARY KEY
        )
    """)

        db.execSQL("""
        CREATE TABLE tipologia (
            tipologia TEXT PRIMARY KEY
        )
    """)

        db.execSQL("""
        CREATE TABLE etnicita (
            etnicita TEXT PRIMARY KEY
        )
    """)

        db.execSQL("""
        CREATE TABLE ingrediente (
            nome TEXT PRIMARY KEY,
            tipologia TEXT
        )
    """)

        db.execSQL("""
        CREATE TABLE unita_di_misura (
            unita TEXT PRIMARY KEY
        )
    """)


        db.execSQL("""
        CREATE TABLE ricetta (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT,
            porzioni INTEGER,
            tempo_di_preparazione INTEGER,
            difficolta TEXT,
            tipologia TEXT,
            portata TEXT,
            dieta TEXT,
            pathFoto TEXT,
            preparazione TEXT,
            preferito INTEGER DEFAULT 0,
            FOREIGN KEY (difficolta) REFERENCES difficolta(difficolta),
            FOREIGN KEY (tipologia) REFERENCES tipologia(tipologia),
            FOREIGN KEY (portata) REFERENCES portate(portata),
            FOREIGN KEY (dieta) REFERENCES dieta(dieta)
        )
    """)


        db.execSQL("""
        CREATE TABLE ingredienti_ricetta (
            id_ricetta INTEGER PRIMARY KEY AUTOINCREMENT,
            ingrediente TEXT,
            quantita INTEGER,
            unita_di_misura TEXT,
            FOREIGN KEY (ricetta) REFERENCES ricetta(id),
            FOREIGN KEY (ingrediente) REFERENCES ingrediente(nome),
            FOREIGN KEY (unita_di_misura) REFERENCES unita_di_misura(unita)
        )
    """)

  /*
        db.execSQL("""
        CREATE TABLE listaSpesa (
            FOREIGN KEY (ingrediente) REFERENCES ingrediente(nome)
        )
    """)*/
    }


    private fun populateTables(db: SQLiteDatabase) {
            db.execSQL("INSERT INTO portate (portata) VALUES ('antipasto')")
            db.execSQL("INSERT INTO portate (portata) VALUES ('primo')")
            db.execSQL("INSERT INTO portate (portata) VALUES ('secondo')")
            db.execSQL("INSERT INTO portate (portata) VALUES ('dolce')")

            // Example of populating other tables if needed
            db.execSQL("INSERT INTO ricette (nome, descrizione, tempoPreparazione, difficolta, categoria) VALUES ('Pasta al Pomodoro', 'Descrizione della ricetta', 20, 'Facile', 'Primo')")
            db.execSQL("INSERT INTO difficolta (difficolta) VALUES ('*')" )
        db.execSQL("INSERT INTO difficolta (difficolta) VALUES ('**')" )
        db.execSQL("INSERT INTO difficolta (difficolta) VALUES ('***')" )
        }

        private fun dropTables(db: SQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS portate")
            db.execSQL("DROP TABLE IF EXISTS utenti")
            db.execSQL("DROP TABLE IF EXISTS ricette")
        }
}