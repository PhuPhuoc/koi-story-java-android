package com.koistorynew;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.koistorynew.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration; // cấu hình cho thanh action và drawer
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // gán toolbar từ layout cho actionbar của activity
        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout; // lấy thanh menu
        NavigationView navigationView = binding.navView; // lấy điều hướng của menu

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // định nghĩa fragment như là top-level destination
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_blog, R.id.nav_market)
                .setOpenableLayout(drawer)
                .build();
        // đối tượng nav controller giúp điều hướng
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // thiết lập điều hướng cho actionbar với nav controller
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        // kết nối thanh điều hướng với nav controller , giúp điều hướng giữa các fragment thông qua các mục menu
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // nạp các mục menu lên thanh action bar, thường dùn để thêm các mục như cài đặt
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() { // xử lý sự kiện khi người dùng nhấn nút back hoặc home, điều hướng về fragment trước đó hoặc đóng drawer
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}