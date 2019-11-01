package com.kiyosuke.cipherroom

import android.app.Activity
import android.os.Bundle
import com.kiyosuke.cipherroom.db.dao.UserDao
import com.kiyosuke.cipherroom.db.entity.UserEntity
import kotlinx.android.synthetic.main.activity_user_add.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserAddActivity : CoroutinesActivity() {

    private val userDao: UserDao by lazy { App.db.userDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_add)

        buttonSave.setOnClickListener {
            saveUser()
        }
    }

    private fun saveUser() = launch {
        val name = editUserName.text.toString()
        val age = editUserAge.text.toString().toInt()
        val user = UserEntity(name = name, age = age)
        withContext(Dispatchers.IO) {
            userDao.insert(user)
        }
        setResult(Activity.RESULT_OK)
        finish()
    }
}
