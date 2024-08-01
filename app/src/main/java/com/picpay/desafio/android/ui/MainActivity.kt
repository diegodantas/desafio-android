package com.picpay.desafio.android.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.picpay.desafio.android.R
import com.picpay.desafio.android.databinding.ActivityMainBinding
import com.picpay.desafio.android.ui.adapter.UserListAdapter
import com.picpay.desafio.android.ui.viewModel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModel()
    private lateinit var adapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userListProgressBar.visibility = View.VISIBLE

        initObservers()
        initRecyclerView()
    }

    private fun initObservers() {
        userViewModel.users.observe(this) { users ->
            adapter.users = users
        }

        userViewModel.isLoading.observe(this) { isLoading ->
            binding.userListProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        userViewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage ?: getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerView() =binding.run {
        adapter = UserListAdapter()
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        userViewModel.fetchUsers()
    }
}
