package com.example.prog7313poe.Database.Categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*

/**
 * A ViewModel that talks to Firebase Realtime Database instead of Room.
 * It exposes LiveData that automatically updates whenever the "category" node changes.
 */
class CategoryViewModelFirebase(application: Application) : AndroidViewModel(application) {

    private val dbRef: DatabaseReference = FirebaseDatabase
        .getInstance("https://thriftsense-b5584-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("category")

    private val _categories = MutableLiveData<List<CategoryData>>(emptyList())

    val categories: LiveData<List<CategoryData>> = _categories


    private val categoryListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val list = mutableListOf<CategoryData>()
            snapshot.children.forEach { child ->
                // Deserialize each child into CategoryDataFirebase
                child.getValue(CategoryData::class.java)?.let { category ->
                    // Ensure the ID is set (child.key is the Firebase-generated key)
                    category.categoryId = child.key ?: ""
                    list.add(category)
                }
            }
            // Post the new list to LiveData
            _categories.postValue(list)
        }

        override fun onCancelled(error: DatabaseError) {
            // If something goes wrong, just post an empty list (or handle errors as needed)
            _categories.postValue(emptyList())
        }
    }

    init {
        // Start listening as soon as ViewModel is created
        dbRef.addValueEventListener(categoryListener)
    }

    /**
     * Inserts a new category into Firebase.
     * Automatically generates a free key with push(), then writes the CategoryDataFirebase object.
     */
    fun insertCategory(categoryName: String, onComplete: () -> Unit = {}) {
        val key = dbRef.push().key ?: return
        val newCategory = CategoryData(categoryId = key, categoryName = categoryName)
        dbRef.child(key).setValue(newCategory)
            .addOnCompleteListener { onComplete() }
    }

    /**
     * Deletes a category by its Firebase key (id).
     */
    fun deleteCategory(categoryId: String, onComplete: () -> Unit = {}) {
        if (categoryId.isBlank()) return
        dbRef.child(categoryId).removeValue()
            .addOnCompleteListener { onComplete() }
    }

    /**
     * Deletes all categories under the "category" node.
     */
    fun deleteAllCategories(onComplete: () -> Unit = {}) {
        dbRef.removeValue()
            .addOnCompleteListener { onComplete() }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up the listener to avoid memory leaks
        dbRef.removeEventListener(categoryListener)
    }
}
