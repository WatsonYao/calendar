package watson.love.mathilda.calendar

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * Created by watson on 1/24 0024.
 */

class SampleTitleBehavior : CoordinatorLayout.Behavior<View> {
    // 列表顶部和title底部重合时，列表的滑动距离。
    private var deltaY: Float = 0.toFloat()

    constructor() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
        return dependency is RecyclerView
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: View?, dependency: View?): Boolean {
        if (deltaY == 0f) {
            deltaY = dependency!!.y - child!!.height
        }
        println("dependency.y -> ${dependency!!.y}")
        println("child.y -> ${child!!.height}")

        var dy = dependency!!.y - child!!.height
        dy = if (dy < 0) 0.0f else dy
        val y = -(dy / deltaY) * child.height
        child.translationY = y

        return true
    }
}