package com.example.crudoperationfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.crudoperationfirebase.Model.PersonDataModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    val personData = Firebase.firestore.collection("Data")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnRetrieveData.setOnClickListener {
            retrieveData()
        }
    }

    private fun retrieveData() = CoroutineScope(Dispatchers.Main).launch {
        try {
            val querySnapshot = personData.get().await()
            val sb = StringBuilder()
            for (document in querySnapshot.documents) {
                val person = document.toObject<PersonDataModel>()
                sb.append("$person\n")
            }
            withContext(Dispatchers.Main) {
                textResult.text = sb.toString()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        btnSubmit.setOnClickListener {
            val name = edtName.text.toString()
            val profile = edtProfile.text.toString()
            val hobby = edtHobby.text.toString()
            val location = edtLocation.text.toString()
            val personDataModel = PersonDataModel(
                personName = name,
                personProfile = profile,
                personHobbies = hobby,
                personLocation = location
            )
            data(personDataModel)
        }
    }

    private fun data(personDataModel: PersonDataModel) = CoroutineScope(Dispatchers.IO).launch {
        try {
            personData.add(personDataModel).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Successfully saved", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        btnUpdateData.setOnClickListener {
            val name = edtName.text.toString()
            val profile = edtProfile.text.toString()
            val hobby = edtHobby.text.toString()
            val location = edtLocation.text.toString()

            updateData(name, profile, hobby, location)

        }
    }

    private fun updateData(name: String, profile: String, hobby: String, location: String) {

        val personData = FirebaseDatabase.getInstance().getReference("Data")
        val person = mapOf<String, Any>(
            "name" to name,
            "profile" to profile,
            "hobby" to hobby,
            "location" to location
        )
        personData.child(name).updateChildren(person).addOnSuccessListener {
            Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(
                this,
                "Failed to Update",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
