package com.devshiv.airtableloginapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import carbon.dialog.ProgressDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.devshiv.airtableloginapp.Constants.Companion.TAG
import com.devshiv.airtableloginapp.databinding.ActivityLoginBinding
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityLoginBinding
    var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this)
        dialog?.setText("Please Wait....")

        binding.backBtn.setOnClickListener(this)
        binding.createNewAccountTxt.setOnClickListener(this)
        binding.loginBtn.setOnClickListener(this)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backBtn -> {
                onBackPressed()
            }

            R.id.createNewAccountTxt -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            R.id.loginBtn -> {
                if (binding.phoneNumberET.text.toString().isEmpty()) {
                    binding.phoneNumberET.error = "* Required"
                } else if (binding.phoneNumberET.text.toString().length < 10) {
                    binding.phoneNumberET.error = "Invalid Phone Number"
                } else if (binding.passwordET.text.toString().isEmpty()) {
                    binding.passwordET.error = "Invalid Password"
                } else if (binding.passwordET.text.toString().length < 4) {
                    binding.passwordET.error = "Very Short"
                } else {
                    loginUser()
                }
            }
        }
    }

    private fun loginUser() {
        dialog?.show()

        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer ${Constants.api_access_token}"
        headers["Content-Type"] = "application/json"

        AndroidNetworking.get(Constants.read_url)
            .addHeaders(headers)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if (response == null) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Couldn't Verify Details",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        try {
                            val records: JSONArray = response.getJSONArray("records")

                            var isFound: Boolean = false

                            if (records.length() > 0) {
                                for (i: Int in 0 until records.length()) {
                                    val fields = records.getJSONObject(i).getJSONObject("fields")

                                    if (fields.getString("Phone Number")
                                            .equals(binding.phoneNumberET.text.toString())
                                    ) {
                                        if (fields.getString("Password")
                                                .equals(binding.passwordET.text.toString())
                                        ) {
                                            isFound = true
                                        }
                                        break
                                    }
                                }

                                if (isFound) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Login Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Login Failed, Invalid Details",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "No User Found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "Error: ${e.message}")
                        }
                    }
                    dialog?.dismiss()
                }

                override fun onError(anError: ANError?) {
                    Log.d(
                        Constants.TAG,
                        "onError: ${anError?.message} ${anError?.errorBody} ${anError?.response} ${anError?.errorDetail}"
                    )
                    dialog?.dismiss()
                    Toast.makeText(this@LoginActivity, "Some Error Occurred", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }
}