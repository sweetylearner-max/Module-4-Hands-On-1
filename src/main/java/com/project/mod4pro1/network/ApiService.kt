package project.project1.network

import project.project1.data.model.Note
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("posts")
    suspend fun getNotes(): Response<List<Note>>

    @GET("posts/{id}")
    suspend fun getNote(@Path("id") id: String): Response<Note>

    @POST("posts")
    suspend fun createNote(@Body note: Note): Response<Note>

    @PUT("posts/{id}")
    suspend fun updateNote(@Path("id") id: String, @Body note: Note): Response<Note>

    @DELETE("posts/{id}")
    suspend fun deleteNote(@Path("id") id: String): Response<Unit>
}