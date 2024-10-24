package com.koistorynew.ui.fish;

import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.koistorynew.R;
import com.koistorynew.ui.fish.adapter.KoiFishAdapter;
import com.koistorynew.ui.fish.model.KoiFish;

import java.util.ArrayList;
import java.util.List;

public class KoiActivity extends AppCompatActivity {
    private GridView koiGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        koiGrid = findViewById(R.id.koi_grid);
//
//        List<KoiFish> koiFishList = new ArrayList<>();
//        koiFishList.add(new KoiFish(R.drawable.asagi, "Asagi")); // Replace with your actual image resources
//        koiFishList.add(new KoiFish(R.drawable.gin_matsuba, "Gin Matsuba"));
//
//        KoiFishAdapter adapter = new KoiFishAdapter(this, koiFishList);
//        koiGrid.setAdapter(adapter);
    }
}
