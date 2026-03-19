package project.project1.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import project.project1.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotesFlow(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getUnsyncedNotes(): List<Note>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Query("SELECT * FROM notes WHERE serverId = :serverId")
    suspend fun getNoteByServerId(serverId: String): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE isSynced = 0 AND id NOT IN (SELECT id FROM notes WHERE isSynced = 1)")
    suspend fun deleteUnsyncedNotes()

    @Query("UPDATE notes SET isSynced = 1, serverId = :serverId WHERE id = :id")
    suspend fun markAsSynced(id: Int, serverId: String)

    @Query("SELECT COUNT(*) FROM notes WHERE isSynced = 0")
    suspend fun getUnsyncedCount(): Int

    @Query("SELECT COUNT(*) FROM notes WHERE isSynced = 0")
    fun getUnsyncedCountFlow(): Flow<Int>
}