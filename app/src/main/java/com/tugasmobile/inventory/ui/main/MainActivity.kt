package com.tugasmobile.inventory.ui.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.databinding.ActivityMainBinding
import com.tugasmobile.inventory.ui.main.barang.BarangMasuk
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.editdata.RincianFragment
import com.tugasmobile.inventory.ui.main.daftarBarang.DaftarBarang
import com.tugasmobile.inventory.ui.main.setting.SettingActivity
import com.tugasmobile.inventory.ui.main.setting.notifikasi.AlarmScheduler
import com.tugasmobile.inventory.ui.main.setting.notifikasi.NotificationHelper
import com.tugasmobile.inventory.ui.search.SearchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: ViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
        private const val SEARCH_REQUEST_CODE = 1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
            val isDarkMode = withContext(Dispatchers.IO) {
                sharedPreferences.getBoolean("dark_mode", false)
            }

            withContext(Dispatchers.Main) {
                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        mainViewModel = ViewModelProvider(this).get(ViewModel::class.java)
        mainViewModel.loadSetting() // Memuat data pengaturan


        // Observasi perubahan data
        mainViewModel.settingData.observe(this) { setting ->
            if (setting != null) {
                AlarmScheduler.scheduleNotification(this)
            }
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_barang_masuk, R.id.nav_barang_keluar,
                R.id.nav_tabel, R.id.nav_daftar_barang, R.id.nav_history_barang,
                R.id.nav_setting, R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_setting -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> {
                    // Biarkan NavigationUI menangani item lainnya
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }

        checkPermissions()
        val fragment = BarangMasuk()
        val bundle = Bundle()
        bundle.putString("toastMessage", "Data berhasil ditambahkan")
        fragment.arguments = bundle

        // Buat Notifikasi Channel
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()
        val filterStock = intent.getStringExtra("filter_stock")
        if (filterStock != null) {
            val bundle = Bundle().apply {
                putString("filter_stock", filterStock)
            }
            navController.navigate(R.id.nav_daftar_barang, bundle)
        }
        val query=intent.getStringExtra("QUERY_KEY")
        if (query != null) {
            val bundle=Bundle().apply {
                putString("QUERY_KEY",query)
            }
            navController.navigate(R.id.nav_daftar_barang,bundle)
        }

    }


    private fun checkPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        // Cek izin untuk penyimpanan dan kamera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
        }
        // Cek izin notifikasi (hanya untuk Android 13 ke atas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (permissionsNeeded.isNotEmpty()) {
            // Hanya meminta izin jika ada izin yang belum diberikan
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            for (i in permissions.indices) {
                when (permissions[i]) {
                    Manifest.permission.POST_NOTIFICATIONS -> {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Izin notifikasi tidak diberikan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Izin penyimpanan tidak diberikan, beberapa fitur mungkin tidak berfungsi", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val kodeBarang = data?.getStringExtra("SELECTED_KODE_BARANG")
            if (kodeBarang != null) {
                openRincianFragment(kodeBarang)
            }
        }
    }

    private fun openRincianFragment(kodeBarang: String) {
        val fragment = RincianFragment().apply {
            arguments = Bundle().apply {
                putString("KODE_BARANG", kodeBarang)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, fragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Agar bisa kembali ke fragment sebelumnya dengan tombol back
            .commit()
    }


}