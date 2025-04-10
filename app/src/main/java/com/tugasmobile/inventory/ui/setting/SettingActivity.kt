package com.tugasmobile.inventory.ui.setting

import android.Manifest
import android.app.TimePickerDialog
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.tugasmobile.inventory.data.SettingData
import com.tugasmobile.inventory.database.BrgDatabaseHelper
import com.tugasmobile.inventory.databinding.ActivitySettingBinding
import com.tugasmobile.inventory.ui.InventoryViewModelFactory
import com.tugasmobile.inventory.ui.setting.notifikasi.AlarmScheduler
import com.tugasmobile.inventory.ui.setting.transferData.exportData
import com.tugasmobile.inventory.ui.setting.transferData.importData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class SettingActivity : AppCompatActivity() {
    private val SettingViewModel: SettingViewModel by viewModels {
        InventoryViewModelFactory.getInstance(this.application)
    }
    private lateinit var binding: ActivitySettingBinding
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val switchmode=binding.switchMode
        val switchNotif = binding.switchNotif
        val spinnerHariMulai = binding.spinnerHariMulai
        val spinnerHariAkhir = binding.spinnerHariAkhir
        val btnPilihJam = binding.btnPilihJam
        val txtJamDipilih = binding.txtJamDipilih

        binding.btnExport.setOnClickListener {
            SettingViewModel.exportDatabase(this)
        }

        binding.btnImport.setOnClickListener {
            SettingViewModel.importDatabase(this)
        }

        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            switchmode.isChecked = true
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            switchmode.isChecked = false
        }

        switchmode.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("dark_mode", true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
               editor.putBoolean("dark_mode", false)
            }
            editor.apply()

        }
        val daysOfWeek = arrayOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

        // Adapter untuk Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Atur adapter ke Spinner
        spinnerHariMulai.adapter = adapter
        spinnerHariAkhir.adapter = adapter
        SettingViewModel.loadSetting()

        SettingViewModel.settingData.observe(this) { setting ->
            if (setting != null) {
                // Tampilkan data di UI
                switchNotif.isChecked = setting.isNotifEnabled
                txtJamDipilih.text = "${setting.notifTime}"
                spinnerHariMulai.setSelection(daysOfWeek.indexOf(setting.startDay))
                spinnerHariAkhir.setSelection(daysOfWeek.indexOf(setting.endDay))
            } else {
                // Jika data null, atur nilai default
                switchNotif.isChecked = false
                txtJamDipilih.text = "08:00" // Nilai default
                spinnerHariMulai.setSelection(0) // Nilai default: Senin
                spinnerHariAkhir.setSelection(0) // Nilai default: Rabu
            }
        }


        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkNotificationPermission()
                Toast.makeText(this, "Notifikasi diaktifkan", Toast.LENGTH_SHORT).show()
            } else {
                AlarmScheduler.cancelNotification(this)
                Toast.makeText(this, "Notifikasi dimatikan", Toast.LENGTH_SHORT).show()
            }
        }

        // Atur listener untuk tombol Pilih Jam
        btnPilihJam.setOnClickListener {
            // Tampilkan TimePickerDialog
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    // Simpan jam yang dipilih dan tampilkan di TextView
                    txtJamDipilih.text = "${String.format("%02d:%02d", hourOfDay, minute)}"
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }
        binding.imgViewBackSetting.setOnClickListener{
            val isNotifEnabled = switchNotif.isChecked
            val settingData=SettingData(
                isNotifEnabled = isNotifEnabled,
                notifTime = txtJamDipilih.text.toString().replace(" ", ""),
                startDay = spinnerHariMulai.selectedItem.toString(),
                endDay = spinnerHariAkhir.selectedItem.toString()
            )
            SettingViewModel.saveSetting(settingData)

            Toast.makeText(this, "Pengaturan Disimpan", Toast.LENGTH_SHORT).show()
            // Jadwalkan Notifikasi Ulang
            if (isNotifEnabled) {
                AlarmScheduler.rescheduleNotification(this)
            } else {
                // Jika notifikasi dinonaktifkan, batalkan notifikasi
                AlarmScheduler.cancelNotification(this)
            }

            finish()
        }
        binding.btnSync.setOnClickListener {
            lifecycleScope.launch {
                val semuaBarang = withContext(Dispatchers.IO) {
                    SettingViewModel.ambilSemuaUriDariDatabase() // suspend function
                }
                syncGambarDenganDatabase(semuaBarang)
                Toast.makeText(this@SettingActivity, "Sinkronisasi selesai", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Minta izin notifikasi
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    private fun syncGambarDenganDatabase(barangList: List<String>) {
        val databaseUris = barangList.mapNotNull { uriString ->
            try {
                Uri.parse(uriString)
            } catch (e: Exception) {
                null
            }
        }

        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val resolver = this.contentResolver
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 ke atas - bisa pakai RELATIVE_PATH
            val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
            val selectionArgs = arrayOf("%InventoryApp%")

            resolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                null
            )
        } else {
            // Android 9 ke bawah - fallback tanpa RELATIVE_PATH
            resolver.query(
                collection,
                projection,
                null,
                null,
                null
            )
        }

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val uri = ContentUris.withAppendedId(collection, id)

                if (!databaseUris.contains(uri)) {
                    resolver.delete(uri, null, null)
                    Log.d("SyncGambar", "Gambar $uri dihapus karena tidak ada di DB.")
                }
            }
        }
    }




}