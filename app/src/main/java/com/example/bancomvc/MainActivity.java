package com.example.bancomvc;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bancomvc.dao.AlunoDAO;
import com.example.bancomvc.model.Aluno;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtNome;
    private EditText edtCpf;
    private EditText edtTelefone;
    private EditText edtListar;
    private Button btnCadastrar;
    private Button btnListar;
    private Button btnLimpar;
    private Button btnProximo;
    private Button btnSair;
    private AlunoDAO dao;
    private List<Aluno> alunos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        edtNome = findViewById(R.id.edtNome);
        edtCpf = findViewById(R.id.edtCpf);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtListar = findViewById(R.id.edtListar);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnListar = findViewById(R.id.btnListar);
        btnLimpar = findViewById(R.id.btnLimpar);
        btnProximo = findViewById(R.id.btnProximo);
        btnSair = findViewById(R.id.btnSair);

        // botão cadastrar
        btnCadastrar.setOnClickListener(view -> {
            // Obter os dados dos campos
            String nome = edtNome.getText().toString();
            String cpf = edtCpf.getText().toString();
            String telefone = edtTelefone.getText().toString();

            // Verificar se algum campo está vazio
            if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Todos os campos devem ser preenchidos", Toast.LENGTH_SHORT).show();
                return; // Interrompe a execução se algum campo estiver vazio
            }

            // Verificar se o CPF já está cadastrado
            dao = new AlunoDAO(this);
            boolean isCpfExistente = dao.isCpfCadastrado(cpf); // Método que você criará no DAO para verificar CPF

            if (isCpfExistente) {
                Toast.makeText(getApplicationContext(), "Este CPF já está cadastrado!", Toast.LENGTH_SHORT).show();
                return; // Interrompe a execução se o CPF já existir
            }

            // Caso o CPF não esteja cadastrado, cria o aluno e faz o cadastro
            Aluno aluno = new Aluno();
            aluno.setNome(nome);
            aluno.setCpf(cpf);
            aluno.setTelefone(telefone);
            long id = dao.insert(aluno);

            if (id > 0) {
                Toast.makeText(getApplicationContext(), "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
            }
        });

        // botão listar
        btnListar.setOnClickListener(view -> {
            edtListar.setText("");
            dao = new AlunoDAO(this);
            alunos = dao.obterAlunos();

            // Verifica se a lista está vazia ou null
            if (alunos == null || alunos.isEmpty()) {
                edtListar.append("\nLista vazia!");
            }
            // Se houver alunos, adicione-os à lista
            for (Aluno aluno : alunos) {
                edtListar.append("\nID: " + aluno.getId() + "\n");
                edtListar.append("Nome: " + aluno.getNome() + "\n");
                edtListar.append("CPF: " + aluno.getCpf() + "\n");
                edtListar.append("Telefone: " + aluno.getTelefone() + "\n");
            }
        });

        // botão limpar
        btnLimpar.setOnClickListener(view ->{
            edtNome.setText(null);
            edtCpf.setText(null);
            edtTelefone.setText(null);
            edtListar.setText(null);
        });

        // botão proximo
        btnProximo.setOnClickListener(view ->{
            Intent it = new Intent(getApplicationContext(), Manutencao.class);
            startActivity(it);
        });

        // botão sair
        btnSair.setOnClickListener(view ->{
          finish();
        });
    }
}