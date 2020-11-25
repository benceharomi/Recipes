package hu.benceharomi.recipes.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.benceharomi.recipes.R
import hu.benceharomi.recipes.data.RecipeItem

class NewRecipeItemDialogFragment() : DialogFragment() {
    interface NewRecipeItemDialogListener {
        fun onRecipeItemCreated(newItem: RecipeItem)
    }

    companion object {
        const val TAG = "NewRecipeItemDialogFragment"
    }

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText

    private lateinit var listener: NewRecipeItemDialogListener

    private var item: RecipeItem? = null
    private var type: CreateOrEdit = CreateOrEdit.CREATE

    enum class CreateOrEdit {
        CREATE, EDIT
    }

    constructor(item: RecipeItem) : this() {
        this.item = item
        this.type = CreateOrEdit.EDIT
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title: Int = when (type) {
            CreateOrEdit.CREATE -> R.string.new_recipe_item
            CreateOrEdit.EDIT -> R.string.edit_recipe_item
        }
        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { _, _ ->
                if (isValid()) {
                    listener.onRecipeItemCreated(getRecipeItem())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewRecipeItemDialogListener
            ?: throw RuntimeException("Activity must implement the NewRecipeItemDialogListener interface!")
    }

    private fun getContentView(): View {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.dialog_new_recipe_item, null)
        nameEditText = contentView.findViewById(R.id.recipeItemNameEditText)
        descriptionEditText = contentView.findViewById(R.id.recipeItemDescriptionEditTextMultiline)
        if (item != null) {
            setFields()
        }
        return contentView
    }

    private fun isValid() = nameEditText.text.isNotEmpty()

    private fun getRecipeItem() = RecipeItem(
        id = item?.id,
        name = nameEditText.text.toString(),
        description = descriptionEditText.text.toString(),
    )

    private fun setFields() {
        nameEditText.setText(item!!.name)
        descriptionEditText.setText(item!!.description)
    }
}