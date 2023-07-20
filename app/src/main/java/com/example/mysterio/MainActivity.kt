package com.example.mysterio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.mysterio.news.NewsFragment
import com.example.mysterio.car.CarFragment
import com.example.mysterio.databinding.ActivityMainBinding
import com.example.mysterio.music.MusicService
import com.example.mysterio.music.MyMusicFragment
import com.example.mysterio.navigation.NavigationFragment

class MainActivity : AppCompatActivity() {
    private var selectedMenuId = R.id.menuMusicFeed
    private lateinit var binding: ActivityMainBinding
    val myMusicFragment = MyMusicFragment()
    val navigationFragment = NavigationFragment()
    val dialFragment = NewsFragment()
    val carFragment = CarFragment()
    private var active: Fragment = myMusicFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        loadAllFragments()
        initView()
    }

    private fun initView() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menuMusicFeed -> {
                    selectedMenuId = it.itemId
                    supportFragmentManager.beginTransaction().hide(active).show(myMusicFragment)
                        .setCustomAnimations(
                            R.anim.exit_to_right,
                            R.anim.enter_from_right
                        )
                        .commit()
                    active = myMusicFragment
                }

                R.id.menuMap -> {
                    selectedMenuId = it.itemId
                    supportFragmentManager.beginTransaction().hide(active).show(navigationFragment)
                        .setCustomAnimations(
                            R.anim.exit_to_right,
                            R.anim.enter_from_right
                        )
                        .commit()
                    active = navigationFragment
                }

                R.id.menuNews -> {
                    selectedMenuId = it.itemId
                    supportFragmentManager.beginTransaction().hide(active).show(dialFragment)
                        .setCustomAnimations(
                            R.anim.exit_to_right,
                            R.anim.enter_from_right
                        )
                        .commit()
                    active = dialFragment
                }

                R.id.menucar -> {
                    selectedMenuId = it.itemId
                    supportFragmentManager.beginTransaction().hide(active).show(carFragment)
                        .setCustomAnimations(
                            R.anim.exit_to_right,
                            R.anim.enter_from_right
                        )
                        .commit()
                    active = carFragment
                }
            }
            true
        }
    }

    fun loadAllFragments() {
        supportFragmentManager.beginTransaction().add(R.id.flContainer, carFragment)
            .hide(carFragment).setCustomAnimations(
                R.anim.exit_to_right,
                R.anim.enter_from_right
            )
            .commit()
        supportFragmentManager.beginTransaction().add(R.id.flContainer, dialFragment)
            .hide(dialFragment).setCustomAnimations(
                R.anim.exit_to_right,
                R.anim.enter_from_right
            ).commit()
        supportFragmentManager.beginTransaction().add(R.id.flContainer, navigationFragment)
            .hide(navigationFragment).setCustomAnimations(
                R.anim.exit_to_right,
                R.anim.enter_from_right
            ).commit()
        supportFragmentManager.beginTransaction().add(R.id.flContainer, myMusicFragment)
            .setCustomAnimations(
                R.anim.exit_to_right,
                R.anim.enter_from_right
            ).commit()
    }

    override fun onBackPressed() {
        val musicIntent = Intent(this, MusicService::class.java)
        stopService(musicIntent)
        finish()
    }
}