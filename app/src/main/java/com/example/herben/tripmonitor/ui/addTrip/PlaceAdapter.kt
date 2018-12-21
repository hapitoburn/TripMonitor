package com.example.herben.tripmonitor.ui.addTrip

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.databinding.ItemTripPlaceBinding
import com.example.herben.tripmonitor.ui.trip.TripOverviewAdapter
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import kotlinx.android.synthetic.main.item_trip_place.view.*

class PlaceAdapter (listener: PlaceAdapterListener)  : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    private var placeAdapterListener = listener
    private var list: ArrayList<String>? = null

    interface ListItemClickListener {
        fun onListItemClick(place: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val view = ItemTripPlaceBinding.inflate(inflater)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip = list?.get(position)
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
        return if (this.list != null) this.list!!.size else 0
    }

    inner class ViewHolder(val binding: ItemTripPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        val upButton: View = itemView.findViewById(R.id.up_button)
        val downButton: View = itemView.findViewById(R.id.down_button)
        val addButton: View = itemView.findViewById(R.id.add_button)
        val removeButton: View = itemView.findViewById(R.id.delete_button)

        init {
            addButton.setOnClickListener(insert())
            removeButton.setOnClickListener(remove())
            upButton.setOnClickListener(moveUp())
            downButton.setOnClickListener(moveDown())
        }

        private fun insert(): (View) -> Unit = {
            layoutPosition.also { currentPosition ->
                placeAdapterListener.addPlaceAtPosition(currentPosition)
            }
        }

        private fun remove(): (View) -> Unit = {
            layoutPosition.also { currentPosition ->
                placeAdapterListener.removePlaceAtPosition(currentPosition)
            }
        }

        private fun moveUp(): (View) -> Unit = { _ ->
            layoutPosition.takeIf { it > 0 }?.also { currentPosition ->
                list?.removeAt(currentPosition).also {
                    list?.add(currentPosition - 1, it!!)
                }
                notifyItemMoved(currentPosition, currentPosition - 1)
            }
        }

        private fun moveDown(): (View) -> Unit = { _ ->
            layoutPosition.takeIf { it < list!!.size - 1 }?.also { currentPosition ->
                list?.removeAt(currentPosition).also {
                    list?.add(currentPosition + 1, it!!)
                }
                notifyItemMoved(currentPosition, currentPosition + 1)
            }
        }
        fun bind(item: String?) {
            with(binding) {
                name.text = item
                Log.i("TOM", "text binding sth wrong? $item")
            }
        }

        //        @Override
        //        public void onClick(View v) {
        //            int clickedPosition = getAdapterPosition();
        //            mOnClickListener.onListItemClick(clickedPosition);
        //        }
    }

    fun replaceData(entries: ArrayList<String>) {
        setList(entries)
    }

    private fun setList(entries: ArrayList<String>) {
        list = entries
        notifyDataSetChanged()
    }
}

@BindingAdapter("app:items")
fun RecyclerView.set(places: List<String>) {
    if(this.adapter is PlaceAdapter) {
        val ad = adapter as PlaceAdapter
        ad.replaceData(ArrayList(places))
    }
    else if(this.adapter is TripOverviewAdapter) {
        val ad = adapter as TripOverviewAdapter
        ad.replaceData(ArrayList(places))
    }
}
