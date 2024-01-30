package gem.poc.tvapp

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.leanback.widget.BrowseFrameLayout
import androidx.recyclerview.widget.RecyclerView
import gem.poc.tvapp.fragment.*
import gem.poc.tvapp.utils.Common
import gem.poc.tvapp.utils.Constants

class MainActivity : FragmentActivity(), View.OnKeyListener {

    lateinit var navBar: BrowseFrameLayout
    lateinit var fragmentContainer: FrameLayout

    lateinit var btnSearch: TextView
    lateinit var btnHome: TextView
    lateinit var btnTvshow: TextView
    lateinit var btnMovie: TextView
    lateinit var btnSports: TextView
    lateinit var btnSetting: TextView
    lateinit var btnLanguage: TextView
    lateinit var btnGenre: TextView

    var SIDE_MENU = false
    var selectedMenu = Constants.MENU_HOME

    lateinit var lastSelectedMenu: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentContainer = findViewById(R.id.container)
        navBar = findViewById(R.id.blfNavBar)

        btnSearch = findViewById(R.id.btn_search)
        btnHome = findViewById(R.id.btn_home)
        btnTvshow = findViewById(R.id.btn_tv)
        btnMovie = findViewById(R.id.btn_movies)
        btnSports = findViewById(R.id.btn_sports)
        btnSetting = findViewById(R.id.btn_settings)
        btnLanguage = findViewById(R.id.btn_language)
        btnGenre = findViewById(R.id.btn_genre)


        btnSearch.setOnKeyListener(this)
        btnHome.setOnKeyListener(this)
        btnTvshow.setOnKeyListener(this)
        btnMovie.setOnKeyListener(this)
        btnSports.setOnKeyListener(this)
        btnSetting.setOnKeyListener(this)
        btnLanguage.setOnKeyListener(this)
        btnGenre.setOnKeyListener(this)

        btnHome.setOnKeyListener(this)
        btnTvshow.setOnKeyListener(this)
        btnMovie.setOnKeyListener(this)
        btnSports.setOnKeyListener(this)
        btnSetting.setOnKeyListener(this)
        btnLanguage.setOnKeyListener(this)
        btnGenre.setOnKeyListener(this)

        lastSelectedMenu = btnHome
        lastSelectedMenu.isActivated = true
        changeFragment(HomeFragment())
    }

    fun changeFragment(fragment: Fragment) {
        Log.d("", "=============changeFragment========fragment = $fragment")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()

        closeMenu()
    }

    override fun onKey(view: View?, i: Int, key_event: KeyEvent?): Boolean {
        Log.d("", "===================onKey===============i = $i, view = $view")
        when (i) {
            KeyEvent.KEYCODE_DPAD_CENTER -> {

                lastSelectedMenu.isActivated = false
                view?.isActivated = true
                lastSelectedMenu = view!!

                when (view.id) {
                    R.id.btn_search -> {
                        selectedMenu = Constants.MENU_SEARCH
                        changeFragment(SearchFragment())
                    }
                    R.id.btn_home -> {
                        selectedMenu = Constants.MENU_HOME
                        changeFragment(HomeFragment())
                    }
                    R.id.btn_tv -> {
                        selectedMenu = Constants.MENU_TV
                        changeFragment(TvShowFragment())
                    }
                    R.id.btn_movies -> {
                        selectedMenu = Constants.MENU_MOVIE
                        changeFragment(MovieFragment())
                    }
                    R.id.btn_sports -> {
                        selectedMenu = Constants.MENU_SPORTS
                        changeFragment(SportsFragment())
                    }
                    R.id.btn_settings -> {
                        selectedMenu = Constants.MENU_SETTINGS
                        changeFragment(SettingsFragment())
                    }
                    R.id.btn_language -> {
                        selectedMenu = Constants.MENU_LANGUAGE
                        changeFragment(LanguageFragment())
                    }
                    R.id.btn_genre -> {
                        selectedMenu = Constants.MENU_GENRES
                        changeFragment(GenresFragment())
                    }
                }

            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (!SIDE_MENU) {
                    switchToLastSelectedMenu()

                    openMenu()
                    SIDE_MENU = true
                }
            }
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && SIDE_MENU) {
            SIDE_MENU = false
            closeMenu()
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (SIDE_MENU) {
            SIDE_MENU = false
            closeMenu()
        } else {
            super.onBackPressed()
        }
    }

    fun switchToLastSelectedMenu() {
        when (selectedMenu) {
            Constants.MENU_SEARCH -> {
                btnSearch.requestFocus()
            }
            Constants.MENU_HOME -> {
                btnHome.requestFocus()
            }
            Constants.MENU_TV -> {
                btnTvshow.requestFocus()
            }
            Constants.MENU_MOVIE -> {
                btnMovie.requestFocus()
            }
            Constants.MENU_SPORTS -> {
                btnSports.requestFocus()
            }
            Constants.MENU_LANGUAGE -> {
                btnLanguage.requestFocus()
            }
            Constants.MENU_GENRES -> {
                btnGenre.requestFocus()
            }
            Constants.MENU_SETTINGS -> {
                btnSetting.requestFocus()
            }
        }
    }

    fun openMenu() {
        val navBarBackground: View = findViewById(R.id.navBarBackground)
        val navBarContent: LinearLayout = findViewById(R.id.navBarContent)

        val animSlide : Animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
        val animBackground : Animation = AnimationUtils.loadAnimation(this, R.anim.left_menu_background_fade_in)
        navBarContent.startAnimation(animSlide)
        navBarBackground.startAnimation(animBackground)

        val layoutParams = navBar.layoutParams as ViewGroup.MarginLayoutParams
        val marginLeft = Common.getWidthInPercent(this, 2)
        layoutParams.setMargins(marginLeft, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin)
        navBar.layoutParams = layoutParams
        navBar.layoutParams.width = Common.getWidthInPercent(this, 100)
        navBarContent.layoutParams.width = Common.getWidthInPercent(this, 15)
        navBar.requestLayout()

        navBarBackground.setBackgroundResource(R.drawable.left_menu_gradient)
        navBarContent.setBackgroundResource(0)
    }

    fun closeMenu() {
        val navBarBackground: View = findViewById(R.id.navBarBackground)
        val navBarContent: LinearLayout = findViewById(R.id.navBarContent)

        val animBackground : Animation = AnimationUtils.loadAnimation(this, R.anim.left_menu_background_fade_out)
        navBarBackground.startAnimation(animBackground)

        val layoutParams = navBar.layoutParams as ViewGroup.MarginLayoutParams
        //val marginLeft = Common.getWidthInPercent(this, 2)
        layoutParams.setMargins(0, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin)
        navBar.layoutParams = layoutParams

        navBar.requestLayout()
        navBarContent.layoutParams.width = Common.getWidthInPercent(this, 5)
        navBarContent.setBackgroundResource(R.drawable.left_menu_gradient_closed)
        //navBar.layoutParams.width = Common.getWidthInPercent(this, 5)

        fragmentContainer.requestFocus()
        SIDE_MENU = false
    }

}