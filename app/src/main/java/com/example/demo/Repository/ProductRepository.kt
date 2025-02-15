package com.example.demo.Repository

import com.example.demo.api.ApiService
import com.example.demo.data.DummyData
import com.example.demo.data.Item
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(private val apiService: ApiService) {
    /**
     * Fetch paginated items from DummyData.
     *
     * @param page Page number (1-based index).
     * @param pageSize Number of items per page.
     * @return List of paginated items.
     */
    fun fetchItems(page: Int, pageSize: Int): List<Item> {
        val startIndex = (page - 1) * pageSize
        val endIndex = (startIndex + pageSize).coerceAtMost(DummyData.items.size)

        return if (startIndex < DummyData.items.size) {
            // TODO: Replace with actual API Implementation
            DummyData.items.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }

    /**
     * Search items by name or brand name in a paginated manner.
     *
     * @param query Search query.
     * @param page Page number.
     * @param size Number of items per page.
     * @return List of filtered and paginated items.
     */
    fun searchItems(query: String): List<Item> {
        return DummyData.items.filter {
            it.name.contains(query, ignoreCase = true) || it.brand.contains(query, ignoreCase = true)
        }
    }

    /**
     * Fetch a sorted list of items.
     *
     * @param sortBy Sorting field ("name" or "price").
     * @param order Sorting order ("asc" or "desc").
     * @return List of sorted items.
     */
    fun sortItems(items: List<Item>, sortBy: String, order: String): List<Item> {
        return when (sortBy) {
            "name" -> {
                if (order == "asc") items.sortedBy { it.name }
                else items.sortedByDescending { it.name }
            }
            "price" -> {
                if (order == "asc") items.sortedBy { it.price }
                else items.sortedByDescending { it.price }
            }
            else -> items
        }
    }


}