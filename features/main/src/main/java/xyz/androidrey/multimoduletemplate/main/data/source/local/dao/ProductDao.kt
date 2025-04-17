package xyz.androidrey.multimoduletemplate.main.data.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xyz.androidrey.multimoduletemplate.main.data.entity.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM Product ORDER BY title ASC")
    fun getProductPagingSortedByTitle(): PagingSource<Int, Product>

    @Query("SELECT * FROM Product ORDER BY price ASC")
    fun getProductPagingSortedByPrice(): PagingSource<Int, Product>

    @Query("SELECT * FROM Product ORDER BY rating DESC")
    fun getProductPagingSortedByRating(): PagingSource<Int, Product>

    @Query("SELECT * FROM Product")
    suspend fun getProducts(): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Query("DELETE FROM Product")
    suspend fun clearAll()

    @Query("SELECT * FROM Product WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun getProductPagingBySearch(query: String): PagingSource<Int, Product>
}