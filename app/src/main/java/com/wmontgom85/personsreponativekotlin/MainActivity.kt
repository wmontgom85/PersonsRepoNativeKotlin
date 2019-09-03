package com.wmontgom85.personsreponativekotlin

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wmontgom85.personsreponativekotlin.api.ImageLoader
import com.wmontgom85.personsreponativekotlin.func.*
import com.wmontgom85.personsreponativekotlin.func.newHeight
import com.wmontgom85.personsreponativekotlin.func.newWidth
import com.wmontgom85.personsreponativekotlin.func.px
import com.wmontgom85.personsreponativekotlin.model.Person
import com.wmontgom85.personsreponativekotlin.viewmodel.PersonsViewModel

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import java.net.URL
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private val NEWPERSONRESULT = 111

    private lateinit var personsViewModel: PersonsViewModel
    private lateinit var adapter : PersonsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var persons : List<Person>? = null

    private val job by lazy { Job() }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        linearLayoutManager = LinearLayoutManager(this)
        persons_list.layoutManager = linearLayoutManager

        adapter = PersonsAdapter()
        persons_list.adapter = adapter

        //Use view ModelFactory to initialize view model
        personsViewModel = ViewModelProviders.of(this).get(PersonsViewModel::class.java)

        //observe viewModel live data
        personsViewModel.personsLiveData.observe(this, Observer {
            it?.let {
                persons = it
                refreshPersons()
            }
        })

        // observe viewModel errors
        personsViewModel.errorHandler.observe(this, Observer {
            showMessage("Whoops!", it)
        })

        // throttle the fab so that the animation can complete before another click is available
        val menuAction: (View) -> Unit = throttleFirst(350L, MainScope(), this::hideShowActionMenu)
        fab.setOnClickListener(menuAction)

        // throttle person creation actions
        val personActon: (View) -> Unit = throttleFirst(500L, MainScope(), this::createPerson)
        create_action.setOnClickListener(personActon)
        random_action.setOnClickListener(personActon)

        // fetch the users
        personsViewModel.getPersonsFromDB()
    }

    private fun showMessage(title: String, msg: String) {
        loading.visibility = View.GONE

        val builder = AlertDialog.Builder(this@MainActivity)

        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

        val dialog: AlertDialog = builder.create()

        loading.visibility = View.GONE
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // kills all coroutines under the scope
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> {
                personsViewModel.deleteAllPersons()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Hides or shows the action menu based on its current visibility
     */
    fun hideShowActionMenu(v : View) {
        if (action_menu.width > 0) {
            hideActionMenu()
        } else {
            showActionMenu()
        }
    }

    /**
     * Shows the action menu
     */
    fun showActionMenu() {
        if (action_menu.width == 0) { // do nothing if it's already expanded
            animateActionMenu(0, 140.px(), 0, 100.px(), 0f, -45f)
        }
    }

    /**
     * Hides the action menu
     */
    fun hideActionMenu() {
        if (action_menu.width > 0) { // do nothing if it's already collapsed
            animateActionMenu(140.px(), 0, 100.px(), 0, -45f, 0f)
        }
    }

    /**
     * Animates the expansion or collapsion (yes, i know that's not a word) of the action menu
     */
    fun animateActionMenu(ws: Int, we: Int, hs: Int, he: Int, rs: Float, re: Float) {
        val wa = ValueAnimator.ofInt(ws, we)
        wa.addUpdateListener { action_menu.newWidth(it.animatedValue as Int) }

        val ha = ValueAnimator.ofInt(hs, he)
        ha.addUpdateListener { action_menu.newHeight(it.animatedValue as Int) }

        val ra = ValueAnimator.ofFloat(rs, re)
        ra.addUpdateListener { fab.rotation = it.animatedValue as Float }

        val animset = AnimatorSet()
        animset.play(wa).with(ha).with(ra)
        animset.duration = 300
        animset.start()
    }

    /**
     * Sends the user to a new screen to manually input person data or randomly generates one from the randomuser API
     */
    fun createPerson(v: View) {
        hideActionMenu()

        if (v == create_action) {
            val intent = Intent(this@MainActivity, CreateUser::class.java)
            startActivityForResult(intent, NEWPERSONRESULT)
        } else {
            // generate random user
            loading.visibility = View.VISIBLE
            personsViewModel.getRandomPerson()
        }
    }

    /**
     * Adds the new person to the DB (if exists) and refreshes the persons list
     * @param Person?
     */
    private fun refreshPersons() {
        loading.visibility = View.GONE

        // update recycler view
        adapter.notifyDataSetChanged()
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        System.out.println("request code: $requestCode")
        System.out.println("data " + data)

        if (requestCode == NEWPERSONRESULT) {
            personsViewModel.getPersonsFromDB()
        }
    }

    /**
     * RecyclerView Adapter
     */
    inner class PersonsAdapter : RecyclerView.Adapter<PersonHolder>() {
        override fun getItemCount(): Int {
            return persons?.size ?: 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonHolder {
            val inflatedView = parent.inflate(R.layout.recycler_view_holder, false)
            return PersonHolder(inflatedView)
        }

        override fun onBindViewHolder(holder: PersonHolder, position: Int) {
            val p = persons?.get(position)

            p.let {
                val cb = fun(v: View) {
                    var i : Intent = Intent(this@MainActivity, CreateUser::class.java)
                    i.putExtra("personId", holder.personId)
                    val options = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity,holder.avatar,
                        "edit_create_person_transition")
                    startActivityForResult(i, NEWPERSONRESULT, options.toBundle())
                }

                val menuAction: (View) -> Unit = throttleFirst(1000L, MainScope(), cb)
                holder.itemView.setOnClickListener(menuAction)

                holder.populate(it!!)
            }
        }
    }

    /**
     * PersonHolder class
     */
    inner class PersonHolder(v: View) : RecyclerView.ViewHolder(v) {
        var personId : Long = 0L
        val avatar : ImageView = v.findViewById(R.id.avatar)
        val name : TextView = v.findViewById(R.id.name)
        val address : TextView = v.findViewById(R.id.address)
        val phone : TextView = v.findViewById(R.id.phone)

        fun populate(p : Person) {
            personId = p.id

            p.avatarLarge?.let {
                if (it.isNotEmpty()) {
                    val bg = ImageLoader.get(it).loadInto(avatar)
                } else {
                    avatar.setImageResource(R.mipmap.avatar)
                }
            } ?: run {
                avatar.setImageResource(R.mipmap.avatar)
            }

            name.text = String.format("%s %s", p.firstName?.capitalize(), p.lastName?.capitalize())
            address.text = p.buildAddress().capitalize()
            phone.text = p.phone
        }
    }
}
