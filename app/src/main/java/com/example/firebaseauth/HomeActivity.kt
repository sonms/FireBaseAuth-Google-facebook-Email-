package com.example.firebaseauth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.firebaseauth.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)
        auth = FirebaseAuth.getInstance()
        binding.appBarHome.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //좌측이나 우측에 나오는 메뉴 화면 세팅
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        
    }

    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }
    
    //툴바 메뉴 버튼이 클릭 됐을 떄 실행하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 클릭한 툴바 메뉴 아이템 id 마다 다르게 실행하도록 설정
        when(item!!.itemId) {
            android.R.id.home-> {
                //버튼 클릭시 네비게이션 드로어 열기
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    // 드로어 내 아이템 클릭 이벤트 처리하는 함수
    override fun onNavigationItemSelected(item : MenuItem): Boolean {

        when (val id = item.itemId) {
            R.id.nav_home -> {
    
            }
            R.id.nav_gallery -> {
                Toast.makeText(baseContext, "$id",
                    Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                //intent.setAction(MediaStore.Images.Media.CONTENT_TYPE)
                intent.action = MediaStore.Images.Media.CONTENT_TYPE
                startActivityForResult(intent,GALLERY_CODE)
            }
            R.id.nav_logout -> {
                auth.signOut()
                LoginManager.getInstance().logOut()
                finish()
                val intent = Intent(this, MainActivity::class.java) //원하는 클래스로 데이터 받기
                startActivity(intent)
    
            }
        }
        // 화면 한쪽에 숨겨져 있다가 사용자가 액션을 취하면 화면에 나타나는 기능을 만들 수 있게 해주는 레이아웃
        val drawer = DrawerLayout(findViewById(R.id.drawer_layout))
        //drawer 닫기
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
