package com.clovis.searchoption

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clovis.searchoption.databinding.ActivityMainBinding
import com.clovis.searchoption.ui.home.DividerWithoutLast
import com.clovis.searchoption.ui.home.MyAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val fullList = listOf(
        "papaya",
        "lime",
        "lemon",
        "pineapple",
        "ginger",
        "onion",
        "avocado",
        "mandarin",
        "blackberry",
        "strawberry",
        "apple",
        "banana",
        "orange",
        "grape",
        "watermelon"
    )

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Setup Drawer variables
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // 2. Connect the Left Icon to open the Drawer (Hamburger behavior)
        binding.appBarMain.ivMenuIcon.setOnClickListener {
            drawerLayout.open()
        }

        binding.appBarMain.ivSearchIcon.setOnClickListener {
            showSearchDialog(this)
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.appBarMain.ivSearchIcon, InputMethodManager.SHOW_IMPLICIT)
        }

        // 4. Navigation View Setup
        navView.setupWithNavController(navController)

        // Note: Remove setupActionBarWithNavController(navController, appBarConfiguration)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun showSearchDialog(context: Context) {
        val dialog = BottomSheetDialog(context, R.style.TransparentBottomSheet)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_search, null)

        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val searchWrapper = view.findViewById<LinearLayout>(R.id.search_wrapper)
        val ivClear = view.findViewById<ImageView>(R.id.ivClear)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val listWrapper = view.findViewById<LinearLayout>(R.id.list_wrapper)

        val adapter = MyAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.visibility = View.GONE
        recyclerView.adapter = adapter

        var filtered = emptyList<String>()
        var length = 0
        var query = ""
        val goodDeal = "Good deal!"

        // Must be before show()
        dialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE or
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        dialog.setContentView(view)
        dialog.show()

        val bottomSheet = dialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )

        bottomSheet?.let { sheet ->
            // Rounded card background matching the screenshot
            // sheet.background = ContextCompat.getDrawable(context, R.drawable.bg_rounded_card)

            // Horizontal margins
            val margin = (16 * resources.displayMetrics.density).toInt()
            val params = sheet.layoutParams as ViewGroup.MarginLayoutParams
            params.leftMargin = margin
            params.rightMargin = margin
            sheet.layoutParams = params

            // Behavior
            val behavior = BottomSheetBehavior.from(sheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.isFitToContents = true

            // Remove gap between bottom sheet and keyboard
            ViewCompat.setOnApplyWindowInsetsListener(sheet) { v, insets ->
                v.setPadding(0, 0, 0, 0)
                insets
            }
        }

        recyclerView.addItemDecoration(
            DividerWithoutLast(this, LinearLayoutManager.VERTICAL)
        )

        // Show keyboard immediately
        etSearch.post {
            etSearch.requestFocus()
        }

        etSearch.setOnClickListener {
            //etSearch.requestFocus()
            try {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)
                adapter.submitList(filtered, false)
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error showing keyboard: ${e.message}")
            }
        }

        // Handle keyboard visibility changes
        ViewCompat.setOnApplyWindowInsetsListener(dialog.window!!.decorView) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            if (imeVisible) {
                listWrapper.background = ContextCompat.getDrawable(context, R.drawable.bg_rounded_card)
                bottomSheet?.let { sheet ->
                    // Horizontal margins
                    val margin = (16 * resources.displayMetrics.density).toInt()
                    val params = sheet.layoutParams as ViewGroup.MarginLayoutParams
                    params.leftMargin = margin
                    params.rightMargin = margin
                    sheet.layoutParams = params

                    // Behavior
                    val behavior = BottomSheetBehavior.from(sheet)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.skipCollapsed = true
                    behavior.isFitToContents = true

                    // Remove gap between bottom sheet and keyboard
                    ViewCompat.setOnApplyWindowInsetsListener(sheet) { v, insets ->
                        v.setPadding(0, 0, 0, 0)
                        insets
                    }
                }

                recyclerView.setPadding(
                    0,  // start
                    0,                        // top
                    0,  // end
                    0                         // bottom
                )
                searchWrapper.setPadding(
                    0,  // start
                    0,                        // top
                    0,  // end
                    0                         // bottom
                )
                adjustRecyclerViewHeight(recyclerView, filtered.size)
            }
            else if (length != 0) {
                val screenHeight = context.resources.displayMetrics.heightPixels
                val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                val searchBarHeight = (48 * context.resources.displayMetrics.density).toInt()
                val padding = (24 * context.resources.displayMetrics.density).toInt()
                val topOffset = (120 * context.resources.displayMetrics.density).toInt()

                val availableHeight = screenHeight - statusBar - navBar - searchBarHeight - padding - topOffset

                recyclerView.layoutParams = recyclerView.layoutParams.apply {
                    height = availableHeight
                }
                val density = recyclerView.context.resources.displayMetrics.density
                //val densityPadding = resources.displayMetrics.density
                val margin = (16* resources.displayMetrics.density).toInt()
                val params = etSearch.layoutParams as ViewGroup.MarginLayoutParams
                params.leftMargin = margin
                params.rightMargin = margin
                //etSearch.layoutParams = params

                //val densityPadding = resources.displayMetrics.density


                // Set padding for RecyclerView 3
                recyclerView.setPadding(
                    (16 * density).toInt(), // start
                    0,                        // top
                    (16 * density).toInt(),   // end
                    0                         // bottom
                )
                searchWrapper.setPadding(
                    (16 * density).toInt(), // start
                    0,                        // top
                    (16 * density).toInt(),   // end
                    (20 * density).toInt()
                )

                bottomSheet?.let { sheet ->
                    // Horizontal margins
                    val margin = (1* resources.displayMetrics.density).toInt()
                    val params = sheet.layoutParams as ViewGroup.MarginLayoutParams
                    params.leftMargin = margin
                    params.rightMargin = margin
                    sheet.layoutParams = params

                    // Behavior
                    val behavior = BottomSheetBehavior.from(sheet)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.skipCollapsed = true
                    behavior.isFitToContents = true

                    // Remove gap between bottom sheet and keyboard
                    ViewCompat.setOnApplyWindowInsetsListener(sheet) { v, insets ->
                        v.setPadding(0, 0, 0, 0)
                        insets
                    }
                }

                listWrapper.background = ContextCompat.getDrawable(context, R.drawable.bg_rounded_top)
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(filtered, true)
            }

            insets
        }

        etSearch.addTextChangedListener { editable ->
            query = editable?.toString()?.lowercase().orEmpty()
            length = query.length
            ivClear.visibility = if (length > 0) View.VISIBLE else View.GONE

            filtered = if (length >= 1) {
                fullList.filter { it.lowercase().contains(query) }.take(5)
            } else emptyList()

            if (length >= 1) {
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(filtered)
                adjustRecyclerViewHeight(recyclerView, filtered.size)
            } else {
                //adjustRecyclerViewHeight(recyclerView, 0)

                listWrapper.background = ContextCompat.getDrawable(context, R.drawable.bg_rounded_card)
                searchWrapper.setPadding(
                    0, // start
                    0,                        // top
                    0,   // end
                    0
                )
                adapter.submitList(filtered)
                recyclerView.visibility = View.GONE
                bottomSheet?.let { sheet ->
                    // Rounded card background matching the screenshot
                    // sheet.background = ContextCompat.getDrawable(context, R.drawable.bg_rounded_card)
                    // Horizontal margins
                    val margin = (16 * resources.displayMetrics.density).toInt()
                    val params = sheet.layoutParams as ViewGroup.MarginLayoutParams
                    params.leftMargin = margin
                    params.rightMargin = margin
                    sheet.layoutParams = params
                }
            }
        }

        ivClear.setOnClickListener {
            etSearch.text.clear()
        }
    }

    fun adjustRecyclerViewHeight(recyclerView: RecyclerView, itemCount: Int) {
        val itemHeightDp = 48 // match your item's height in dp
        val maxItems = 5
        val density = recyclerView.context.resources.displayMetrics.density

        val itemHeightPx = (itemHeightDp * density).toInt()
        val newHeight = itemHeightPx * minOf(itemCount, maxItems)

        recyclerView.layoutParams = recyclerView.layoutParams.apply {
            height = newHeight
        }
    }

}