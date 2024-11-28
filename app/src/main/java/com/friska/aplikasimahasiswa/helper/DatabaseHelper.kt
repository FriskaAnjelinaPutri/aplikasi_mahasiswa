package com.friska.aplikasimahasiswa.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.friska.aplikasimahasiswa.model.Mahasiswa

class DatabaseHelper(context: Context) {
    private val DatabaseHelper: DBHelper = DBHelper(context)
    private val db: SQLiteDatabase = DatabaseHelper.writableDatabase

    // tambah data mahasiswa
    fun addMahasiswa(nama: String, nim: String, jurusan: String): Long {
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_NAME, nama)
            put(DBHelper.COLUMN_NIM, nim)
            put(DBHelper.COLUMN_JURUSAN, jurusan)
        }
        val id = db.insert(DBHelper.TABLE_NAME, null, values)
        resetID()
        return id
    }

    fun getAllMahasiswa(): List<Mahasiswa> {
        val mahasiswaList = mutableListOf<Mahasiswa>()
        val cursor: Cursor = db.query(
            DBHelper.TABLE_NAME,
            arrayOf(DBHelper.COLUMN_ID, DBHelper.COLUMN_NAME, DBHelper.COLUMN_NIM, DBHelper.COLUMN_JURUSAN),
            null, null, null, null, "${DBHelper.COLUMN_ID} ASC" // Menambahkan urutan ID ASC
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID))
                val nama = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME))
                val nim = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NIM))
                val jurusan = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_JURUSAN))
                mahasiswaList.add(Mahasiswa(id, nama, nim, jurusan))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return mahasiswaList
    }

    //memperbarui data mahasiswa
    fun updateMahasiswa(id: Int, nama: String, nim: String, jurusan: String): Int {
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_NAME, nama)
            put(DBHelper.COLUMN_NIM, nim)
            put(DBHelper.COLUMN_JURUSAN, jurusan)
        }
        return db.update(DBHelper.TABLE_NAME, values, "${DBHelper.COLUMN_ID}=?", arrayOf(id.toString()))
    }

    // menghapus data mahasiswa
    fun deleteMahasiswa(id: Int): Int {
        val rowsDeleted = db.delete(DBHelper.TABLE_NAME, "${DBHelper.COLUMN_ID}=?", arrayOf(id.toString()))
        resetID()

        return rowsDeleted
    }
    private fun resetID() {
        val db = DatabaseHelper.writableDatabase
        db.execSQL("DELETE FROM sqlite_sequence WHERE name = '${DBHelper.TABLE_NAME}'")
    }

    // Kelas untuk membuat dan mengelola database
    private class DBHelper(context: Context) : android.database.sqlite.SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase?) {
            // Membuat tabel mahasiswa
            val CREATE_TABLE = """
                CREATE TABLE ${TABLE_NAME} (
                    ${COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                    ${COLUMN_NAME} TEXT,
                    ${COLUMN_NIM} TEXT,
                    ${COLUMN_JURUSAN} TEXT
                )
            """
            db?.execSQL(CREATE_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }

        companion object {
            const val DATABASE_NAME = "mahasiswa.db"
            const val DATABASE_VERSION = 1
            const val TABLE_NAME = "mahasiswa"
            const val COLUMN_ID = "id"
            const val COLUMN_NAME = "nama"
            const val COLUMN_NIM = "nim"
            const val COLUMN_JURUSAN = "jurusan"
        }
    }
}
