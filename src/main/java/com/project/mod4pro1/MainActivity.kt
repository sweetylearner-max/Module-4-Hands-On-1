package project.project1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import project.project1.data.model.Note
import project.project1.databinding.ActivityMainBinding
import project.project1.ui.NotesAdapter
import project.project1.viewmodel.NoteViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupViewModel()
        setupListeners()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = NotesAdapter { note ->
            showEditNoteDialog(note)
        }
        binding.recyclerViewNotes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
    }

    private fun setupListeners() {
        binding.fabAddNote.setOnClickListener {
            showAddNoteDialog()
        }

        binding.fabSync.setOnClickListener {
            viewModel.syncNotes()
            Snackbar.make(binding.root, "Sync started...", Snackbar.LENGTH_SHORT).show()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncNotes()
        }
    }

    private fun observeData() {
        viewModel.allNotes.observe(this) { notes ->
            adapter.submitList(notes)
            binding.swipeRefreshLayout.isRefreshing = false

            if (notes.isEmpty()) {
                binding.textViewEmpty.visibility = android.view.View.VISIBLE
                binding.recyclerViewNotes.visibility = android.view.View.GONE
            } else {
                binding.textViewEmpty.visibility = android.view.View.GONE
                binding.recyclerViewNotes.visibility = android.view.View.VISIBLE
            }
        }

        viewModel.syncStatus.observe(this) { status ->
            binding.textViewSyncStatus.text = status
        }

        viewModel.isOnline.observe(this) { isOnline ->
            if (isOnline) {
                binding.imageViewSyncStatus.setImageResource(R.drawable.ic_sync_online)
                binding.textViewSyncStatus.text = "Online"
            } else {
                binding.imageViewSyncStatus.setImageResource(R.drawable.ic_sync_offline)
                binding.textViewSyncStatus.text = "Offline"
            }
        }
    }

    private fun showAddNoteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_note, null)
        val titleEditText = dialogView.findViewById<android.widget.EditText>(R.id.editTextNoteTitle)
        val contentEditText = dialogView.findViewById<android.widget.EditText>(R.id.editTextNoteContent)

        MaterialAlertDialogBuilder(this)
            .setTitle("Add New Note")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = titleEditText.text.toString()
                val content = contentEditText.text.toString()

                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val note = Note(
                        title = title,
                        content = content,
                        timestamp = System.currentTimeMillis()
                    )
                    viewModel.insertNote(note)
                    Toast.makeText(this, "Note saved locally", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditNoteDialog(note: Note) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_note, null)
        val titleEditText = dialogView.findViewById<android.widget.EditText>(R.id.editTextNoteTitle)
        val contentEditText = dialogView.findViewById<android.widget.EditText>(R.id.editTextNoteContent)

        titleEditText.setText(note.title)
        contentEditText.setText(note.content)

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Note")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val title = titleEditText.text.toString()
                val content = contentEditText.text.toString()

                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val updatedNote = note.copy(
                        title = title,
                        content = content,
                        isSynced = false
                    )
                    viewModel.updateNote(updatedNote)
                    Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Delete") { _, _ ->
                viewModel.deleteNote(note)
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }
}