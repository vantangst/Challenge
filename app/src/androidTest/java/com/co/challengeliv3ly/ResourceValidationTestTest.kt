package com.co.challengeliv3ly

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.co.challengeliv3ly.models.ShapeColorModel
import com.co.challengeliv3ly.models.ShapeType
import com.co.challengeliv3ly.utils.ResourceValidation
import org.junit.Test
import com.google.common.truth.Truth.*
import org.junit.Before


class ResourceValidationTestTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun colorTextIsLink_returnTrue() {
        val result = ResourceValidation.isColorIsLink("http://test/image.jpg")
        assertThat(result).isTrue()
    }

    @Test
    fun colorTextIsLink_returnFalse() {
        val result = ResourceValidation.isColorIsLink("#ffffff")
        assertThat(result).isFalse()
    }

    @Test
    fun getImageResourceAccordingWithShapeType_returnCircle() {
        val result = ResourceValidation.getImageResourceAccordingWithShapeType(ShapeType.CIRCLE.value)
        assertThat(result).isEqualTo(R.drawable.bg_circle_gray)
    }

    @Test
    fun getImageResourceAccordingWithShapeType_returnSquare() {
        val result = ResourceValidation.getImageResourceAccordingWithShapeType(ShapeType.SQUARE.value)
        assertThat(result).isEqualTo(R.drawable.bg_squares)
    }

    @Test
    fun getImageResourceAccordingWithShapeType_returnTriangle() {
        val result = ResourceValidation.getImageResourceAccordingWithShapeType(ShapeType.TRIANGLE.value)
        assertThat(result).isEqualTo(R.drawable.ic_triangle)
    }

    @Test
    fun isColorResponseListValid_returnTrue() {
        val result = ResourceValidation.isColorResponseListValid(listOf(ShapeColorModel("#ffffff", null)))
        assertThat(result).isTrue()
    }

    @Test
    fun isColorResponseListValid_returnFalse() {
        val result = ResourceValidation.isColorResponseListValid(listOf(ShapeColorModel("", null)))
        assertThat(result).isFalse()
    }

    @Test
    fun getShapeColorRandom_returnRandom() {
        val result = ResourceValidation.getShapeColor(context, null)
        assertThat(result).isNotEmpty()
    }

    @Test
    fun getShapeColor_returnFromServer() {
        val result = ResourceValidation.getShapeColor(context, listOf(ShapeColorModel("#ffffff", null)))
        assertThat(result).isNotEmpty()
    }

}