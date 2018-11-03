package com.example.herben.tripmonitor.ui.board

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.herben.tripmonitor.data.Post
import com.example.herben.tripmonitor.databinding.PostsListItemBinding;

class BoardAdapter(/*private val mOnClickListener: ListItemClickListener?*/) : RecyclerView.Adapter<BoardAdapter.PostsViewHolder>() {

    private var posts: List<Post>? = null

    interface ListItemClickListener {
        fun onListItemClick(post: Post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val view = PostsListItemBinding.inflate(inflater)

        return PostsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val post = posts!![position]
        /*
        val date = post.date

        val formatterDate = SimpleDateFormat("EEE dd MMM yyyy", Locale.US)
        val formatterTime = SimpleDateFormat("hh:mm aa", Locale.US)
        val dateFormatted = formatterDate.format(date)
        val timeFormatted = formatterTime.format(date)*/

        holder.bind(post)

        /*holder.itemView.setOnClickListener {
            mOnClickListener?.onListItemClick(post)
        }*/
    }

    override fun getItemCount(): Int {
        return if (this.posts != null) this.posts!!.size else 0
    }

    inner class PostsViewHolder(val binding: PostsListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Post) {
            with(binding) {
                title.text = item.title
                editDate.text = item.date.toString()
                //created.text = item.author
                body.text = item.body
            }
        }
/*
        init {
            title = itemView.findViewById(R.id.title)
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

    fun replaceData(entries: List<Post>) {
        setList(entries)
    }

    private fun setList(entries: List<Post>) {
        posts = entries
        notifyDataSetChanged()
    }
}

