package com.picpay.desafio.android.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.data.model.User

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchUsers() {
        _isLoading.value = true
        userRepository.getUsers { result ->
            result.onSuccess { users ->
                _users.postValue(users)
                _isLoading.postValue(false)
            }.onFailure { throwable ->
                _error.postValue(throwable.message)
                _isLoading.postValue(false)
            }
        }
    }
}