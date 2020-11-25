package hu.benceharomi.recipes

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import hu.benceharomi.recipes.adapters.DetailsPagerAdapter
import hu.benceharomi.recipes.data.RecipeItem
import hu.benceharomi.recipes.data.RecipeListDatabase
import hu.benceharomi.recipes.fragments.NewRecipeItemDialogFragment
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.fragment_share.*
import kotlin.concurrent.thread

class DetailsActivity : AppCompatActivity(),
    NewRecipeItemDialogFragment.NewRecipeItemDialogListener {
    private lateinit var database: RecipeListDatabase
    private lateinit var item: RecipeItem
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val id = intent.getLongExtra("id", 0)
        database = Room.databaseBuilder(
            applicationContext,
            RecipeListDatabase::class.java,
            "recipe_list"
        ).build()
        getRecipe(id)
        vpDetails.adapter = DetailsPagerAdapter(supportFragmentManager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_remove -> {
                removeDialog()
                true
            }
            R.id.action_edit -> {
                onItemEdited()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun removeDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.remove_all_dialog_message)
            .setPositiveButton(R.string.ok) { _, _ ->
                onItemRemoved(item)
                this.finish()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun onItemRemoved(item: RecipeItem) {
        thread {
            database.recipeItemDao().deleteItem(item)
        }
    }

    private fun onItemEdited() {
        NewRecipeItemDialogFragment(item).show(
            supportFragmentManager,
            NewRecipeItemDialogFragment.TAG
        )
    }

    private fun getRecipe(id: Long) {
        thread {
            item = database.recipeItemDao().getByID(id)
            runOnUiThread {
                update(item)
            }
        }
    }

    private fun update(item: RecipeItem) {
        title = item.name
        findViewById<TextView>(R.id.textBlock).text = item.description
        generateQRCode()
    }

    override fun onRecipeItemCreated(newItem: RecipeItem) {
        item = newItem
        thread {
            database.recipeItemDao().update(item)
            runOnUiThread {
                update(item)
            }
        }
    }

    private fun generateQRCode() {
        val textForQR = Gson().toJson(item)
        val bitMatrix = MultiFormatWriter().encode(textForQR, BarcodeFormat.QR_CODE, 800, 800)
        bitmap = BarcodeEncoder().createBitmap(bitMatrix)
        qrCode.setImageBitmap(bitmap)
    }
}