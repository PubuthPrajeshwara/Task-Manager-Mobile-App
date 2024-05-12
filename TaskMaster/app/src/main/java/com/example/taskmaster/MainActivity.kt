package com.example.taskmaster;

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.taskmaster.Adapter.ToDoAdapter
import com.example.taskmaster.Model.ToDoModel
import com.example.taskmaster.Utils.DatabaseHandler
import java.util.*

class MainActivity : AppCompatActivity(), DialogCloseListener {

    private lateinit var db: DatabaseHandler
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var tasksAdapter: ToDoAdapter
    private lateinit var fab: FloatingActionButton
    private var taskList: MutableList<ToDoModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        db = DatabaseHandler(this)
        db.openDatabase()

        val newTaskText = findViewById<EditText>(R.id.newTaskText)
        newTaskText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tasksAdapter.filterTasks(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        tasksRecyclerView = findViewById(R.id.taskRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        tasksAdapter = ToDoAdapter(db, this)
        tasksRecyclerView.adapter = tasksAdapter

        val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(tasksAdapter))
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            val dialog = AddNewTask()
            dialog.show(supportFragmentManager, AddNewTask.TAG)
        }

        loadTasks()
    }

    private fun loadTasks() {
        taskList.clear() // Clear existing task list
        taskList.addAll(db.allTasks) // Retrieve tasks from the database
        taskList.reverse() // Reverse the order of tasks (optional)
        tasksAdapter.setTasks(taskList) // Update RecyclerView with the new task list
    }

    override fun handleDialogClose(dialog: DialogInterface?) {
        loadTasks() // Reload tasks when dialog is closed
        tasksAdapter.notifyDataSetChanged() // Notify RecyclerView adapter of data change
    }
}
