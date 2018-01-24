package watson.love.mathilda.calendar

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.support.design.widget.AppBarLayout
import android.R.attr.name
import android.util.Log
import watson.love.mathilda.calendar.MainActivity.AppBarStateChangeListener
import watson.love.mathilda.calendar.R.id.appBarLayout


class MainActivity : AppCompatActivity() {

    private val list = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val animator = ObjectAnimator.ofFloat(recyclerView, "translationY", dpOrSpToPx(this, 244f))

        button.setOnClickListener {
            // 执行动画
//            animator.setDuration(320)
//            animator.start()
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
                println("stat -> $state.name")
                if (state == State.COLLAPSED) {
                    calendar.visibility = View.GONE
                }
            }
        })
    }

    fun dpOrSpToPx(context: Context, dpOrSpValue: Float): Float {
        return dpOrSpValue * context.getResources().getDisplayMetrics().density
    }

    inner class ItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.item, parent, false)
            return ItemViewHolder(view)
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
}
