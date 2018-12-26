package com.example.herben.tripmonitor.ui.addTrip

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.herben.tripmonitor.data.Trip
import com.example.herben.tripmonitor.data.User
import com.example.herben.tripmonitor.databinding.ItemTripUserBinding

class UserAdapter  : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var list: List<User>? = null

    interface ListItemClickListener {
        fun onListItemClick(users: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val view = ItemTripUserBinding.inflate(inflater)

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

    inner class ViewHolder(val binding: ItemTripUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: User?) {
            with(binding) {
                name.text = item?.name
                email.text = item?.email
                phone.text = item?.phoneNumber
            }
        }

        //        @Override
        //        public void onClick(View v) {
        //            int clickedPosition = getAdapterPosition();
        //            mOnClickListener.onListItemClick(clickedPosition);
        //        }
    }

    fun replaceData(entries: List<User>) {
        setList(entries)
    }

    private fun setList(entries: List<User>) {
        list = entries
        notifyDataSetChanged()
    }
}
@BindingAdapter("app:userAdapterItems")
fun RecyclerView.set(users: List<User>) {
    val ad = this.adapter as UserAdapter
    ad.replaceData(users)
}

