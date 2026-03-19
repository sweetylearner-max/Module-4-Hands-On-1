package project.project1.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import project.project1.data.AppDatabase
import project.project1.data.model.Note
import project.project1.network.ApiClient
import project.project1.network.ApiService
import project.project1.util.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteRepository(private val context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val noteDao = database.noteDao()
    private val apiService = ApiClient.getInstance(context)

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun syncNotes() {
        if (NetworkUtils.isInternetAvailable(context)) {
            try {
                // Push local changes to server
                pushLocalChanges()

                // Pull remote changes from server
                pullRemoteChanges()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun pushLocalChanges() {
        val unsyncedNotes = noteDao.getUnsyncedNotes()

        if (unsyncedNotes.isNotEmpty()) {
            unsyncedNotes.forEach { note ->
                try {
                    val response = if (note.serverId == null) {
                        apiService.createNote(note)
                    } else {
                        apiService.updateNote(note.serverId, note)
                    }

                    if (response.isSuccessful) {
                        val serverNote = response.body()
                        val sId = serverNote?.serverId ?: serverNote?.id?.toString() ?: ""
                        noteDao.markAsSynced(note.id, sId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun pullRemoteChanges() {
        try {
            val response = apiService.getNotes()

            if (response.isSuccessful) {
                val remoteNotes = response.body() ?: emptyList()

                remoteNotes.forEach { remoteNote ->
                    val sId = remoteNote.serverId ?: return@forEach
                    val localNote = noteDao.getNoteByServerId(sId)

                    if (localNote == null) {
                        // New note from server
                        noteDao.insertNote(remoteNote.copy(isSynced = true))
                    } else if (remoteNote.lastModified > localNote.lastModified) {
                        // Server has newer version
                        noteDao.updateNote(remoteNote.copy(id = localNote.id, isSynced = true))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUnsyncedCount(): Flow<Int> {
        return noteDao.getUnsyncedCountFlow()
    }
}