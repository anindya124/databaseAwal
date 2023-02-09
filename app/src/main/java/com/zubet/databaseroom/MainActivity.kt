package com.zubet.databaseroom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zubet.databaseroom.room.Constant
import com.zubet.databaseroom.room.Note
import com.zubet.databaseroom.room.NoteDB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val db by lazy { NoteDB(this) }
    lateinit var noteAdapter: NoteAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pihdah_halaman()
        setupRecyclerView()
        loadNote()
    }

    override fun onStart() {
        super.onStart()
        loadNote()
    }
    fun loadNote(){
        CoroutineScope(Dispatchers.IO).launch{
            val note = db.noteDao().getNotes()
            Log.d("MainActivity","db Response:$note")
            withContext(Dispatchers.Main){
                noteAdapter.setData(note)
            }
        }
    }
    fun pihdah_halaman() {
        button_create.setOnClickListener {intentEdit(Constant.TYPE_CREATE,0)  }
    }
      fun intentEdit(note_Id : Int, intentType : Int){
          startActivity(
              Intent(applicationContext, EditActivity::class.java)
                  .putExtra("intent_id", note_Id)
                  .putExtra("intent_type", intentType)
          )

      }
    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(arrayListOf(), object: NoteAdapter.OnAdapterListener{
            override fun onClick(note: Note) {
           //read detail note
                intentEdit(note.id, Constant.TYPE_READ)
            }

            override fun onUpdate(note: Note) {
                intentEdit(note.id,Constant.TYPE_UPDATE)
            }

            override fun onDelete(note: Note) {
                deleteDialog(note)
            }
        })
        list_note.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = noteAdapter
        }
    }
        private fun deleteDialog(note: Note){
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.apply {
                setTitle("Konfirmasi")
                setMessage("Yakin hapus ${note.title}?")
                setNegativeButton("Batal"){ dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                setPositiveButton("Hapus"){ dialogInterface, i ->
                    dialogInterface.dismiss()
                    CoroutineScope(Dispatchers.IO).launch {
                        db.noteDao().deleteNote(note)
                        loadNote()
                    }
                }
            }
            alertDialog.show()
        }


}
