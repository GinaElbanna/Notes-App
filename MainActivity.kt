package com.zybooks.thenotesapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.zybooks.thenotesapp.database.NoteDatabase
import com.zybooks.thenotesapp.repository.NoteRepository
import com.zybooks.thenotesapp.viewmodel.NoteViewModel
import com.zybooks.thenotesapp.viewmodel.NoteViewModelFactory
enum class NoteSortOrder {
    ALPHABETIC, NEW_FIRST, OLD_FIRST
}
class MainActivity() : AppCompatActivity() {

    lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpViewModel()
    }

    private fun setUpViewModel(){
        val noteRepository = NoteRepository(NoteDatabase(this))
        val viewModelProviderFactory = NoteViewModelFactory(application, noteRepository)
        noteViewModel = ViewModelProvider(this, viewModelProviderFactory)[NoteViewModel::class.java]
    }
    private fun getSettingsSortOrder(): NoteSortOrder {

        // Set sort order from settings
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val sortOrderPref = sharedPrefs.getString("note_order", "alpha")
        return when (sortOrderPref) {
            "alpha" -> NoteSortOrder.ALPHABETIC
            "new_first" -> NoteSortOrder.NEW_FIRST
            else -> NoteSortOrder.OLD_FIRST
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}