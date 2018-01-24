package watson.love.mathilda.calendar

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min


class MainActivity : AppCompatActivity(), ScaleGestureDetector.OnScaleGestureListener {

    private val scaleSize = ScaleSize()

    override fun onScaleBegin(p0: ScaleGestureDetector?): Boolean {
        println("onScaleBegin -> ${p0?.scaleFactor}")
        return true
    }

    override fun onScaleEnd(p0: ScaleGestureDetector?) {
        println("onScaleEnd -> ${p0?.scaleFactor}")
        val scale = p0?.scaleFactor as Float
        if (scale > 1.2) {
            scaleSize.bigger()
        } else if (scale < 0.8) {
            scaleSize.smaller()
        }
        recyclerView.adapter.notifyDataSetChanged()
    }

    override fun onScale(p0: ScaleGestureDetector?): Boolean {
        println("onScale -> ${p0?.scaleFactor}")
        return false
    }

    private val list = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18")

    private lateinit var mGestureDetector: GestureDetector
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    class ScaleSize() {

        var current: Int = 0
        private val maxSize = 3;
        private val minSize = -3;

        fun bigger() {
            current += 1
            if (current >= maxSize) {
                current = maxSize
            }
        }

        fun smaller() {
            current -= 1
            if (current <= minSize) {
                current = minSize
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scaleGestureDetector = ScaleGestureDetector(this, this)

        button.setOnClickListener {
            if (calendar.visibility == View.VISIBLE) {
                calendar.visibility = View.GONE
                val params = collapsingToolbar.layoutParams as AppBarLayout.LayoutParams
                params.scrollFlags = 0 // list other flags here by |
                collapsingToolbar.layoutParams = params
            } else {
                calendar.visibility = View.VISIBLE
                val params = collapsingToolbar.layoutParams as AppBarLayout.LayoutParams
                params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL // list other flags here by |
                params.scrollFlags += AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED // list other flags here by |
                collapsingToolbar.layoutParams = params

            }
        }

        recyclerView.adapter = ItemAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
//                println("stat -> $state.name")
                if (state == State.COLLAPSED) {
                    calendar.visibility = View.GONE
                }
            }
        })

        mGestureDetector = GestureDetector(GestureListener())
    }

    private var initHeight: Int = 0


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    private val ids = intArrayOf(R.layout.item_b3, R.layout.item_b2, R.layout.item_b1, R.layout.item,
            R.layout.item_1, R.layout.item_2, R.layout.item_3)

    inner class ItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(this@MainActivity).inflate(ids[scaleSize.current + 3], parent, false)
            return ItemViewHolder(view!!)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            val itemVH = holder as ItemViewHolder
            itemVH.text.text = list.get(position)
        }

    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        public val text: TextView = view.findViewById(R.id.text)

    }

    enum class State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }


    abstract inner class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {

        private var mCurrentState = State.IDLE


        override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
            if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                mCurrentState = State.EXPANDED
            } else if (Math.abs(i) >= appBarLayout.totalScrollRange) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                mCurrentState = State.COLLAPSED
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                mCurrentState = State.IDLE
            }
        }

        abstract fun onStateChanged(appBarLayout: AppBarLayout, state: State)
    }

    private inner class GestureListener : GestureDetector.OnGestureListener {

        // 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发
        override fun onDown(e: MotionEvent): Boolean {
            Toast.makeText(this@MainActivity, "onDown", Toast.LENGTH_SHORT).show()
            return false
        }

        /*
         * 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
         * 注意和onDown()的区别，强调的是没有松开或者拖动的状态
         *
         * 而onDown也是由一个MotionEventACTION_DOWN触发的，但是他没有任何限制，
         * 也就是说当用户点击的时候，首先MotionEventACTION_DOWN，onDown就会执行，
         * 如果在按下的瞬间没有松开或者是拖动的时候onShowPress就会执行，如果是按下的时间超过瞬间
         * （这块我也不太清楚瞬间的时间差是多少，一般情况下都会执行onShowPress），拖动了，就不执行onShowPress。
         */
        override fun onShowPress(e: MotionEvent) {
            Toast.makeText(this@MainActivity, "onShowPress", Toast.LENGTH_SHORT).show()
        }

        // 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
        ///轻击一下屏幕，立刻抬起来，才会有这个触发
        //从名子也可以看出,一次单独的轻击抬起操作,当然,如果除了Down以外还有其它操作,那就不再算是Single操作了,所以这个事件 就不再响应
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            Toast.makeText(this@MainActivity, "onSingleTapUp", Toast.LENGTH_SHORT).show()
            return true
        }

        // 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
        override fun onScroll(e1: MotionEvent, e2: MotionEvent,
                              distanceX: Float, distanceY: Float): Boolean {
            Toast.makeText(this@MainActivity, "onScroll", Toast.LENGTH_LONG).show()

            return true
        }

        // 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
        override fun onLongPress(e: MotionEvent) {
            Toast.makeText(this@MainActivity, "onLongPress", Toast.LENGTH_LONG).show()
        }

        // 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                             velocityY: Float): Boolean {
            Toast.makeText(this@MainActivity, "onFling", Toast.LENGTH_LONG).show()
            return true
        }
    };
}
