package com.example.tubes_pbp_kelompok3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tubes_pbp_kelompok3.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "LoginActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText edtEmail;
    private EditText edtPass;
    private Button btnMasuk;
    private Button btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_login);
        //variabel tadi untuk memanggil fungsi
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // diatur sesuai id komponennya
        edtEmail = (EditText)findViewById(R.id.emailLogin);
        edtPass = (EditText)findViewById(R.id.passwordLogin);
        btnMasuk = (Button) findViewById(R.id.btnLogin);
        btnDaftar = (Button)findViewById(R.id.btnDaftarSkrng);

        //nambahin method onClick, biar tombolnya bisa diklik
        btnMasuk.setOnClickListener(this);
        btnDaftar.setOnClickListener(this);

    }

    //fungsi signin untuk mengkonfirmasi data pengguna yang sudah mendaftar sebelumnya
    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        //showProgressDialog();
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        //hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //fungsi dipanggil ketika proses Authentikasi berhasil
    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // membuat User admin baru
        writeNewAdmin(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    /*
        ini fungsi buat bikin username dari email
            contoh email: abcdefg@mail.com
            maka username nya: abcdefg
     */
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    //fungsi untuk memvalidasi EditText email dan password agar tak kosong dan sesuai format
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("Required");
            result = false;
        } else {
            edtEmail.setError(null);
        }

        if (TextUtils.isEmpty(edtPass.getText().toString())) {
            edtPass.setError("Required");
            result = false;
        } else {
            edtPass.setError(null);
        }

        return result;
    }

    // menulis ke Database
    private void writeNewAdmin(String userId, String name, String email) {
        AdminDAO admin = new AdminDAO(name, email);

        mDatabase.child("admins").child(userId).setValue(admin);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnLogin) {
            signIn();
        }

        else if (i == R.id.btnDaftarSkrng) {
            Intent intent = new Intent(getApplicationContext(), DaftarSbgActivity.class);
            startActivity(intent);
        }
    }

}
