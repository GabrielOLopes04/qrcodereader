package com.example.bancomvc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setStatusBarColor(Color.parseColor("#E0F3FD"));
        getWindow().setNavigationBarColor(Color.parseColor("#E0F3FD"));

        // Inicializando os componentes da tela
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Configurando o clique do botão de login
        btnLogin.setOnClickListener(view -> {
            String username = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();

            // Verificação simples de login (simulação)
            if (username.equals("admin") && password.equals("1234")) {  // Exemplo básico de verificação
                // Login bem-sucedido
                Toast.makeText(getApplicationContext(), "Login bem-sucedido", Toast.LENGTH_SHORT).show();

                // Redirecionando para a tela 'genQrCode'
                Intent intent = new Intent(Login.this, genQrCode.class);
                startActivity(intent);
                finish(); // Finaliza a tela de login para não voltar a ela
            } else {
                // Caso o login falhe
                Toast.makeText(getApplicationContext(), "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

