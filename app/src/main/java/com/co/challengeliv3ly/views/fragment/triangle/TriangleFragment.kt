package com.co.challengeliv3ly.views.fragment.triangle

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.extensions.observe
import android.support.core.utils.DriverUtils
import android.view.MotionEvent
import android.view.View
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.annotations.ActionBarOptions
import com.co.challengeliv3ly.app.AppFragment
import com.co.challengeliv3ly.extensions.getColorRandom
import com.co.challengeliv3ly.extensions.lock
import com.co.challengeliv3ly.models.ShapeType
import com.co.challengeliv3ly.utils.ResourceValidation
import com.co.challengeliv3ly.viewmodels.MainViewModel
import com.co.challengeliv3ly.views.adapter.ShapeAdapter
import kotlinx.android.synthetic.main.fragment_triangle.*

@ActionBarOptions(
    title = R.string.title_tab_triangle,
    left = false
)
@LayoutId(R.layout.fragment_triangle)
class TriangleFragment : AppFragment<MainViewModel>() {

    private val adapter by lazy { ShapeAdapter(requireContext(), rlMainView) }
    private val type = ShapeType.TRIANGLE.value
    private var currentShapePosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun observeData() {
        super.observeData()
        viewModel.refreshLoading.observe(this) {
            it?.let {
                it lock rlMainView
            }
        }
        viewModel.getShapeColorSuccess.observe(this) {
            adapter.changeShapeColorAt(currentShapePosition, ResourceValidation.getShapeColor(requireContext(), it))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        super.initView()
        adapter.doubleClickListener = {
            updateShapeColor(it)
        }
        rlMainView.setOnTouchListener { _, p1 ->
            val point = PointF(p1.x, p1.y)
            when (p1.action) {
                MotionEvent.ACTION_DOWN -> {
                    adapter.addNewShape(type, point, "")
                    updateShapeColor(adapter.getLastIndex())
                }
                else -> {

                }
            }
            false
        }
    }

    private fun updateShapeColor(index: Int) {
        currentShapePosition = index
        if (DriverUtils.isNetworkEnabled(requireContext())) {
            viewModel.getShapeColor.value = ShapeType.CIRCLE.value
        } else {
            adapter.changeShapeColorAt(currentShapePosition, requireContext().getColorRandom())
        }
    }
}