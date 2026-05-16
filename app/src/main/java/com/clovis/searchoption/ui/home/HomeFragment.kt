package com.clovis.searchoption.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clovis.searchoption.R
import com.clovis.searchoption.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        textView.setOnClickListener {
            showSearchDialog(requireContext())
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            DividerWithoutLast(requireContext(), LinearLayoutManager.VERTICAL)
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
                etSearch.layoutParams = params

                //val densityPadding = resources.displayMetrics.density
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

            if(filtered.isEmpty()) {
                query = ""
                length = 0
                adapter.submitList(emptyList(), false)
                recyclerView.visibility = View.GONE
            }

            Log.d("HomeFragment", "Filtered list: $filtered")

            if (length >= 1) {
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(filtered)
                adjustRecyclerViewHeight(recyclerView, filtered.size)
            }
            else {
                //listWrapper.background = ContextCompat.getDrawable(context, R.drawable.bg_rounded_card)
                searchWrapper.setPadding(
                    0, // start
                    0,                        // top
                    0,   // end
                    0
                )
                query = ""
                //adapter.submitList(emptyList())
                //recyclerView.visibility = View.GONE
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