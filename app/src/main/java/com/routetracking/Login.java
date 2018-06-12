package com.routetracking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    private TextInputLayout emailInput, passwordInput;
    private EditText emailEdit, passwordEdit;
    private Button btnLogin;
    private View focusView = null;
    private boolean cancel = false;
    private String getEmail, getPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        bindResources();
        clickedLogin();
    }


    private void bindResources() {
        emailInput = findViewById(R.id.email_input);
        emailEdit = findViewById(R.id.email_edit);
        passwordInput = findViewById(R.id.password_input);
        passwordEdit = findViewById(R.id.password_edit);
        btnLogin = findViewById(R.id.btn_login);

    }


    private void getLoginData() {

        emailInput.setError(null);
        passwordInput.setError(null);

        emailInput.setErrorEnabled(false);
        passwordInput.setErrorEnabled(false);

        getEmail = emailEdit.getText().toString().replaceAll(" ", "");
        getPass = passwordEdit.getText().toString().replaceAll(" ", "");


    }


    private boolean validate() {
        String EMAIL_REGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";

        getLoginData();

        if (TextUtils.isEmpty(getEmail)) {
            emailInput.setErrorEnabled(true);
            emailInput.setError((getString(R.string.please_enter_e_mail)));
            focusView = emailInput;
            cancel = true;
            return false;

        } else if (!getEmail.matches(EMAIL_REGEX)) {
            emailInput.setErrorEnabled(true);
            emailInput.setError((getString(R.string.invalid_email)));
            focusView = emailInput;
            cancel = true;
            return false;

        } else if (TextUtils.isEmpty(getPass)) {
            passwordInput.setErrorEnabled(true);
            passwordInput.setError((getString(R.string.please_enter_password)));
            focusView = passwordInput;
            cancel = true;
            return false;

        } else {
            return true;
        }
    }


    private void clickedLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    SharedPreferences sessPreferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
                    SharedPreferences.Editor editor = sessPreferences.edit();
                    editor.putBoolean("Islogin", true).apply();
                    editor.apply();

                    Intent goToHomeScreen = new Intent(Login.this, Home.class);
                    startActivity(goToHomeScreen);
                    finish();

                } else {
                    if (focusView != null)
                        focusView.requestFocus();
                }

            }
        });
    }


}
