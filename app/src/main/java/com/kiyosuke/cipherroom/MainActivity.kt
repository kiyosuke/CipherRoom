package com.kiyosuke.cipherroom

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.kiyosuke.cipherroom.db.dao.UserDao
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : CoroutinesActivity() {

    private val userDao: UserDao by lazy { App.db.userDao() }

    private val adapter: UserAdapter = UserAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()

        updateUserList()

        fab.setOnClickListener {
            startAddUser()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun startAddUser() {
        val i = Intent(this, UserAddActivity::class.java)
        startActivityForResult(i, REQUEST_ADD)
    }

    private fun updateUserList() = launch {
        val users = withContext(Dispatchers.IO) { userDao.allUsers() }
        adapter.update(users)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ADD -> {
                if (resultCode == Activity.RESULT_OK) {
                    updateUserList()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_ADD = 1000
    }

}
