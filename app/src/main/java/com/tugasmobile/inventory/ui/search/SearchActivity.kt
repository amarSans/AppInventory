package com.tugasmobile.inventory.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterDataSearch
import com.tugasmobile.inventory.data.SearchData
import com.tugasmobile.inventory.databinding.ActivitySearchBinding
import com.tugasmobile.inventory.ui.InventoryViewModelFactory
import com.tugasmobile.inventory.ui.editdata.DetailBarang
import com.tugasmobile.inventory.ui.main.MainActivity

class SearchActivity : AppCompatActivity() {
    private lateinit var adapter: AdapterDataSearch
    private lateinit var recyclerView: RecyclerView
    private val searchViewModel: SearchViewModel  by viewModels {
        InventoryViewModelFactory.getInstance(this.application)
    }
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("SearchActivity", "onCreate called")
        // Tombol kembali
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        val searchEditText = findViewById<TextInputEditText>(R.id.editTextSearch)
        recyclerView = findViewById(R.id.recyclerView)

        adapter = AdapterDataSearch(emptyList()) { selectedBarang ->
            onBarangSelected(selectedBarang)
        }
        searchViewModel.searchResults.observe(this) { results ->
            adapter.updateList(results)
            val controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in)
            binding.recyclerView.layoutAnimation = controller
            binding.recyclerView.scheduleLayoutAnimation()
            recyclerView.visibility = if (results.isNotEmpty()) View.VISIBLE else View.GONE
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (!text.isNullOrEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    searchBarang(text.toString())
                } else {
                    recyclerView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            Log.d("SearchActivity", "Editor Action triggered")
            Log.d("SearchActivity", "Action ID: $actionId")
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.editTextSearch.text.toString().trim()
                val filterQuery = filterQuery(query)
                if (filterQuery.isNotEmpty()) {
                    // Pindah Activity dengan data pencarian
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("QUERY_KEY", query)
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }
        binding.textInputLayoutSearch.setStartIconOnClickListener {
            Log.d("SearchActivity", "Start icon clicked")
            val query = binding.editTextSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                Log.d("SearchActivity", "Ikon search diklik: $query")
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("QUERY_KEY", query)
                startActivity(intent)

            }
        }
        setupHideKeyboardWhenTouchOutside()

        // Tambahkan animasi masuk
        findViewById<View>(android.R.id.content).animate().alpha(1f).setDuration(200).start()
    }

    private fun searchBarang(query: String) {
        searchViewModel.search(query)
    }
    private fun setupHideKeyboardWhenTouchOutside() {
        binding.searchBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Penting: agar aksesibilitas tetap jalan
                v.performClick()

                if (binding.editTextSearch.isFocused) {
                    binding.editTextSearch.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
                }
            }
            false
        }


    }


    private fun onBarangSelected(barang: SearchData) {
        val intent = Intent(this, DetailBarang::class.java).apply {
            putExtra("ID_BARANG", barang.id)
        }
        startActivity(intent)

    }

    private fun filterQuery(query: String): String {
        val keywords = query.lowercase().split(" ")
        val resultList = searchViewModel.searchResults.value ?: emptyList()
        val recognizedWords = resultList.flatMap {
            listOfNotNull(
                it.id,
                it.merekBarang,
                it.karakteristik
            ).flatMap { attr ->
                attr.lowercase().split(" ")
            }
        }.distinct()

        // Bandingkan tiap kata dari query, hanya simpan yang dikenali
        val filteredWords = keywords.filter { keyword ->
            recognizedWords.any { it.contains(keyword) || keyword.contains(it) }
        }

        return filteredWords.joinToString(" ")
    }

}