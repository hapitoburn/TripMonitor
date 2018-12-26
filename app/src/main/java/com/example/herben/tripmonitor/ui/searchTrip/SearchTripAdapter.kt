package com.example.herben.tripmonitor.ui.searchTrip

import android.databinding.BindingAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.databinding.ItemSearchTripsBinding
import kotlinx.android.synthetic.main.item_search_trips.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class SearchTripAdapter  : RecyclerView.Adapter<SearchTripAdapter.ViewHolder>() {

    private var trips: List<Trip>? = null

    interface ListItemClickListener {
        fun onListItemClick(trip: Trip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val view = ItemSearchTripsBinding.inflate(inflater)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip = trips?.get(position)
        /*
        val date = post.date

        val formatterDate = SimpleDateFormat("EEE dd MMM yyyy", Locale.US)
        val formatterTime = SimpleDateFormat("hh:mm aa", Locale.US)
        val dateFormatted = formatterDate.format(date)
        val timeFormatted = formatterTime.format(date)*/

        holder.bind(trip)

        /*holder.itemView.setOnClickListener {
            mOnClickListener?.onListItemClick(post)
        }*/
    }

    override fun getItemCount(): Int {
        return if (this.trips != null) this.trips!!.size else 0
    }

    inner class ViewHolder(val binding: ItemSearchTripsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Trip?) {
            with(binding) {
                name.text = item?.name
                dateFrom.text = SimpleDateFormat("dd/MM/yyy").format(item?.dateFrom)
                dateTo.text = SimpleDateFormat("dd/MM/yyy").format(item?.dateTo)
                leader.text = item?.leader
            }
        }

        //        @Override
        //        public void onClick(View v) {
        //            int clickedPosition = getAdapterPosition();
        //            mOnClickListener.onListItemClick(clickedPosition);
        //        }
    }

    fun replaceData(entries: List<Trip>) {
        setList(entries)
    }

    private fun setList(entries: List<Trip>) {
        trips = entries
        notifyDataSetChanged()
    }
}

@BindingAdapter("app:items")
fun RecyclerView.set(places: List<Trip>) {
    val ad = this.adapter as SearchTripAdapter
    ad.replaceData(places)
}

@BindingAdapter("android:onRefresh")
fun SwipeRefreshLayout.set(viewModel: SearchTripViewModel) {
    setOnRefreshListener {
        viewModel.loadEntries(true)
    }
}