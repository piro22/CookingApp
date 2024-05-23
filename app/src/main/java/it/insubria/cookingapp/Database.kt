package it.insubria.cookingapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

//Database sottoclasse di sqLiteOpenHelper. e quindi bisogna per forza implementare onCreate e onUpgrade
//nCreate per inizializzare le tabelle
//onUpgrade viene chiamato quando il database deve essere aggiornato dalla sua versione precedente alla versione corrente.

    class Database(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "myapp.db"
            private const val DATABASE_VERSION = 1
        }

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE portate(portata TEXT PRIMARY KEY)")
            popolaDB(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.d("Database Upgrade", "onUpgrade: Database version changed from $oldVersion to $newVersion")
            // Handle database upgrade if needed
        }

        private fun popolaDB(db: SQLiteDatabase) {
            // Esegui un'istruzione SQL per inserire i dati nella tabella
            db.execSQL("INSERT INTO portate (portata) VALUES ('antipasto')")
            db.execSQL("INSERT INTO portate (portata) VALUES ('primo')")
            db.execSQL("INSERT INTO portate (portata) VALUES ('secondo')")
            db.execSQL("INSERT INTO portate (portata) VALUES ('dolce')")
            // No need to close the db here
        }
    }




