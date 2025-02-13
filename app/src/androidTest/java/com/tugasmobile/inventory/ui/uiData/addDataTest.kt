package com.tugasmobile.inventory.ui.uiData

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tugasmobile.inventory.R
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class addDataTest {

    @Before
    fun setUp() {
        ActivityScenario.launch(addData::class.java)
    }

    @Test
    fun testTambahStok() {
        // Verify initial stock
        onView(withId(R.id.edit_stok_barang)).check(matches(withText("0")))

        // Press the "Add Stock" button and check if stock is updated
        onView(withId(R.id.button_add_stok)).perform(click())
        onView(withId(R.id.edit_stok_barang)).check(matches(withText("1")))
    }

    @Test
    fun testKurangiStok() {
        // Verify initial stock
        onView(withId(R.id.edit_stok_barang)).check(matches(withText("0")))

        // Try to remove stock when it's 0 (should stay 0)
        onView(withId(R.id.button_remove_stok)).perform(click())
        onView(withId(R.id.edit_stok_barang)).check(matches(withText("0")))

        // Add stock and then remove it to check if it decreases correctly
        onView(withId(R.id.button_add_stok)).perform(click())
        onView(withId(R.id.button_remove_stok)).perform(click())
        onView(withId(R.id.edit_stok_barang)).check(matches(withText("0")))
    }

    @Test
    fun testSaveDataWithValidInputs() {
        // Mengisi input yang valid
        onView(withId(R.id.editTextNamaBarang)).perform(typeText("Sandal"))
        onView(withId(R.id.editTextKodeBarang)).perform(typeText("SD001"))
        onView(withId(R.id.editTextHargaBarang)).perform(typeText("50000"))
        onView(withId(R.id.edit_stok_barang)).perform(clearText(), typeText("10"))

        // Membuka dropdown kategori
        // Membuka dropdown kategori
        onView(withId(R.id.edt_nama_toko)).perform(click())

// Memilih item dari dropdown berdasarkan teks yang ingin dipilih
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bayi")))
            .inRoot(RootMatchers.isPlatformPopup())  // Memastikan ini adalah popup dropdown
            .perform(click())

        // Memilih warna dengan mengklik checkbox
        onView(withId(R.id.box_item_1)).perform(click())

        // Membuka bottom sheet ukuran dan memilih ukuran
        val sizeSelectionFragment = BottonUkuranSheet()

        // Menyiapkan listener untuk menangkap ukuran yang dipilih
        val listener = mock(BottonUkuranSheet.SizeSelectionListener::class.java)
        sizeSelectionFragment.listener = listener

        // Memulai skenario fragment
        val scenario = FragmentScenario.launchInContainer(BottonUkuranSheet::class.java)

        // Menggunakan FragmentScenario untuk memanipulasi fragment
        scenario.onFragment { fragment ->
            // Mengemulasi pemilihan checkbox untuk ukuran
            fragment.binding.checkbox7.isChecked = true
            fragment.binding.checkbox8.isChecked = true

            // Mengklik tombol simpan
            fragment.binding.buttonSimpanUkuran.performClick()
        }

        // Memeriksa apakah listener dipanggil dengan ukuran yang benar
        verify(listener).onSizeSelected(listOf("7", "8"))

        // Menyimpan data
        onView(withId(R.id.button_save)).perform(click())

        // Mengecek pesan toast yang muncul
        onView(withText("data berhasil ditambahkan")).inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }
}