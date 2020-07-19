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
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.utils.MessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterActivity extends AppCompatActivity {


    EditText name, email, password;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViewElements();
    }

    private void initViewElements() {
        name = findViewById(R.id.textRegisterName);
        email = findViewById(R.id.textRegisterEmail);
        password = findViewById(R.id.textRegisterPassword);
        progressBar = findViewById(R.id.progressBarRegister);
        progressBar.setVisibility(View.GONE);
    }

    public void startRegister(View view) {
        progressBar.setVisibility(View.VISIBLE);
        String textName = name.getText().toString();
        String textEmail = email.getText().toString();
        String textPassword = password.getText().toString();

        if (!textName.isEmpty() && !textEmail.isEmpty() && !textPassword.isEmpty())
            register(new User(textName, textEmail, textPassword));
        else
            MessageHelper.showLongToast("Preencha todos os campos");
    }


    private void register(final User user) {
    FirebaseConfig.getAuth()
        .createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user.setId(task.getResult().getUser().getUid());
                    try {
                        UserHelper.updateProfileName(user.getName());
                        UserHelper.saveOnDatabase(user);
                        MessageHelper.showLongToast("Usuário criado com sucesso");
                        startMainActivity();
                    } catch (Exception e) {
                        MessageHelper.showLongToast("Erro ao cadastrar o usuário" + e.getMessage());
                    }
                } else {
                    try {
                        throw (task.getException());
                    } catch (FirebaseAuthInvalidUserException e) {
                        MessageHelper.showLongToast("email inválido");
                    } catch (FirebaseAuthWeakPasswordException e) {
                        MessageHelper.showLongToast("Senha fraca, insira uma que contenha letras e números");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        MessageHelper.showLongToast("Email ou senha inválidos");
                    } catch (Exception e) {
                        MessageHelper.showLongToast("Erro ao cadastrar o usuário: " + e.getMessage());
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
}