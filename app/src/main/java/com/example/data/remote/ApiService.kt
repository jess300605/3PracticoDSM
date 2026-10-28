package com.example.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getResourcesDirect(@Url url: String): Response<ResponseBody>

    @GET("recursos")
    suspend fun getResources(): Response<List<ResourceDto>>

    @GET("recursos/{id}")
    suspend fun getResourceById(@Path("id") id: String): Response<ResourceDto>

    @POST("recursos")
    suspend fun createResource(@Body item: ResourceDto): Response<ResourceDto>

    @PUT("recursos/{id}")
    suspend fun updateResource(@Path("id") id: String, @Body item: ResourceDto): Response<ResourceDto>

    @DELETE("recursos/{id}")
    suspend fun deleteResource(@Path("id") id: String): Response<ResponseBody>

    @POST
    suspend fun createResourceDirect(@Url url: String, @Body item: ResourceDto): Response<ResourceDto>

    @PUT
    suspend fun updateResourceDirect(@Url url: String, @Body item: ResourceDto): Response<ResourceDto>

    @DELETE
    suspend fun deleteResourceDirect(@Url url: String): Response<ResponseBody>
}
