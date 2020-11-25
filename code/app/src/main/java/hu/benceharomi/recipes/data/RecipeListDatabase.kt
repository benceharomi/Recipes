package hu.benceharomi.recipes.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecipeItem::class], version = 1)
abstract class RecipeListDatabase : RoomDatabase() {
    abstract fun recipeItemDao(): RecipeItemDao.ShoppingItemDao
}