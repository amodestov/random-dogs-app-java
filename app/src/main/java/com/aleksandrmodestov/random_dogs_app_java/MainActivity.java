package com.aleksandrmodestov.random_dogs_app_java;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView imageViewDog;
    private ProgressBar progressBar;
    private Button buttonNextImage;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.loadDogImage();
        viewModel.getDog().observe(this, new Observer<Dog>() {
            @Override
            public void onChanged(Dog dog) {
                Glide.with(MainActivity.this)
                        .load(dog.getMessage())
                        .into(imageViewDog);
                Log.d(TAG, dog.toString());
            }
        });
        viewModel.getShouldDisplayProgressBar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean shouldDisplayProgressBar) {
                if (shouldDisplayProgressBar) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        viewModel.getShouldDisplayErrorToast().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean shouldDisplayErrorToast) {
                if (shouldDisplayErrorToast) {
                    Toast.makeText(
                            MainActivity.this,
                            R.string.internet_connection_lost,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonNextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.loadDogImage();
            }
        });
    }

    private void initViews() {
        imageViewDog = findViewById(R.id.imageViewDog);
        progressBar = findViewById(R.id.progressBar);
        buttonNextImage = findViewById(R.id.buttonLoadImage);
    }
}
