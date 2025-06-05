package com.muammar.inventory.ui.setting

import android.Manifest
import android.app.TimePickerDialog
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.muammar.inventory.data.SettingData
import com.muammar.inventory.databinding.ActivitySettingBinding
import com.muammar.inventory.ui.InventoryViewModelFactory
import com.muammar.inventory.ui.setting.notifikasi.AlarmScheduler
import com.muammar.inventory.utils.AnimationHelper
import kotlinx.coroutines.CoroutineScope
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
    private lateinit var pickZipFileLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var switchNotif: SwitchCompat
    private lateinit var spinnerHariMulai: Spinner
    private lateinit var spinnerHariAkhir: Spinner
    private lateinit var txtJamDipilih: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnimationHelper.animateItems(binding.linearLayoutSetting,this)
        val switchmode=binding.switchMode
        val btnPilihJam = binding.btnPilihJam
         switchNotif = binding.switchNotif
         spinnerHariMulai = binding.spinnerHariMulai
         spinnerHariAkhir = binding.spinnerHariAkhir
         txtJamDipilih = binding.txtJamDipilih


        binding.btnExport.setOnClickListener {
            binding.btnExport.isEnabled = false
            binding.btnExport.text = ""
            binding.progressExport.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    SettingViewModel.exportDatabase(this@SettingActivity)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SettingActivity, "Export selesai!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SettingActivity, "Gagal export: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        binding.progressExport.visibility = View.GONE
                        binding.btnExport.text = "Export"
                        binding.btnExport.isEnabled = true
                    }
                }
            }
        }
        pickZipFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri == null) {
                binding.progressImport.visibility = View.GONE
                binding.btnImport.text = "Import"
                binding.btnImport.isEnabled = true
                return@registerForActivityResult
            }
            uri.let {

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        SettingViewModel.importDatabase(this@SettingActivity, it)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SettingActivity, "Import selesai!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SettingActivity, "Gagal import: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } finally {
                        withContext(Dispatchers.Main) {
                            binding.progressImport.visibility = View.GONE
                            binding.btnImport.text = "Import"
                            binding.btnImport.isEnabled = true
                        }
                    }
                }
            }
        }

        binding.btnImport.setOnClickListener {
            binding.btnImport.isEnabled = false
            binding.btnImport.text = ""
            binding.progressImport.visibility = View.VISIBLE
            pickZipFileLauncher.launch(arrayOf("application/zip"))
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

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerHariMulai.adapter = adapter
        spinnerHariAkhir.adapter = adapter
        SettingViewModel.loadSetting()

        SettingViewModel.settingData.observe(this) { setting ->
            if (setting != null) {
                switchNotif.isChecked = setting.isNotifEnabled
                txtJamDipilih.text = "${setting.notifTime}"
                spinnerHariMulai.setSelection(daysOfWeek.indexOf(setting.startDay))
                spinnerHariAkhir.setSelection(daysOfWeek.indexOf(setting.endDay))
            } else {
                switchNotif.isChecked = false
                txtJamDipilih.text = "08:00"
                spinnerHariMulai.setSelection(0)
                spinnerHariAkhir.setSelection(0)
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

        btnPilihJam.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    txtJamDipilih.text = "${String.format("%02d:%02d", hourOfDay, minute)}"
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }
        binding.imgViewBackSetting.setOnClickListener{
            saveSetting()
        }
        binding.btnSync.setOnClickListener {
            lifecycleScope.launch {
                val semuaBarang = withContext(Dispatchers.IO) {
                    SettingViewModel.ambilSemuaUriDariDatabase()
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
    private fun saveSetting() {
        val isNotifEnabled = switchNotif.isChecked
        val settingData = SettingData(
            isNotifEnabled = isNotifEnabled,
            notifTime = txtJamDipilih.text.toString().replace(" ", ""),
            startDay = spinnerHariMulai.selectedItem.toString(),
            endDay = spinnerHariAkhir.selectedItem.toString()
        )
        SettingViewModel.saveSetting(settingData)

        Toast.makeText(this, "Pengaturan Disimpan", Toast.LENGTH_SHORT).show()
        if (isNotifEnabled) {
            AlarmScheduler.rescheduleNotification(this)
        } else {
            AlarmScheduler.cancelNotification(this)
        }
        finish()
    }
    override fun onBackPressed() {
        saveSetting()
        super.onBackPressed()
    }




}