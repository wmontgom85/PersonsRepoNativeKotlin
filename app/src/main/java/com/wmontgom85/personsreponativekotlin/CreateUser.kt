package com.wmontgom85.personsreponativekotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wmontgom85.personsreponativekotlin.func.px
import com.wmontgom85.personsreponativekotlin.func.throttleFirst
import com.wmontgom85.personsreponativekotlin.model.Person
import com.wmontgom85.personsreponativekotlin.repo.DBHelper

import kotlinx.android.synthetic.main.activity_create_user.toolbar
import kotlinx.android.synthetic.main.content_create_user.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CreateUser : AppCompatActivity(), CoroutineScope {
    private val NEWPERSONRESULT = 111

    private val CAMERA_SCAN = 1
    private val CAMERA_PERMISSION = 100
    private val READ_EXT_PERMISSION = 101
    private var person : Person? = Person()

    private val job by lazy { Job() }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        setSupportActionBar(toolbar)

        /*
        val cameraAction: (View) -> Unit = throttleFirst(350L, MainScope(), this::checkCameraPermission)
        select_avatar.setOnClickListener(cameraAction)

        val saveAction: (View) -> Unit = throttleFirst(350L, MainScope(), this::savePerson)
        save_button.setOnClickListener(saveAction)

        val personId = intent.getLongExtra("personId", 0)

        if (personId > 0) {
            // we're editing a person
            loading_person.visibility = View.VISIBLE

            // load user in background thread
            getPerson(personId)
        }
        */
    }
/*
    private fun setupForm() {
        first_name.error = "Please enter a first name"
        last_name.error = "Please enter a last name"
    }

    /**
     * Attempts to retrieve a person from the db
     */
    private fun getPerson(pId: Long) {
        launch { // coroutine on Main
            val query = async(Dispatchers.IO) {
                // retrieve person from db
                DBHelper.getInstance(this@CreateUser)?.personDao()?.let { pd ->
                    person = pd.getPerson(pId)
                }
            }

            query.await()

            // fill inputs
            populateForm()
        }
    }

    /**
     * Populates the form based on the loaded user
     */
    private fun populateForm() {
        person?.let { p ->
            first_name.setText(p.firstName)
            last_name.setText(p.lastName)
            address.setText(p.street)
            city.setText(p.city)
            state.setText(p.state)
            zip.setText(p.postcode)
            phone.setText(p.phone)
            cell.setText(p.cell)

            p.avatarLarge?.let {
                // Picasso.get().load(it).into(avatar)
                //hideShowAvatarPlaceholder(true)
            } ?: p.getImage()?.let {
                avatar.setImageBitmap(it)
                hideShowAvatarPlaceholder(true)
            } ?: run {
                hideShowAvatarPlaceholder(false)
            }
        }

        loading_person.visibility = View.GONE
    }

    private fun savePerson(v: View) {
        val fn : String = first_name.text.toString()
        val ln : String = last_name.text.toString()
        val add : String = address.text.toString()
        val c : String = city.text.toString()
        val st : String = state.text.toString()
        val pc : String = zip.text.toString()
        val ph : String = phone.text.toString()
        val cl : String = cell.text.toString()

        person?.apply {
            firstName = fn
            lastName = ln
            street = add
            city = c
            state = st
            postcode = pc
            phone = ph
            cell = cl

            if (avatar.visibility == View.VISIBLE) {
                avatar.drawable?.let { img ->
                    val bm : BitmapDrawable = img as BitmapDrawable
                    setImage(bm.bitmap)
                }
            }
        }

        // update person in background
        launch { // coroutine on Main
            val query = async(Dispatchers.IO) {
                // retrieve person from db
                person?.let { p ->
                    DBHelper.getInstance(this@CreateUser)?.personDao()?.let { pd ->
                        if (p._id > 0) {
                            pd.update(p)
                        } else {
                            pd.insert(p)
                        }
                    }
                }
            }


            query.await()

            person?.let {
                // return to previous activity with save flag
                val i = Intent()
                i.putExtra("pId", it._id)
                setResult(NEWPERSONRESULT)
            }

            finish()
        }
    }

    /**
     * Sets the visibility of the avatar image and placeholder
     */
    private fun hideShowAvatarPlaceholder(hide: Boolean) {
        if (hide) {
            placeholder.visibility = View.GONE
            plus_icon.visibility = View.GONE
            avatar.visibility = View.VISIBLE
        } else {
            avatar.visibility = View.GONE
            placeholder.visibility = View.VISIBLE
            plus_icon.visibility = View.VISIBLE
        }
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_SCAN) {
            if (data != null) {
                try {
                    val bitmap = data.extras!!.get("data") as Bitmap
                    val avatarBitmap = Bitmap.createScaledBitmap(bitmap, 100.px(), 100.px(), false)

                    avatar.setImageBitmap(avatarBitmap)

                    placeholder.visibility = View.GONE
                    plus_icon.visibility = View.GONE
                    avatar.visibility = View.VISIBLE
                } catch (e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(this@CreateUser, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun presentCamera() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_SCAN)
    }

    private fun checkCameraPermission(v : View) {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        } else {
            checkReadPermission()
        }
    }

    private fun checkReadPermission() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestReadExternalPermission()
        } else {
            presentCamera()
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION)
    }

    private fun requestReadExternalPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXT_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Permission has been denied by user
                    showMessage("Whoops!", "Please allow camera permission to upload photos")
                } else {
                    // Permission has been granted by user
                    checkReadPermission()
                }
            }
            READ_EXT_PERMISSION -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // Permission has been denied by user
                    showMessage("Whoops!", "Please allow read permission to upload photos")
                } else {
                    // Permission has been granted by user
                    presentCamera()
                }
            }
        }
    }

    private fun showMessage(title: String, message: String) {
        val builder = AlertDialog.Builder(this@CreateUser)

        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

        val dialog: AlertDialog = builder.create()

        dialog.show()
    }
    */
}