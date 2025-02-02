package com.eco.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.airbnb.lottie.LottieAnimationView
import com.eco.app.carbonfootprint.ResultCalculator
import com.eco.app.databinding.ActivityHomeWindowBinding
import com.eco.app.games.ResultQuizFragmentDirections
import com.facebook.AccessToken
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class HomeWindow : AppCompatActivity() {
    private lateinit var binding : ActivityHomeWindowBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var currentUser: FirebaseUser
    private var logoutActionId: Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeWindowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawer=binding.root
        toolbar=binding.toolbar
        navView=binding.navView
        navHostFragment=supportFragmentManager.findFragmentById(R.id.home_fragment_container) as NavHostFragment
        navController=navHostFragment.navController

        setSupportActionBar(binding.toolbar)

       setupActionBarDestinations(0)

        navView.setupWithNavController(navController)


        val navBar=binding.navBar
        navBar.setupWithNavController(navController)

        //quando il drawer viene aperto, se l'animazione di lottie è visibile, e quindi l'utente
        //non è loggato, allora faccio partire l'animazione
        drawer.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val lottie=drawer.findViewById<LottieAnimationView>(R.id.lottie_stock_profile_animation)
                if(Firebase.auth.currentUser!=null){ //se sei loggato, setta immagine nel drawer ( se la hai )
                    val filename = Firebase.auth.currentUser!!.uid
                    val storageReference = FirebaseStorage.getInstance("gs://ecoapp-706b8.appspot.com")
                        .getReference("propics/$filename")
                    val localfile = File.createTempFile("tempImage", "jpg")
                    storageReference.getFile(localfile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                        val resized = Bitmap.createScaledBitmap(bitmap, 400, 400, true)
                        lottie.setImageBitmap(resized)
                        lottie.clipToOutline = true
                    }.addOnFailureListener {
                       // Toast.makeText(baseContext, "Errore nella propic", Toast.LENGTH_SHORT).show()
                    }
                }
                if(lottie.visibility==View.VISIBLE) {
                    if(slideOffset>=0.75f&&!lottie.isAnimating){
                        lottie.playAnimation()
                    }
                    else {
                        lottie.progress = 0f
                    }
                }
            }

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerOpened(drawerView: View) {
                val lottie=drawer.findViewById<LottieAnimationView>(R.id.lottie_stock_profile_animation)
                if(lottie.visibility==View.VISIBLE){
                    //resetto l'animazione
                    //faccio fermare l'animazione quando arriva all'ultimo frame
                    lottie.addAnimatorUpdateListener { animation ->
                        if(animation.animatedFraction==0.99F){
                            lottie.pauseAnimation()
                        }
                    }
                    lottie.playAnimation()
                }
            }
        })

        //TODO cambiare icona logout
    }

    override fun onSupportNavigateUp(): Boolean {
        //se l'utente si trova nella schermata di risultato del quiz
        //allora, una volta premuto il tasto per tornare indietro,
        //lo faccio tornare alla schermata di selezione dei giochi
        return if(navController.currentDestination?.id==(R.id.QuizResultFragment)){
            navController.navigate(ResultQuizFragmentDirections.actionQuizResultFragmentToGameSelectionFragment())
            true
        }
        else {
            navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        loadDrawerMenuItems()

        return true
    }


    fun loadDrawerMenuItems(){
        //TODO se l'utente è loggato mostrare la sua profile pic nel drawer_header.xml
        if(Firebase.auth.currentUser!=null){
            currentUser= Firebase.auth.currentUser!!

            logoutActionId= View.generateViewId()


            navView.menu.removeItem(R.id.login_fragment)
            navView.menu.add(0,R.id.profile_fragment,0,getString(R.string.profile_page_name)).setIcon(R.drawable.ic_person)
            navView.menu.add(0,logoutActionId!!,1,getString(R.string.logout_menu_item))
                .setIcon(R.drawable.ic_logout)
                .setOnMenuItemClickListener {
                    drawer.closeDrawer(GravityCompat.START)
                    val logoutDialog=AlertDialog.Builder(this)
                    logoutDialog.setMessage("Sei sicuro di voler effettuare il logout?")
                    logoutDialog.setNegativeButton("Annulla"){dialog,_->
                        dialog.dismiss()
                    }
                    logoutDialog.setPositiveButton("Conferma"){dialog,_->
                        dialog.dismiss()
                        val snackbar=Snackbar.make(binding.root,"Logout effettuato con successo",Snackbar.LENGTH_SHORT)
                        snackbar.anchorView=binding.navBar
                        //tasto per annullare il logout
                        snackbar.setAction("Annulla"){
                            //settare la callback della snackbar a null //todo rivedere snackbar fa schifo
                            snackbar.dismiss()
                        }
                        //quando la snackbar non è più visibile, fa il logout
                        snackbar.addCallback(object : Snackbar.Callback(){
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                if(event==Snackbar.Callback.DISMISS_EVENT_TIMEOUT){
                                    Firebase.auth.signOut()
                                    if(isLoggedInWithFacebook()){
                                        com.facebook.login.LoginManager.getInstance().logOut();
                                    }
                                    invalidateOptionsMenu()
                                }
                            }
                        })
                        snackbar.show()
                    }
                    logoutDialog.show()

                    true
            }
        }
        else{
            navView.menu.removeItem(R.id.profile_fragment)
            if(logoutActionId!=null) {
                navView.menu.removeItem(logoutActionId!!)
            }
            navView.menu.add(0,R.id.login_fragment,1,getString(R.string.login_menu_item)).setIcon(R.drawable.ic_login)
        }
    }
    fun isLoggedInWithFacebook(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null
    }


    fun setupActionBarDestinations(currentDestinationId: Int){
        val navController=(supportFragmentManager.findFragmentById(R.id.home_fragment_container) as NavHostFragment).navController
        val navBar=findViewById<BottomNavigationView>(R.id.navBar)

        val firstFragmentsList= mutableSetOf(R.id.GameSelectionFragment,R.id.CalendarFragment)

        val cfPoints=getSharedPreferences(ResultCalculator.SHARED_PREFS,Context.MODE_PRIVATE).getFloat("punteggio",0F)
        println("punteggio cf: $cfPoints")
        //se l'utente ha calcolato il punteggio,
        //imposto che il bottone del carbon footprint lo porta alla schermata del punteggio
        if(cfPoints!=0F){
            firstFragmentsList.add(R.id.CalculatorFragmentResult)
            navBar.menu.clear()
            navBar.inflateMenu(R.menu.bottom_nav_bar_cf_result)
        }
        else{
            firstFragmentsList.add(R.id.CalculatorFragmentPage0)
            navBar.menu.clear()
            navBar.inflateMenu(R.menu.bottom_nav_bar_cf_calculator)
        }

        navBar.menu.getItem(currentDestinationId).isChecked=true


        //Inserisco gli elementi base della navigation UI
        appBarConfiguration= AppBarConfiguration(firstFragmentsList,drawer)

        setupActionBarWithNavController(navController,appBarConfiguration)
    }
}

