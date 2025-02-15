package com.example.demo.api

import com.example.demo.data.Item
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * ApiService - Defines the API endpoints for fetching Decathlon store items.
 *
 * This interface includes:
 * 1. Fetching paginated items - Supports sorting by name or price.
 * 2. Searching items - Allows searching by name or brand.
 * 3. Sorting items - Retrieves a sorted list based on price or name.
 *
 * The API uses pagination to efficiently load data in chunks.
 */

interface ApiService {

    @GET("items")
    suspend fun getItems(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String?,
        @Query("order") order: String?
    ): List<Item>


    @GET("items/search")
    suspend fun searchItems(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): List<Item>

    // 3. Fetch sorted list
    @GET("items/sorted")
    suspend fun getSortedItems(
        @Query("sortBy") sortBy: String,
        @Query("order") order: String
    ): List<Item>
}

