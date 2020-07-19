package com.example.instagram_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.instagram_clone.MainActivity;
import com.example.instagram_clone.R;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.utils.MessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseConfig.getAuth();

    EditText email, password;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (auth.getCurrentUser() != null) startMainActivity();

        initViewElements();
    }

    private void initViewElements() {
        email = findViewById(R.id.textLoginEmail);
        password = findViewById(R.id.textLoginPassword);
        progressBar = findViewById(R.id.progressBarLogin);
        progressBar.setVisibility(View.GONE);
    }

    public void startLogin(View view) {
        progressBar.setVisibility(View.VISIBLE);
        String textEmail = email.getText().toString();
        String textPassword = password.getText().toString();

        if (!textEmail.isEmpty() && !textPassword.isEmpty())
            login(new User(textEmail, textPassword));
        else
            MessageHelper.showLongToast("Preencha todos os campos");
    }

    private void login(User user) {
        auth.signInWithEmailAndPassword(
          user.getEmail(),
          user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    MessageHelper.showLongToast("Usuário logado");
                    startMainActivity();
                } else {
                    try {
                        throw (task.getException());
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        MessageHelper.showLongToast("Email ou senha inválidos");
                    } catch (FirebaseAuthInvalidUserException e) {
                        MessageHelper.showLongToast("Usuário não cadastrado");
                    } catch (Exception e) {
                        MessageHelper.showLongToast("Algo deu errado: " + e.getMessage());
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    public void navigateToRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}