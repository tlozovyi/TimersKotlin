package com.levor.timerskotlin

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.levor.timerskotlin.utils.BitmapUtils
import java.util.*
import java.util.concurrent.TimeUnit

class TimersAdapter(context: Context, var elements: List<Event>) : BaseAdapter() {

    private var time: GregorianCalendar = GregorianCalendar()
    private var updateImage = BooleanArray(elements.size, { i -> false})

    init {
        val h = Handler()
        val delay = 1000L //milliseconds

        h.postDelayed(object : Runnable {
            override fun run() {
                time.time = Date()
                this@TimersAdapter.notifyDataSetChanged()
                h.postDelayed(this, delay)
            }
        }, delay)
    }

    private val inflater = LayoutInflater.from(context)

    override fun getCount() = elements.size
    override fun getItem(position: Int) = elements[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, v: View?, parent: ViewGroup): View {
        var view = v
        val holder: ViewHolder
        if (view != null) {
            holder = view.tag as ViewHolder
        } else {
            view = inflater.inflate(R.layout.timers_list_item, parent, false)
            holder = ViewHolder(view)
            view!!.tag = holder
        }

        var timeLeft = (elements[position].endDate - time.timeInMillis) / 1000L
        val days : Long = TimeUnit.SECONDS.toDays(timeLeft)
        timeLeft -= TimeUnit.DAYS.toSeconds(days)
        val hours : Long = TimeUnit.SECONDS.toHours(timeLeft)
        timeLeft -= TimeUnit.HOURS.toSeconds(hours)
        val minutes : Long = TimeUnit.SECONDS.toMinutes(timeLeft)
        val seconds : Long = timeLeft - TimeUnit.MINUTES.toSeconds(minutes)

        val sb: StringBuilder = StringBuilder()

        if (seconds <= 0L && minutes <= 0 && hours <= 0 && days <= 0) {
            sb.append("Done")
        } else {
            if (days > 0) sb.append(days).append(" d, ")
            if (hours > 0) sb.append(String.format("%02d h, ", hours))
            if (minutes > 0) sb.append(String.format("%02d min, ", minutes))
            sb.append(String.format("%02d sec", seconds))
        }

        holder.name.text = elements[position].title
        holder.timer.text = sb.toString()
        if (!updateImage[position]) {
            BitmapUtils.loadBitmap(elements[position].imagePath, 40, holder.image)
            updateImage[position] = true
        }

        return view
    }

    class ViewHolder(view: View) {
        var name: TextView
        var timer: TextView
        var image: ImageView

        init {
            name = view.findViewById(R.id.timerName) as TextView
            timer = view.findViewById(R.id.timerValue) as TextView
            image = view.findViewById(R.id.timerImage) as ImageView
        }
    }
}
