package com.friska.aplikasimahasiswa.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.friska.aplikasimahasiswa.R
import com.friska.aplikasimahasiswa.helper.DatabaseHelper
import com.friska.aplikasimahasiswa.model.Mahasiswa

class MahasiswaAdapter(
    private var mahasiswaList: List<Mahasiswa>,
    private val dbHelper: DatabaseHelper,
    private val onActionClick: (Mahasiswa, String) -> Unit
) : RecyclerView.Adapter<MahasiswaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvID: TextView = view.findViewById(R.id.tvID)
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvNIM: TextView = view.findViewById(R.id.tvNIM)
        val tvJurusan: TextView = view.findViewById(R.id.tvJurusan)

        val btnEdit: ImageView = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mahasiswa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mahasiswa = mahasiswaList[position]
        holder.tvID.text = mahasiswa.id.toString()
        holder.tvNama.text = mahasiswa.nama
        holder.tvNIM.text = mahasiswa.nim
        holder.tvJurusan.text = mahasiswa.jurusan

        //tombol Edit
        holder.btnEdit.setOnClickListener {
            onActionClick(mahasiswa, "edit")
        }

        //tombol hapus
        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context).apply {
                setTitle("Konfirmasi")
                setMessage("Apakah Anda yakin ingin menghapus data ini?")
                setIcon(R.drawable.baseline_delete_24)

                setPositiveButton("Ya") { dialogInterface, _ ->
                    val rowsDeleted = dbHelper.deleteMahasiswa(mahasiswa.id)

                    if (rowsDeleted > 0) {
                        refreshData(dbHelper.getAllMahasiswa())

                        Toast.makeText(holder.itemView.context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                    }
                    dialogInterface.dismiss()
                }

                setNeutralButton("Tidak") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
            }.show()
        }
    }

    override fun getItemCount(): Int {
        return mahasiswaList.size
    }

    fun refreshData(newData: List<Mahasiswa>) {
        mahasiswaList = newData
        notifyDataSetChanged()
    }
}
