package com.tutorial.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tutorial.roomdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val employeeDao = (application as EmployeeApp).db.employeeDao()
        binding?.btnAdd?.setOnClickListener {
            // TODO call addrecord with employeeDao
            addRecord(employeeDao)
        }
        lifecycleScope.launch{
            employeeDao.fetchAllEmployees().collect{
                Log.d("exactemployee","$it")
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list,employeeDao)
            }
        }
    }
    fun addRecord(employeeDao: EmployeeDao){
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmailId?.text.toString()
        if(email.isNotEmpty() && name.isNotEmpty()){
            lifecycleScope.launch{
                employeeDao.insert(EmployeeEntity(name = name, email = email))
                Toast.makeText(applicationContext,"Record saved",Toast.LENGTH_LONG)
                binding?.etName?.text?.clear()
                binding?.etEmailId?.text?.clear()
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Name or Email cannot be blank",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    /**
     * Function is used show the list of inserted data.
     */
    private fun setupListOfDataIntoRecyclerView(employeesList:ArrayList<EmployeeEntity>,
                                                employeeDao: EmployeeDao) {

        if (employeesList.isNotEmpty()) {
            val itemAdapter = ItemAdapter(employeesList)


            // Set the LayoutManager that this RecyclerView will use.
            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            // adapter instance is set to the recyclerview to inflate the items.
            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
        } else {

            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }
}