package com.example.crudoperationfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.crudoperationfirebase.Model.PersonDataModel
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

        btnUpdateData.setOnClickListener {
            updateData()
        }

        btnRetrieveData.setOnClickListener {
            retrieveData()
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

    private fun updateData() = CoroutineScope(Dispatchers.Main).launch {
        try {
            personData.addSnapshotListener { QuerySnapshot, FirebaseFirestoreException ->
                FirebaseFirestoreException?.let {
                    Toast.makeText(this@MainActivity, "Successfully Updated", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                QuerySnapshot?.let {
                    var sb = StringBuilder()
                    for (documents in it!!) {
                        var user = documents.toObject<PersonDataModel>()
                        sb.append("$user")
                    }
//                    edtName?.setText("$sb")
//                    edtProfile?.setText("$sb")
//                    edtHobby?.setText("$sb")
//                    edtLocation?.setText("$sb")

                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun retrieveData() = CoroutineScope(Dispatchers.Main).launch {
        try {
            val querySnapshot = personData.get().await()
            val sb = StringBuilder()
            for (document in querySnapshot.documents)
            {
                val person = document.toObject<PersonDataModel>()
                sb.append("$person\n")
            }
            withContext(Dispatchers.Main){
                textResult.text = sb.toString()
            }
        }
        catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun data(personDataModel: PersonDataModel) = CoroutineScope(Dispatchers.IO).launch{
        try {
            personData.add(personDataModel).await()
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, "Successfully saved", Toast.LENGTH_SHORT).show()
            }

        }
        catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
