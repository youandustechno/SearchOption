package com.clovis.searchoption.ui.home

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clovis.searchoption.R
import com.clovis.searchoption.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior

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
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showSearchDialog(context: Context) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_search, null)

        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val ivClear = view.findViewById<ImageView>(R.id.ivClear)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val adapter = MyAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.visibility = View.GONE
        recyclerView.adapter = adapter

        var filtered = emptyList<String>()
        var length = 0
        var query = ""

        dialog.setContentView(view)
        dialog.window?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )?.setBackgroundColor(Color.TRANSPARENT)
        //dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.show()

        val bottomSheet = dialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.isFitToContents = true
        }

        bottomSheet?.let { sheet ->
            val layoutParams = sheet.layoutParams as ViewGroup.MarginLayoutParams
            val margin = (16 * resources.displayMetrics.density).toInt() // 16dp

            layoutParams.leftMargin = margin
            layoutParams.rightMargin = margin

            sheet.layoutParams = layoutParams
        }

        bottomSheet?.setBackgroundColor(Color.TRANSPARENT)

        val marginHorizontal = 12 // dp
        val density = context.resources.displayMetrics.density
        val marginPx = (marginHorizontal * density).toInt()
        val screenWidth = context.resources.displayMetrics.widthPixels

//        dialog.window?.setLayout(screenWidth - (marginPx * 2),
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )



        // Show keyboard automatically
        etSearch.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)
        recyclerView.addItemDecoration(
            DividerWithoutLast(requireContext(),
                LinearLayoutManager.VERTICAL))

        ViewCompat.setOnApplyWindowInsetsListener(dialog.window!!.decorView) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            if (!imeVisible && length != 0) {
                filtered = fullList.filter { item ->
                    item.lowercase().contains(query)
                }.take(5)
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(filtered)
                adjustRecyclerViewHeight(recyclerView, 10)
                val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                view.setPadding(0, 0, 0, imeHeight)
            }
            insets
        }


        // Listen for text changes
        etSearch.addTextChangedListener { editable ->

            query = editable?.toString()?.lowercase().orEmpty()
            length = query.length
            ivClear.visibility = if (length > 0) View.VISIBLE else View.GONE

            filtered =  if (length >= 1) {
                fullList.filter { item ->
                    item.lowercase().contains(query)
                }.take(5)

            } else emptyList()

            if(length >= 1) {
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(filtered)
            }
            else {
                recyclerView.visibility = View.GONE
            }
        }

        dialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
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