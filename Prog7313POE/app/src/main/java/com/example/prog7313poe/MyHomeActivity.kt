package com.example.prog7313poe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuItem
import com.example.prog7313poe.ui.budget.BudgetFragment
import com.example.prog7313poe.ui.expense.expenseview.ExpenseViewFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MyHomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_home)

        //Set the current user's email from intent
        val email = intent.getStringExtra("email") ?: ""
        CurrentUser.email = email

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)


        // Handle navigation drawer item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_account -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AccountDetailsFragment())
                        .commit()
                    true
                }
                R.id.nav_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SettingsFragment())
                        .commit()
                    true
                }
                R.id.nav_dark_mode -> {
                    // TODO: Toggle dark mode logic here
                    true
                }
                R.id.nav_logout -> {
                    finish() // or navigate back to login screen
                    true
                }
                R.id.nav_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SettingsFragment())
                        .commit()
                    true
                }
                else -> false
            }.also {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
        }


        // Bottom navigation setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_transactions -> ExpenseViewFragment()
                R.id.nav_budget -> BudgetFragment()
                else -> HomeFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }

        // Load default fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()

        // System bars padding
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar)) { v, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            v.setPadding(0, topInset, 0, 0)
            insets
        }*/

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_drawer -> {
                drawerLayout.openDrawer(GravityCompat.END)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
