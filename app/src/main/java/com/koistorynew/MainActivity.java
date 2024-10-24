package com.koistorynew;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.koistorynew.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

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
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_blog, R.id.nav_market, R.id.nav_consult, R.id.nav_mymarket, R.id.nav_myconsult)
                .setOpenableLayout(drawer)
                .build();
        // đối tượng nav controller giúp điều hướng
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // thiết lập điều hướng cho actionbar với nav controller
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        // kết nối thanh điều hướng với nav controller , giúp điều hướng giữa các fragment thông qua các mục menu
        NavigationUI.setupWithNavController(navigationView, navController);


        View headerView = navigationView.getHeaderView(0); // Lấy view header đầu tiên
        TextView userName = headerView.findViewById(R.id.userName);
        TextView userEmail = headerView.findViewById(R.id.userEmail);
        ImageView avatar = headerView.findViewById(R.id.avatar);

        // Lấy thông tin từ UserSessionManager
        String user_name = UserSessionManager.getInstance().getDisplayName();
        String user_email = UserSessionManager.getInstance().getEmail();
        String user_picture = UserSessionManager.getInstance().getProfilePictureUrl();

        // Đặt tên người dùng và email
        userName.setText(user_name != null && !user_name.isEmpty() ? user_name : "No Name");
        userEmail.setText(user_email != null && !user_email.isEmpty() ? user_email : "No Email");

        // Kiểm tra và đặt avatar người dùng bằng Picasso
        if (user_picture != null && !user_picture.isEmpty()) {
            Picasso.get()
                    .load(user_picture)
                    .placeholder(R.mipmap.ic_launcher_round) // Hình mặc định nếu chưa tải xong
                    .error(R.mipmap.ic_launcher_round) // Hình mặc định nếu có lỗi
                    .into(avatar);
        } else {
            // Nếu không có avatar, đặt ảnh mặc định
            avatar.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // nạp các mục menu lên thanh action bar, thường dùn để thêm các mục như cài đặt
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Tạo AlertDialog để xác nhận đăng xuất
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setTitle("Đang đăng xuất...");
                            progressDialog.show();

                            UserSessionManager.getInstance().clearUserData();

                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() { // xử lý sự kiện khi người dùng nhấn nút back hoặc home, điều hướng về fragment trước đó hoặc đóng drawer
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}