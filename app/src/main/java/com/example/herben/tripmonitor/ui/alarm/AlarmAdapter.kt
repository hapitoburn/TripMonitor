package com.example.herben.tripmonitor.ui.alarm

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.data.Alarm
import com.example.herben.tripmonitor.databinding.ItemAlarmBinding

class AlarmAdapter(val listener: AlarmListener) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    private var alarms : List<Alarm> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val view = ItemAlarmBinding.inflate(inflater)

        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val place = alarms[position]

        holder.bind(place)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    inner class AlarmViewHolder(val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        private val addButton: View = itemView.findViewById(R.id.add_button)

        init {
            addButton.setOnClickListener(add())
        }

        private fun add(): (View) -> Unit = {
            layoutPosition.also { currentPosition ->
                listener.add(currentPosition)
            }
        }

        fun bind(info: Alarm?) {
            with(binding) {
                name.text = info?.name
                date.text = info?.date.toString()
            }
        }
    }

    fun replaceData(alarmList: List<Alarm>) {
        alarms = alarmList
        notifyDataSetChanged()
    }
}
@BindingAdapter("app:alarmItems")
fun RecyclerView.set(users: List<Alarm>) {
    if(this.adapter is AlarmAdapter) {
        val ad = adapter as AlarmAdapter
        ad.replaceData(ArrayList(users))
    }
}
