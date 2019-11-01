package com.kiyosuke.cipherroom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kiyosuke.cipherroom.db.entity.UserEntity

class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val users: MutableList<UserEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(root)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users.getOrNull(position) ?: return
        holder.textUserName.text = user.name
        holder.textUserAge.text = "${user.age}歳"
    }

    fun update(users: List<UserEntity>) {
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textUserName: TextView = itemView.findViewById(R.id.textUserName)
        val textUserAge: TextView = itemView.findViewById(R.id.textUserAge)
    }
}