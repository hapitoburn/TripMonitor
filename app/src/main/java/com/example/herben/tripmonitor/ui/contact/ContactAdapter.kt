package com.example.herben.tripmonitor.ui.contact

import android.databinding.BindingAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.herben.tripmonitor.R
import com.example.herben.tripmonitor.common.Injection
import com.example.herben.tripmonitor.data.Repository
import com.example.herben.tripmonitor.data.User
import com.example.herben.tripmonitor.databinding.ItemContactsBinding

class ContactAdapter (val listener: ContactAdapterListener)  : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    private var list: ArrayList<User>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val view = ItemContactsBinding.inflate(inflater)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list?.get(position)
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return if (this.list != null) this.list!!.size else 0
    }

    inner class ViewHolder(val binding: ItemContactsBinding) : RecyclerView.ViewHolder(binding.root) {
        val removeButton: View = itemView.findViewById(R.id.delete_button)

        init {
            if(listener.isLeader()){
                removeButton.visibility = android.view.View.VISIBLE
                removeButton.setOnClickListener(remove())
            }
            else{
                removeButton.visibility = android.view.View.INVISIBLE
            }
        }

        private fun remove(): (View) -> Unit = {
            layoutPosition.also { currentPosition ->
                listener.remove(currentPosition)
            }
        }


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

    fun replaceData(entries: ArrayList<User>) {
        setList(entries)
    }

    private fun setList(entries: ArrayList<User>) {
        list = entries
        notifyDataSetChanged()
    }
}

@BindingAdapter("app:items")
fun RecyclerView.set(users: List<User>) {
    if(this.adapter is ContactAdapter) {
        val ad = adapter as ContactAdapter
        ad.replaceData(ArrayList(users))
    }
}
