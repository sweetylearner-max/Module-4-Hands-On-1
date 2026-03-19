package project.project1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import project.project1.data.model.Note
import project.project1.data.repository.NoteRepository
import project.project1.util.NetworkUtils
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NoteRepository(application)

    val allNotes: LiveData<List<Note>> = repository.allNotes

    private val _syncStatus = MutableLiveData<String>()
    val syncStatus: LiveData<String> = _syncStatus

    private val _isOnline = MutableLiveData<Boolean>()
    val isOnline: LiveData<Boolean> = _isOnline

    init {
        checkConnectivity()
        syncNotes()
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note)
            syncNotes()
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
            syncNotes()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
            syncNotes()
        }
    }

    fun syncNotes() {
        viewModelScope.launch {
            _syncStatus.value = "Syncing..."
            repository.syncNotes()
            _syncStatus.value = "Last sync: ${java.text.SimpleDateFormat.getDateTimeInstance().format(java.util.Date())}"
        }
    }

    private fun checkConnectivity() {
        _isOnline.value = NetworkUtils.isInternetAvailable(getApplication())

        // Observe connectivity changes (simplified - in real app use ConnectivityManager)
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(5000)
                _isOnline.postValue(NetworkUtils.isInternetAvailable(getApplication()))
            }
        }
    }
}