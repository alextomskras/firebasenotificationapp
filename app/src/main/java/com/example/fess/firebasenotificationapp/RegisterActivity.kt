package com.example.fess.firebasenotificationapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    internal lateinit var email: EditText
    internal lateinit var password: EditText
    internal lateinit var name: EditText
    internal lateinit var submit: Button
    internal lateinit var login: Button
    internal lateinit var image: CircleImageView
    private var uri: Uri? = null
    internal lateinit var progressBar: ProgressBar


    internal lateinit var auth: FirebaseAuth
    internal lateinit var StorageReference: StorageReference
    internal lateinit var StorageReference1: com.google.firebase.storage.StorageReference
    internal lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().reference.child("Users")
        StorageReference = FirebaseStorage.getInstance().reference.child("images")
        StorageReference1 = FirebaseStorage.getInstance().reference.child("images")



        uri = null
        progressBar = findViewById(R.id.registerProgressBar)
        email = findViewById(R.id.email)
        name = findViewById(R.id.name)
        password = findViewById(R.id.password)
        submit = findViewById(R.id.submit)
        login = findViewById(R.id.login)
        image = findViewById(R.id.image_profile)
        submit.setOnClickListener(this)
        login.setOnClickListener(this)
        image.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.submit -> Register()

            R.id.login -> finish()

            R.id.image_profile -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Selected Picture"), pick_image)
            }
        }
    }

    private fun Register() {


        val user_name = name.text.toString()
        Log.d(TAG, "user_name$user_name")
        val EmailTxt = email.text.toString()
        Log.d(TAG, "user_EmailTxt$EmailTxt")
        val PassTxt = password.text.toString()

        if (uri != null) {

            progressBar.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(EmailTxt) && !TextUtils.isEmpty(PassTxt)) {
                auth.createUserWithEmailAndPassword(EmailTxt, PassTxt).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user_id = auth.currentUser!!.uid
                        Log.d(TAG, "user_user_id-$user_id")
                        val user_profile = StorageReference.child("$user_id.png")
                        Log.d(TAG, "user_user_profile-$user_profile")


                        user_profile.putFile(uri!!).addOnCompleteListener { uploadtask ->
                            uploadtask.addOnSuccessListener { taskSnapshot ->

                                user_profile.downloadUrl.addOnSuccessListener {
                                    Log.d(TAG, "File location: $it")
                                    val downloadUrl1 = it.toString()

                                    //                                            Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                                    //                                            StorageReference user_profile2 = StorageReference.;
                                    val downloadUrl = taskSnapshot.uploadSessionUri

                                    Log.d(TAG, "user_downloadUrl-" + downloadUrl!!.toString())
                                    val image_uri = downloadUrl1
                                    Log.d(TAG, "user_image_uri-$image_uri")
                                    val token_id = FirebaseInstanceId.getInstance().token
                                    val user_data = database.child(user_id)
                                    user_data.child("name").setValue(user_name)
                                    user_data.child("user_image").setValue(image_uri)
                                    user_data.child("token_id").setValue(token_id)
                                    progressBar.visibility = View.INVISIBLE
                                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                                    finishAffinity()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this@RegisterActivity, "Image not Uploaded", Toast.LENGTH_LONG).show()
                                progressBar.visibility = View.INVISIBLE
                            }
                        }

                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration Error", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.INVISIBLE
                    }
                }
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pick_image) {
            uri = data!!.data
            image.setImageURI(uri)
        }


    }

    companion object {
        val pick_image = 1
        val TAG = "Profile_user-name"
    }
}
