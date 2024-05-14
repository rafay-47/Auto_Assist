package com.project.auto_assist

import android.content.Context
import android.os.Parcelable
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import java.util.*

class InfinitePagerAdapter(private val context: Context, private val images: List<Int>) : PagerAdapter() {
    private var viewPager: ViewPager? = null
    private var inflater: LayoutInflater? = null
    private var actualCount = 0

    init {
        actualCount = if (images.isNotEmpty()) images.size else 0
    }

    override fun getCount(): Int {
        // Set count to infinite if there are images
        return if (actualCount > 0) Integer.MAX_VALUE else 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (inflater == null) {
            inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        val itemView: View = inflater!!.inflate(R.layout.item_image, container, false)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val imageResource = images[position % actualCount]
        imageView.setImageResource(imageResource)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

    override fun startUpdate(container: ViewGroup) {
        viewPager = container as ViewPager
    }

    override fun finishUpdate(container: ViewGroup) {
        viewPager = null
    }

    fun setCurrentItem(position: Int) {
        viewPager?.setCurrentItem(position, false)
    }
}
