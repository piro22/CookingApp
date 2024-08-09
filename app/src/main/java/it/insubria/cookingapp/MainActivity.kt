package it.insubria.cookingapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        //CAMBIO COLORE STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.coquelicot)
        }

        //SETUP DATABASE
        val dataModel = ViewModelProvider(this).get(DataModel::class.java)
        dataModel.dbHelper = Database_SQL(this)

        //TODO DA ELIMINARE POI
        val dbw = dataModel.dbHelper!!.writableDatabase
        dbw.execSQL("DELETE FROM unita_di_misura")
        dbw.execSQL("INSERT INTO unita_di_misura VALUES ('- - -'), ('L'), ('dL'), ('cL'), ('mL'), ('Kg'), ('h'), ('g'), ('tsp'), ('tbsp'), ('cup'), ('pt'), ('qt'), ('gal'), ('Oz'), ('lb'), ('qb')")
        //-----------------------------

        //SETUP DRAWER
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        var toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment()).commit()
            }
            R.id.nav_settings -> {
                val settingFragment = SettingsFragment()
                //settingFragment.setDatabaseHelper(databaseHelper)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, settingFragment).commit()
            }
            R.id.nav_favorites -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FavoritesFragment()).commit()
            }
            R.id.nav_list -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ShoppingListFragment()).commit()
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "LOGGED OUT", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true

    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            //onBackPressedDispatcher.onBackPressed()
            super.onBackPressed()
        }
    }
}