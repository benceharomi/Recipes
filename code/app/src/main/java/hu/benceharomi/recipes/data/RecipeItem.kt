package hu.benceharomi.recipes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "recipe_item")
data class RecipeItem(
    @Transient @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long?,
    @SerializedName("recipe_name") @ColumnInfo(name = "name") val name: String,
    @SerializedName("recipe_description") @ColumnInfo(name = "description") val description: String,
)