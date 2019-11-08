package com.example.studikasussisi.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.studikasussisi.Adapter.ReadAdapter;
import com.example.studikasussisi.R;
import com.example.studikasussisi.Repository.Model.User;
import com.example.studikasussisi.ViewModel.CreateViewModel;
import com.example.studikasussisi.ViewModel.ReadViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends AppCompatActivity implements ReadAdapter.Listener {
    ReadViewModel viewModel;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar loading;
    TextView errorMessage;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private ReadAdapter adapter =
            new ReadAdapter(new ArrayList<User>(), ReadActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        viewModel = ViewModelProviders.of(this).get(ReadViewModel.class);
        recyclerView = findViewById(R.id.read_recycler);
        swipeRefreshLayout = findViewById(R.id.to_do_swipe_refresh_layout);
        loading = findViewById(R.id.read_loading);
        errorMessage = findViewById(R.id.read_error);

        viewModel.getAllUser();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getAllUser();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getListUser().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users != null) {
                    adapter.updateList(users);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Data Tidak Ditemukan");
                }
            }
        });

        viewModel.getIsError().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isError) {
                if(isError){
                    errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                errorMessage.setText(message);
            }
        });

        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading){
                    loading.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.GONE);
                } else {
                    loading.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(User User) {
        editor = pref.edit();
        editor.putString("noKtp", User.getNoKtp());
        editor.commit();
        Intent intent = new Intent(ReadActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getAllUser();
    }
}
