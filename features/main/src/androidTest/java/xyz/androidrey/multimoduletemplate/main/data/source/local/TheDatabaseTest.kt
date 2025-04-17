package xyz.androidrey.multimoduletemplate.main.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import xyz.androidrey.multimoduletemplate.main.data.entity.Product

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TheDatabaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TheDatabase
    private lateinit var products: List<Product>

    fun mockProductList(): List<Product> = listOf(
        Product(1, "Redmi Note 12", "Desc", 199.99, 10.0, 4.5, 120, "Xiaomi", "smartphones", ""),
        Product(
            2,
            "Samsung Galaxy S22",
            "Desc",
            699.99,
            15.0,
            4.7,
            80,
            "Samsung",
            "smartphones",
            ""
        ),
        Product(3, "Apple iPhone 14 Pro", "Desc", 999.99, 5.0, 4.8, 50, "Apple", "smartphones", ""),
        Product(4, "OnePlus 11", "Desc", 649.99, 12.0, 4.6, 95, "OnePlus", "smartphones", ""),
        Product(5, "Google Pixel 7", "Desc", 599.99, 8.0, 4.4, 70, "Google", "smartphones", "")
    )

    @Before
    fun setUp() {
        products = mockProductList()
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            TheDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun getProductPagingSortedByTitle_basicRetrieval() = runTest {
        database.productDao.insertAll(products)
        val result = database.productDao.getProductPagingSortedByTitle().load(
            PagingSource.LoadParams.Refresh(0, 5, false)
        ) as PagingSource.LoadResult.Page
        assertEquals(5, result.data.size)
        assertEquals("Apple iPhone 14 Pro", result.data.first().title)
    }

    @Test
    fun getProductPagingSortedByTitle_emptyDatabase() {
        runTest {
            val result = database.productDao.getProductPagingSortedByTitle().load(
                PagingSource.LoadParams.Refresh(0, 5, false)
            ) as PagingSource.LoadResult.Page
            assertTrue(result.data.isEmpty())
        }
    }


    @Test
    fun getProductPagingSortedByTitle_largeDataSet() = runTest {
        val largeList = (1..100).map {
            Product(it, "Product $it", "Desc", it.toDouble(), 10.0, 4.0, 10, "Brand", "cat", "")
        }
        database.productDao.insertAll(largeList)
        val result = database.productDao.getProductPagingSortedByTitle().load(
            PagingSource.LoadParams.Refresh(0, 20, false)
        ) as PagingSource.LoadResult.Page
        assertEquals(20, result.data.size)
    }


    @Test
    fun getProductPagingSortedByTitle_productsWithSameTitle() = runTest {
        val duplicatedTitleList = listOf(
            Product(1, "Phone", "", 100.0, 0.0, 4.0, 10, "", "", ""),
            Product(2, "Phone", "", 200.0, 0.0, 4.0, 10, "", "", "")
        )
        database.productDao.insertAll(duplicatedTitleList)
        val result = database.productDao.getProductPagingSortedByTitle().load(
            PagingSource.LoadParams.Refresh(0, 10, false)
        ) as PagingSource.LoadResult.Page
        assertEquals(2, result.data.size)
    }


    @Test
    fun getProductPagingSortedByPrice_basicRetrieval() = runTest {
        database.productDao.insertAll(products)
        val result = database.productDao.getProductPagingSortedByPrice().load(
            PagingSource.LoadParams.Refresh(0, 5, false)
        ) as PagingSource.LoadResult.Page
        assertEquals(5, result.data.size)
        assertEquals(199.99, result.data.first().price, 0.01)
    }

    @Test
    fun getProductPagingSortedByPrice_emptyDatabase() {
        runTest {
            val result = database.productDao.getProductPagingSortedByPrice().load(
                PagingSource.LoadParams.Refresh(0, 5, false)
            ) as PagingSource.LoadResult.Page
            assertTrue(result.data.isEmpty())
        }
    }


    @Test
    fun getProductPagingSortedByPrice_largeDataSet() = runTest {
        val largeList = (1..100).map {
            Product(it, "Product $it", "", it.toDouble(), 0.0, 3.0, 10, "", "", "")
        }
        database.productDao.insertAll(largeList)
        val result = database.productDao.getProductPagingSortedByPrice().load(
            PagingSource.LoadParams.Refresh(0, 20, false)
        ) as PagingSource.LoadResult.Page
        assertEquals(20, result.data.size)
    }


    @Test
    fun getProductPagingSortedByPrice_productsWithSamePrice() = runTest {
        val list = listOf(
            Product(1, "A", "", 100.0, 0.0, 4.0, 10, "", "", ""),
            Product(2, "B", "", 100.0, 0.0, 4.0, 10, "", "", "")
        )
        database.productDao.insertAll(list)
        val result = database.productDao.getProductPagingSortedByPrice().load(
            PagingSource.LoadParams.Refresh(0, 10, false)
        ) as PagingSource.LoadResult.Page
        assertEquals(2, result.data.size)
    }


    @Test
    fun getProductPagingSortedByRating_basicRetrieval() = runTest {
        database.productDao.insertAll(products)
        val result = database.productDao.getProductPagingSortedByRating().load(
            PagingSource.LoadParams.Refresh(0, 5, false)
        ) as PagingSource.LoadResult.Page
        assertEquals(5, result.data.size)
        assertEquals(4.8, result.data.first().rating, 0.01)
    }

    @Test
    fun getProductPagingSortedByRating_emptyDatabase() {
        runTest {
            val result = database.productDao.getProductPagingSortedByRating().load(
                PagingSource.LoadParams.Refresh(0, 5, false)
            ) as PagingSource.LoadResult.Page
            assertTrue(result.data.isEmpty())
        }
    }


    @Test
    fun getProductPagingSortedByRating_largeDataSet() = runTest {
        val largeList = (1..100).map {
            Product(it, "Product $it", "", 10.0, 0.0, it % 5 + 1.0, 10, "", "", "")
        }
        database.productDao.insertAll(largeList)
        val result = database.productDao.getProductPagingSortedByRating().load(
            PagingSource.LoadParams.Refresh(0, 20, false)
        ) as PagingSource.LoadResult.Page
        assertEquals(20, result.data.size)
    }


    @Test
    fun getProductPagingSortedByRating_productsWithSameRating() = runTest {
        val list = listOf(
            Product(1, "A", "", 100.0, 0.0, 4.5, 10, "", "", ""),
            Product(2, "B", "", 150.0, 0.0, 4.5, 10, "", "", "")
        )
        database.productDao.insertAll(list)
        val result = database.productDao.getProductPagingSortedByRating().load(
            PagingSource.LoadParams.Refresh(0, 10, false)
        ) as PagingSource.LoadResult.Page
        assertEquals(2, result.data.size)
    }


    @Test
    fun getProducts_basicRetrieval() = runTest {
        database.productDao.insertAll(products)
        val result = database.productDao.getProducts()
        assertEquals(5, result.size)
    }

    @Test
    fun getProducts_emptyDatabase() {
        runTest {
            val result = database.productDao.getProducts()
            assertTrue(result.isEmpty())
        }
    }


    @Test
    fun getProducts_largeDataSet() = runTest {
        val largeList = (1..1000).map {
            Product(it, "Product $it", "", 10.0, 0.0, 3.0, 10, "", "", "")
        }
        database.productDao.insertAll(largeList)
        val result = database.productDao.getProducts()
        assertEquals(1000, result.size)
    }


    @Test
    fun insertAll_basicInsertion() = runTest {
        database.productDao.insertAll(products)
        val result = database.productDao.getProducts()
        assertEquals(products.size, result.size)
    }


    @Test
    fun insertAll_emptyList() = runTest {
        database.productDao.insertAll(emptyList())
        val result = database.productDao.getProducts()
        assertTrue(result.isEmpty())
    }


    @Test
    fun insertAll_duplicateEntries() = runTest {
        val duplicateList = listOf(products[0], products[0])
        database.productDao.insertAll(duplicateList)
        val result = database.productDao.getProducts()
        assertEquals(1, result.size) // primary key constraint
    }


    @Test
    fun insertAll_largeDataSet() = runTest {
        val largeList = (1..1000).map {
            Product(it, "Product $it", "", 10.0, 0.0, 3.0, 10, "", "", "")
        }
        database.productDao.insertAll(largeList)
        val result = database.productDao.getProducts()
        assertEquals(1000, result.size)
    }


    @Test
    fun insertAll_mixedNewAndExistingProducts() = runTest {
        database.productDao.insertAll(listOf(products[0]))
        val newList = listOf(products[0], products[1])
        database.productDao.insertAll(newList)
        val result = database.productDao.getProducts()
        assertEquals(2, result.size)
    }


    @Test
    fun clearAll_basicClear() = runTest {
        database.productDao.insertAll(products)
        database.productDao.clearAll()
        val result = database.productDao.getProducts()
        assertTrue(result.isEmpty())
    }


    @Test
    fun clearAll_emptyDatabase() = runTest {
        database.productDao.clearAll()
        val result = database.productDao.getProducts()
        assertTrue(result.isEmpty())
    }


    @Test
    fun clearAll_afterInsert() = runTest {
        database.productDao.insertAll(products)
        database.productDao.clearAll()
        val result = database.productDao.getProducts()
        assertEquals(0, result.size)
    }
}
