package hu.benceharomi.recipes.data

import androidx.room.*

@Dao
interface RecipeItemDao {

    @Dao
    interface ShoppingItemDao {
        @Query("SELECT * FROM recipe_item")
        fun getAll(): List<RecipeItem>

        @Insert
        fun insert(recipeItem: RecipeItem): Long

        @Update
        fun update(recipeItem: RecipeItem)

        @Delete
        fun deleteItem(recipeItem: RecipeItem)

        @Query("DELETE FROM recipe_item")
        fun deleteAll()

        @Query("SELECT * FROM recipe_item WHERE id=:id")
        fun getByID(id: Long): RecipeItem
    }
}