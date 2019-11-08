package com.example.studikasussisi.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studikasussisi.Adapter.MainAdapter;
import com.example.studikasussisi.R;
import com.example.studikasussisi.Repository.Model.HomeMenu;
import com.example.studikasussisi.ViewModel.MainViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainAdapter.Listener {

    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarText = findViewById(R.id.toolbar_text);
        toolbarText.setText("Home");
        toolbarText.setTextColor(Color.WHITE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getmHomeMenu().observe(this, new Observer<List<HomeMenu>>() {
            @Override
            public void onChanged(List<HomeMenu> homeMenus) {
                MainAdapter adapter = new MainAdapter(homeMenus, MainActivity.this, MainActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });

    }

    @Override
    public void onClick(int position) {
        if (position == 0) {
            Intent intent = new Intent(MainActivity.this, CreateActivity.class);
            startActivity(intent);
        } else if (position == 1) {
            Intent intent = new Intent(MainActivity.this, ReadActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "position "+position, Toast.LENGTH_SHORT).show();
        }
    }
}
