package com.co.challengeliv3ly.views.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.co.challengeliv3ly.R
import com.co.challengeliv3ly.app.AppActivity
import com.co.challengeliv3ly.extensions.*
import com.co.challengeliv3ly.models.ShapeModel
import com.co.challengeliv3ly.utils.DoubleClickListener
import com.co.challengeliv3ly.utils.ResourceValidation
import kotlinx.android.synthetic.main.shape_item.view.*

class ShapeAdapter(
    private val context: Context,
    private val shapeView: RelativeLayout,
    private val listShape: MutableList<ShapeModel> = mutableListOf<ShapeModel>(),
    var doubleClickListener: ((Int) -> Unit)? = null
) {
    private val layoutInflater by lazy { context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater }

    fun notifyDataSetChanged() {
        clearAllView()
        listShape.forEachIndexed { index, shapeModel ->
            addShape(index, shapeModel)
        }
    }

    fun notifyDataChangedAt(index: Int) {
        if (shapeView.childCount > 0) {
            initShape(
                shapeView.getChildAt(index),
                listShape[index]
            )
            shapeView.invalidate()
        }
    }

    fun changeShapeColorAt(index: Int, color: String) {
        listShape[index].color = color
        notifyDataChangedAt(index)
    }

    private fun addShape(index: Int, shape: ShapeModel) {
        val viewItem: View = layoutInflater.inflate(R.layout.shape_item, null)
        val size = shape.size
        viewItem.x = shape.xPosition - size / 2
        viewItem.y = shape.yPosition - size / 2
        val params: ViewGroup.LayoutParams =
            ViewGroup.LayoutParams(size, size)
        viewItem.layoutParams = params
        viewItem.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick() {
                viewItem.pbLoading.show()
                doubleClickListener?.invoke(index)
            }
        })
        initShape(viewItem, shape)
        shapeView.addView(viewItem)
    }

    private fun initShape(viewItem: View, shape: ShapeModel) {
        viewItem.apply {
            ivShape.setImageResource(ResourceValidation.getImageResourceAccordingWithShapeType(shape.type))
            if (shape.color.isNullOrEmpty()) {
                pbLoading.show()
            } else {
                if (ResourceValidation.isColorIsLink(shape.color)) {
                    ivShape.clearColorFilter()
                    Glide.with(context).load(shape.color).placeholder(R.drawable.bg_squares).into(ivShape)
                } else {
                    ivShape.setColorFilter(
                        Color.parseColor(shape.color),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
                pbLoading.hide()
            }
        }
    }

    private fun clearAllView() {
        if (shapeView.childCount > 0) {
            shapeView.removeAllViews()
        }
    }

    fun addNewShape(type: Int, point: PointF, color: String) {
        val shape = ShapeModel(
            type,
            color,
            point.x,
            point.y,
            (context as AppActivity<*>).getDeviceWidthIn(
                AppConst.SHAPE.RANGE_SIZE_PERCENT
            )
        )
        listShape.add(shape)
        addShape(getLastIndex(), shape)
    }

    fun getCount(): Int {
        return listShape.size
    }

    fun getItems() : List<ShapeModel> {
        return listShape
    }

    fun getLastIndex(): Int {
        return listShape.size - 1
    }
}