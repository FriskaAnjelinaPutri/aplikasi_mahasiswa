package com.friska.aplikasimahasiswa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friska.aplikasimahasiswa.adapter.MahasiswaAdapter
import com.friska.aplikasimahasiswa.helper.DatabaseHelper
import com.friska.aplikasimahasiswa.model.Mahasiswa
import com.friska.aplikasimahasiswa.screen.AddEditActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MahasiswaAdapter

    // Register for activity result to handle returning data from AddEditActivity
    private val resultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Refresh data when coming back from AddEditActivity
                loadData()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Muat data dari database
        loadData()

        // Tombol untuk tambah data
        val addButton: FloatingActionButton = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            // Pindah ke aktivitas tambah/edit data
            val intent = Intent(this, AddEditActivity::class.java)
            resultLauncher.launch(intent)  // Menggunakan launcher untuk menangani hasil activity
        }
    }

    // Fungsi untuk memuat data mahasiswa dari database
    private fun loadData() {
        // Ambil data dari database dan urutkan berdasarkan ID
        val mahasiswaList = dbHelper.getAllMahasiswa().sortedBy { it.id }

        if (mahasiswaList.isNotEmpty()) {
            if (!::adapter.isInitialized) {
                // Jika adapter belum diinisialisasi, buat adapter baru
                adapter = MahasiswaAdapter(mahasiswaList, dbHelper) { mahasiswa, action ->
                    when (action) {
                        "edit" -> editData(mahasiswa)
                        "delete" -> deleteData(mahasiswa)
                    }
                }
                recyclerView.adapter = adapter
            } else {
                // Jika adapter sudah diinisialisasi, perbarui data
                adapter.refreshData(mahasiswaList)
            }
        } else {
            // Jika data kosong, tampilkan pesan
            Toast.makeText(this, "Tidak ada data.", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk membuka aktivitas AddEditActivity dalam mode edit
    private fun editData(mahasiswa: Mahasiswa) {
        // Buka activity AddEditActivity untuk mengedit data
        val intent = Intent(this, AddEditActivity::class.java)
        intent.putExtra("id", mahasiswa.id)
        intent.putExtra("nama", mahasiswa.nama)
        intent.putExtra("nim", mahasiswa.nim)
        intent.putExtra("jurusan", mahasiswa.jurusan)
        resultLauncher.launch(intent)  // Meluncurkan AddEditActivity dengan result launcher
    }

    // Fungsi untuk menghapus data mahasiswa
    private fun deleteData(mahasiswa: Mahasiswa) {
        // Hapus data berdasarkan ID
        val rowsDeleted = dbHelper.deleteMahasiswa(mahasiswa.id)
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Data berhasil dihapus.", Toast.LENGTH_SHORT).show()
            loadData() // Refresh data setelah penghapusan
        } else {
            Toast.makeText(this, "Gagal menghapus data.", Toast.LENGTH_SHORT).show()
        }
    }
}
