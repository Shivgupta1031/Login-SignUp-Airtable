package com.devshiv.airtableloginapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import carbon.dialog.ProgressDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.devshiv.airtableloginapp.Constants.Companion.TAG
import com.devshiv.airtableloginapp.databinding.ActivitySignUpBinding
import org.json.JSONArray
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this)
        dialog?.setText("Please Wait....")

        binding.alreadyHaveAnAccountTxt.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signUpBtn.setOnClickListener {
            dialog?.show()
            if (validateData()) {
                signUpUser()
            } else {
                dialog?.dismiss()
            }
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun signUpUser() {
        dialog?.show()

        val records: JSONObject = JSONObject()
        val array: JSONArray = JSONArray()
        try {
            val data: JSONObject = JSONObject()
            val fields: JSONObject = JSONObject()
            fields.put("Name", binding.usernameET.text.toString())
            fields.put("Phone Number", binding.phoneNumberET.text.toString())
            fields.put("Password", binding.passwordET.text.toString())
            data.put("fields", fields)
            array.put(data)
            records.put("records", array)
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "signUpUser: ${e.message}")
        }

        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer ${Constants.api_access_token}"
        headers["Content-Type"] = "application/json"

        AndroidNetworking.post(Constants.insert_url)
            .addHeaders(headers)
            .addJSONObjectBody(records)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if (response == null) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Couldn't Create Account",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val records = response.getJSONArray("records")
                        if (records.length() > 0) {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Account Created Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Couldn't Create Account",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    dialog?.dismiss()
                }

                override fun onError(anError: ANError?) {
                    Log.d(
                        TAG,
                        "onError: ${anError?.message} ${anError?.errorBody} ${anError?.response} ${anError?.errorDetail}"
                    )
                    dialog?.dismiss()
                    Toast.makeText(this@SignUpActivity, "Some Error Occurred", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    private fun validateData(): Boolean {
        if (binding.usernameET.text.isEmpty()) {
            binding.usernameET.error = "* Required"
            return false
        } else if (binding.passwordET.text.isEmpty()) {
            binding.passwordET.error = "* Required"
            return false
        } else if (binding.passwordET.text.length < 6) {
            binding.passwordET.error = "Password Should Be Of 6 Digits"
            return false
        } else if (binding.phoneNumberET.text.isEmpty()) {
            binding.phoneNumberET.error = "* Required"
            return false
        } else if (binding.phoneNumberET.text.length < 10) {
            binding.phoneNumberET.error = "Invalid Phone Number"
            return false
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}