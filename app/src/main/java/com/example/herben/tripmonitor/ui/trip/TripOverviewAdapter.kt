package com.example.herben.tripmonitor.ui.trip

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.databinding.StringItemBinding

class TripOverviewAdapter(/*private val mOnClickListener: ListItemClickListener?*/) : RecyclerView.Adapter<TripOverviewAdapter.TripViewHolder>() {

    private var trip: Trip? = Trip()

    interface ListItemClickListener {
        fun onListItemClick(post: Post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val view = StringItemBinding.inflate(inflater)

        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val place = trip?.places?.get(position)

        holder.bind(place)

        /*holder.itemView.setOnClickListener {
            mOnClickListener?.onListItemClick(post)
        }*/
    }

    override fun getItemCount(): Int {
        return if (trip != null) trip?.places?.size!! else 0
    }

    inner class TripViewHolder(val binding: StringItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(place: String?) {
            with(binding) {
                name.text = place
            }
        }

        //        @Override
        //        public void onClick(View v) {
        //            int clickedPosition = getAdapterPosition();
        //            mOnClickListener.onListItemClick(clickedPosition);
        //        }
    }

    fun replaceData(places: List<String>) {
        trip?.places = places
        notifyDataSetChanged()
    }
}