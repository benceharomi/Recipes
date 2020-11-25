package hu.benceharomi.recipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.benceharomi.recipes.R
import hu.benceharomi.recipes.data.RecipeItem

class RecipeRecyclerAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecipeRecyclerAdapter.RecipeViewHolder>() {

    private val items = mutableListOf<RecipeItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_recipe_list, parent, false)
        return RecipeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(items[position], itemClickListener)
    }

    fun addItem(item: RecipeItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(shoppingItems: List<RecipeItem>) {
        items.clear()
        items.addAll(shoppingItems)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClicked(item: RecipeItem)
        fun onRecipeItemCreated(newItem: RecipeItem)
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.RecipeItemNameTextView)

        private var recipeItem: RecipeItem? = null

        fun bind(item: RecipeItem, clickListener: OnItemClickListener) {
            nameTextView.text = item.name
            recipeItem = item
            itemView.setOnClickListener {
                clickListener.onItemClicked(item)
            }
        }
    }
}