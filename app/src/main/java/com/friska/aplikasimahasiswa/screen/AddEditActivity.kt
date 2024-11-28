package com.friska.aplikasimahasiswa.screen

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.friska.aplikasimahasiswa.R
import com.friska.aplikasimahasiswa.helper.DatabaseHelper

class AddEditActivity : AppCompatActivity() {

    private lateinit var DatabaseHelper: DatabaseHelper
    private lateinit var etNama: EditText
    private lateinit var etNIM: EditText
    private lateinit var etJurusan: EditText
    private lateinit var btnSave: Button

    private var isEdit = false
    private var mahasiswaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)  // Pastikan layout yang benar

        DatabaseHelper = DatabaseHelper(this)

        // Inisialisasi View
        etNama = findViewById(R.id.etNama)
        etNIM = findViewById(R.id.etNIM)
        etJurusan = findViewById(R.id.etJurusan)
        btnSave = findViewById(R.id.btnSave)

        // Periksa apakah ini edit atau tambah
        if (intent.hasExtra("id")) {
            isEdit = true
            mahasiswaId = intent.getIntExtra("id", -1)
            etNama.setText(intent.getStringExtra("nama"))
            etNIM.setText(intent.getStringExtra("nim"))
            etJurusan.setText(intent.getStringExtra("jurusan"))
        }

        // Klik tombol simpan
        btnSave.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val nim = etNIM.text.toString().trim()
            val jurusan = etJurusan.text.toString().trim()

            // Validasi input
            if (nama.isEmpty() || nim.isEmpty() || jurusan.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
            } else {
                // Jika edit, update data
                if (isEdit) {
                    val updated = DatabaseHelper.updateMahasiswa(mahasiswaId, nama, nim, jurusan)
                    if (updated > 0) {
                        // Kirim hasil kembali ke MainActivity untuk menyegarkan data
                        setResult(RESULT_OK)
                        Toast.makeText(this, "Data berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Gagal memperbarui data.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Jika tambah, simpan data
                    val inserted = DatabaseHelper.addMahasiswa(nama, nim, jurusan)
                    if (inserted > 0) {
                        // Kirim hasil kembali ke MainActivity untuk menyegarkan data
                        setResult(RESULT_OK)
                        Toast.makeText(this, "Data berhasil ditambahkan.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Gagal menambahkan data.", Toast.LENGTH_SHORT).show()
                    }
                }
                // Setelah operasi selesai, kembali ke MainActivity
                finish()
            }
        }
    }
}
