package hu.benceharomi.recipes

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import hu.benceharomi.recipes.adapters.RecipeRecyclerAdapter
import hu.benceharomi.recipes.data.RecipeItem
import hu.benceharomi.recipes.data.RecipeListDatabase
import hu.benceharomi.recipes.fragments.NewRecipeItemDialogFragment
import kotlinx.android.synthetic.main.content_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), RecipeRecyclerAdapter.OnItemClickListener,
    NewRecipeItemDialogFragment.NewRecipeItemDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeRecyclerAdapter
    private lateinit var database: RecipeListDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            NewRecipeItemDialogFragment().show(
                supportFragmentManager,
                NewRecipeItemDialogFragment.TAG
            )
        }
        database = Room.databaseBuilder(
            applicationContext,
            RecipeListDatabase::class.java,
            "recipe_list"
        ).build()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = MainRecyclerView
        adapter = RecipeRecyclerAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadItemsInBackground() {
        thread {
            val items = database.recipeItemDao().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                readQRCode()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun readQRCode() {
        val integrator = IntentIntegrator(this)
        integrator.setRequestCode(IntentIntegrator.REQUEST_CODE)
        integrator.setOrientationLocked(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != IntentIntegrator.REQUEST_CODE) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        if (result != null) {

            if (result.contents == null) {
                Snackbar.make(
                    findViewById<FloatingActionButton>(R.id.fab),
                    getString(R.string.scan_cancelled),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                val newItem = Gson().fromJson(result.contents, RecipeItem::class.java)
                onRecipeItemCreated(newItem)
                Snackbar.make(
                    findViewById<FloatingActionButton>(R.id.fab),
                    getString(R.string.recipe_import_successful),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onItemClicked(item: RecipeItem) {
        val detailsIntent = Intent(this, DetailsActivity::class.java)
        detailsIntent.putExtra("id", item.id)
        startActivity(detailsIntent)
    }

    override fun onRecipeItemCreated(newItem: RecipeItem) {
        thread {
            val newId = database.recipeItemDao().insert(newItem)
            val newRecipeItem = newItem.copy(
                id = newId
            )
            runOnUiThread {
                adapter.addItem(newRecipeItem)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initRecyclerView()
    }
}
