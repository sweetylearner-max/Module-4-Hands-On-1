package project.project1.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    @com.google.gson.annotations.SerializedName("body")
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    @com.google.gson.annotations.SerializedName("id")
    val serverId: String? = null,
    val lastModified: Long = System.currentTimeMillis()
)