package com.laotang.quickdev.libstore.mvvm.model.repository

import com.laotang.quickdev.libstore.mvvm.model.entity.User
import com.laotang.quickdev.libstore.mvvm.model.service.UserService
import com.laotang.quickdevcore.utils.RxPreferences
import com.laotang.quickdevcore.utils.getObject
import com.laotang.quickext.ioToUI
import io.reactivex.Observable
import kotlin.random.Random

class UserRepository(private val userService: UserService) :
    UserService {

    override fun getUsers(lastIdQueried: Int, perPage: Int): Observable<MutableList<User>> {
        return userService.getUsers(lastIdQueried, perPage)
            .ioToUI()
            .map {
                if (it.isNotEmpty()) {
                    val random = Random(System.currentTimeMillis()).nextInt(it.size)
                    UserState.saveUser(
                        it[random]
                    )
                }
                return@map it
            }
    }

    fun getUser(): User? {
        return UserState.getUser()
    }

    private object UserState {
        private var lock: Any = Any()
        private var user: User? = null

        internal fun saveUser(user: User) {
            synchronized(lock) {
                RxPreferences.getObject<User>("user").set(user)
                this.user = user
            }
        }

        internal fun getUser(): User? {
            if (user == null) {
                user = RxPreferences.getObject<User>("user").get()
            }
            return user
        }
    }
}