package com.example.chatapplication
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class UserAdapter(val context: Context, private val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.textName.text = currentUser.userName
        Glide.with(context).load(currentUser.profileImage).placeholder(R.mipmap.ic_launcher_round).into(holder.imgUser)
       // Glide.with(context).load(currentUser.userImage)

        holder.itemView.setOnClickListener{
            val intent = Intent(context,ChatActivity::class.java)

            intent.putExtra("userName",currentUser.userName)
            intent.putExtra("userId", currentUser.userId)

            context.startActivity(intent)
        }

    }

    override fun getItemCount():Int {
        return userList.size
    }
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val textName:TextView = itemView.findViewById(R.id.txt_name)
       // val textTemp:TextView = itemView.findViewById(R.id.tmp)
        val imgUser:ImageView = itemView.findViewById(R.id.userImage)
      //  val layoutUser: LinearLayout = itemView.findViewById(R.id.layoutUser)
    }


    }






