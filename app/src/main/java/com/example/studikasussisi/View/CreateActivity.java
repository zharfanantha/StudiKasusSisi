package com.example.studikasussisi.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studikasussisi.R;
import com.example.studikasussisi.Repository.Model.User;
import com.example.studikasussisi.ViewModel.CreateViewModel;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class CreateActivity extends AppCompatActivity {
    EditText noKtp, nama, tglLahir, alamat;
    DatePickerDialog picker;
    Spinner jKelamin;
    Button save;
    ConstraintLayout fotoLayout;
    ImageView foto;
    private static final int PIC_FOTO = 1;
    TextView error;
    ProgressBar loading;

    String[] jenisK = { "Pilih Salah Satu", "Laki-Laki", "Perempuan" };
    String jenisKelamin = "", fotoString = "";
    int inputSpinner = 0;
    CreateViewModel viewModel;
    Boolean cekField;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        noKtp = findViewById(R.id.create_noKtp);
        nama = findViewById(R.id.create_nama);
        tglLahir = findViewById(R.id.create_tglLahir);
        alamat = findViewById(R.id.create_alamat);
        jKelamin = findViewById(R.id.create_spinner_jKelamin);
        save = findViewById(R.id.create_saveBtn);
        fotoLayout = findViewById(R.id.create_foto_constraint);
        foto = findViewById(R.id.create_foto);
        error = findViewById(R.id.create_error);
        loading = findViewById(R.id.create_loading);
        tglLahir.setInputType(InputType.TYPE_NULL);
        viewModel = ViewModelProviders.of(this).get(CreateViewModel.class);

        ArrayAdapter<String> prioritasSpinner =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, jenisK);
        prioritasSpinner.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        jKelamin.setAdapter(prioritasSpinner);

        jKelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    inputSpinner = 1;
                    jenisKelamin = "Laki-Laki";
                } else if (position == 2) {
                    inputSpinner = 2;
                    jenisKelamin = "Perempuan";
                } else {
                    inputSpinner = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tglLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(CreateActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tglLahir.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        fotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraLaunch = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraLaunch, PIC_FOTO);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
                if (cekField) {
                    error.setVisibility(View.GONE);
                    user.setNoKtp(noKtp.getText().toString());
                    user.setNama(nama.getText().toString());
                    user.setJenisKelamin(jenisKelamin);
                    user.setTglLahir(tglLahir.getText().toString());
                    user.setFoto(fotoString);
                    user.setAlamat(alamat.getText().toString());
                    viewModel.insertUserToDB(user);
                } else {
                    error.setVisibility(View.VISIBLE);
                }
            }
        });
        
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIsSaved().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Log.e("cek", "user Sukses");
                    Intent intent = new Intent(CreateActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        viewModel.getIsError().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isError) {
                if(isError){
                    error.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                error.setText(message);
            }
        });

        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading){
                    loading.setVisibility(View.VISIBLE);
                    error.setVisibility(View.GONE);
                    save.setVisibility(View.GONE);
                } else {
                    loading.setVisibility(View.GONE);
                    error.setVisibility(View.GONE);
                }
            }
        });
    }

    public void validation() {
        if (noKtp.getText().toString().isEmpty()) {
            cekField = false;
            error.setText("Nomor KTP Harus Di isi");
        } else if (nama.getText().toString().isEmpty()) {
            cekField = false;
            error.setText("Nama Harus Di isi");
        } else if (inputSpinner == 0) {
            cekField = false;
            error.setText("Silahkan Pilih Jenis Kelamin");
        } else if (tglLahir.getText().toString().isEmpty()) {
            cekField = false;
            error.setText("Tanggal Lahir Harus Di isi");
        } else if (alamat.getText().toString().isEmpty()) {
            cekField = false;
            error.setText("Alamat Harus Di isi");
        } else if (fotoString.isEmpty()) {
            cekField = false;
            error.setText("Silahkan Foto terlebih Dahulu");
        }  else {
            cekField = true;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PIC_FOTO) {
            if (data != null) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                foto.setImageBitmap(image);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
                fotoString = imageEncoded;
                Log.e("encode", "user "+imageEncoded);
            }
        }
    }

}
