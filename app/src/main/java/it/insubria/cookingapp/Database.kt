package it.insubria.cookingapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

//Database sottoclasse di sqLiteOpenHelper. e quindi bisogna per forza implementare onCreate e onUpgrade

//onUpgrade viene chiamato quando il database deve essere aggiornato dalla sua versione precedente alla versione corrente.

class Database(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

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
            CREATE TABLE utenti (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                age INTEGER,
                path TEXT
            )
        """)

                db.execSQL("""
            CREATE TABLE ricette (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                descrizione TEXT,
                tempoPreparazione INTEGER,
                difficolta TEXT,
                categoria TEXT
            )
        """)
            }

            private fun populateTables(db: SQLiteDatabase) {
                db.execSQL("INSERT INTO portate (portata) VALUES ('antipasto')")
                db.execSQL("INSERT INTO portate (portata) VALUES ('primo')")
                db.execSQL("INSERT INTO portate (portata) VALUES ('secondo')")
                db.execSQL("INSERT INTO portate (portata) VALUES ('dolce')")

                // Example of populating other tables if needed
                db.execSQL("INSERT INTO utenti (name, age, path) VALUES ('Mario Rossi', 30, '/path/to/resource')")
                db.execSQL("INSERT INTO ricette (nome, descrizione, tempoPreparazione, difficolta, categoria) VALUES ('Pasta al Pomodoro', 'Descrizione della ricetta', 20, 'Facile', 'Primo')")
            }

            private fun dropTables(db: SQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS portate")
                db.execSQL("DROP TABLE IF EXISTS utenti")
                db.execSQL("DROP TABLE IF EXISTS ricette")
            }
        }






