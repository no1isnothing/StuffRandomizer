package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.ItemList
import com.thebipolaroptimist.stuffrandomizer.data.Match
import com.thebipolaroptimist.stuffrandomizer.data.MatchSet
import com.thebipolaroptimist.stuffrandomizer.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

/**
 * The launching [AppCompatActivity] for the project. Starts the [HomeFragment].
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            // TODO #7: Remove, hide behind flag, or update to be 'default data'
            R.id.action_add_data -> {
                addSampleData()
                true
            }
            // TODO #7: Remove, hide behind flag, or move to advanced settings with a warning
            R.id.action_clear_data -> {
                clearAllData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Handles toolbar back button
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_content_main).navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun addSampleData() {
        mainViewModel.insertMatchSet(createSampleMatchData())
        mainViewModel.insertItemList(createSampleItemListData())
    }

    private fun clearAllData() {
        mainViewModel.deleteItemLists()
        mainViewModel.deleteMatchSets()
    }

    private fun createSampleMatchData(): MatchSet = MatchSet(
        UUID.randomUUID(),
        "Skyrim 2024",
        listOf(
            Match("Jane", mapOf(Pair("Aedra", "Talos"), Pair("Daedra", "Clavicus Vile"))),
            Match("Bear", mapOf(Pair("Aedra", "Mara"), Pair("Daedra", "Peryite"))),
            Match("The Tooth Fairy", mapOf(Pair("Aedra", "Julianos"), Pair("Daedra", "Vaermina"))),
            Match("Gifty", mapOf(Pair("Aedra", "Stendar"), Pair("Daedra", "Merida"))),
        )
    )

    private fun createSampleItemListData(): ItemList = ItemList(
        UUID.randomUUID(),
        "Aedra",
        listOf(
            "Talos",
            "Julianos",
            "Arkay",
            "Akatosh",
            "Mara",
            "Stendarr",
            "Dibella",
            "Kynareth",
            "Zenithar"
        )
    )
}