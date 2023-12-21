package com.example.rottentomato

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.rottentomato.databinding.HomeScreenBinding
import com.google.firebase.Firebase

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var binding: HomeScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /* atur tampilan awal dari fragement */
        switchFragment(HomeFragment())
        /* fungsi untuk berpindah antar fragment */
        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home -> switchFragment(HomeFragment())
                R.id.profile -> switchFragment(ProfileFragment())
                else ->{

                }
            }
            true
        }
    }
    private fun switchFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}