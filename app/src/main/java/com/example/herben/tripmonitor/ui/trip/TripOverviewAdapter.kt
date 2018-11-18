package com.example.herben.tripmonitor.ui.trip

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.data.Trip
//import com.example.herben.tripmonitor.databinding.

class TripOverviewAdapter(/*private val mOnClickListener: ListItemClickListener?*/) : RecyclerView.Adapter<TripOverviewAdapter.TripViewHolder>() {

    private var trip: Trip? = null

    interface ListItemClickListener {
        fun onListItemClick(post: Post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val view = TripBinding.inflate(inflater)

        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trip?.places?.get(position)
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
        return if (trip != null) trip?.places?.size!! else 0
    }

    inner class TripViewHolder(val binding: PostsListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            with(binding) {
                title.text = item.title
                editDate.text = item.date.toString()
                //created.text = item.author
                body.text = item.body
            }
        }
/*
        init {
            name = itemView.findViewById(R.id.name)
            date = itemView.findViewById(R.id.edit_date)
            time = itemView.findViewById(R.id.time)
        }
*/
        //        @Override
        //        public void onClick(View v) {
        //            int clickedPosition = getAdapterPosition();
        //            mOnClickListener.onListItemClick(clickedPosition);
        //        }
    }

    fun replaceData(entry: Trip) {
        trip = entry
        notifyDataSetChanged()
    }
}