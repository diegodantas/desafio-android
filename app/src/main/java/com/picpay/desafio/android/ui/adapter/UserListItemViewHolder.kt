package com.picpay.desafio.android.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.picpay.desafio.android.R
import com.picpay.desafio.android.databinding.ListItemUserBinding
import com.picpay.desafio.android.data.model.User
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class UserListItemViewHolder(
    private val binding: ListItemUserBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.run {
            user.name?.let {
                name.text = it
            }

            user.username?.let {
                username.text = it
            }
            user.img?.let {
                progressBar.visibility = View.VISIBLE
                Picasso.get()
                    .load(user.img)
                    .error(R.drawable.ic_round_account_circle)
                    .into(picture, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            progressBar.visibility = View.GONE
                        }
                    })
            }
        }
    }
}