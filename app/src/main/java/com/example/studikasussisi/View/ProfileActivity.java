package com.example.studikasussisi.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.studikasussisi.R;
import com.example.studikasussisi.Repository.Model.User;
import com.example.studikasussisi.ViewModel.CreateViewModel;
import com.example.studikasussisi.ViewModel.ProfileViewModel;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    EditText noKtp, nama, tglLahir, alamat, umur;
    DatePickerDialog picker;
    Spinner jKelamin;
    Button update, delete;
    ConstraintLayout fotoLayout;
    ImageView foto;
    private static final int PIC_FOTO = 1;
    TextView error;
    ProgressBar loading;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    String[] jenisK = { "Pilih Salah Satu", "Laki-Laki", "Perempuan" };
    String jenisKelamin = "", fotoString = "", getNoKtp;
    int inputSpinner = 0;
    ProfileViewModel viewModel;
    Boolean cekField;
    User newUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        getNoKtp = pref.getString("noKtp", null);

        noKtp = findViewById(R.id.profile_noKtp);
        nama = findViewById(R.id.profile_nama);
        tglLahir = findViewById(R.id.profile_tglLahir);
        alamat = findViewById(R.id.profile_alamat);
        jKelamin = findViewById(R.id.profile_spinner_jKelamin);
        update = findViewById(R.id.profile_updateBtn);
        delete = findViewById(R.id.profile_deleteBtn);
        umur = findViewById(R.id.profile_umur);
        fotoLayout = findViewById(R.id.profile_foto_constraint);
        foto = findViewById(R.id.profile_foto);
        error = findViewById(R.id.profile_error);
        loading = findViewById(R.id.profile_loading);
        tglLahir.setInputType(InputType.TYPE_NULL);
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        viewModel.getUser(getNoKtp);

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
                picker = new DatePickerDialog(ProfileActivity.this,
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

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
                if (cekField) {
                    error.setVisibility(View.GONE);
                    newUser.setNoKtp(noKtp.getText().toString());
                    newUser.setNama(nama.getText().toString());
                    newUser.setJenisKelamin(jenisKelamin);
                    newUser.setTglLahir(tglLahir.getText().toString());
                    newUser.setFoto(fotoString);
                    newUser.setAlamat(alamat.getText().toString());
                    viewModel.updateProfile(newUser);
                } else {
                    error.setVisibility(View.VISIBLE);
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteProfile(newUser);
            }
        });

        observeViewModel();
    }


    private void observeViewModel() {
        viewModel.getUserProfile().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                newUser = user;
                noKtp.setText(user.getNoKtp());
                nama.setText(user.getNama());
                alamat.setText(user.getAlamat());
                tglLahir.setText(user.getTglLahir());
                umur.setText(calculateAge(user.getTglLahir()));
                umur.setInputType(InputType.TYPE_NULL);
                foto.setImageBitmap(decodeImage(user.getFoto()));
                if (user.getJenisKelamin().equalsIgnoreCase("Perempuan")) {
                    jKelamin.setSelection(2);
                } else {
                    jKelamin.setSelection(1);
                }
            }
        });

        viewModel.getIsDeleted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Log.e("cek", "user deleted sukses");
                    finish();
                }
            }
        });

        viewModel.getIsUpdated().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Log.e("cek", "user updated sukses");
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
                    update.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                } else {
                    loading.setVisibility(View.GONE);
                    error.setVisibility(View.GONE);
                    update.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
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

    private String calculateAge(String born) {
        Date birthDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            birthDate = sdf.parse(born);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int years = 0, months = 0, days = 0;
        Calendar birthday = Calendar.getInstance();
        birthday.setTimeInMillis(birthDate.getTime());

        long currTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currTime);

        years = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthday.get(Calendar.MONTH) + 1;

        months = currMonth - birthMonth;
        if (months < 0) {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthday.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthday.get(Calendar.DATE)) {
            years--;
            months = 11;
        }

        if (now.get(Calendar.DATE) > birthday.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthday.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthday.get(Calendar.DATE)) {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH) + today;
        }
        else {
            days = 0;
            if (months == 12) {
                years++;
                months = 0;
            }
        }

        return years+" tahun";
    }

    private Bitmap decodeImage(String base64){
        fotoString = base64;
        base64 = base64.replace("data:image/png;base64,", "");
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

}
