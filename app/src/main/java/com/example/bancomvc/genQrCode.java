package com.example.bancomvc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.example.bancomvc.dao.AlunoDAO;
import com.example.bancomvc.model.Aluno;

public class genQrCode extends AppCompatActivity {

    private ImageButton btnScan;
    private TextView txtResultWrong;
    private TextView txtResultCorrect;
    private Button btnManutencao;
    private Button btnSaida;  // Novo botão para redirecionar para o leitor de QR Code de saída
    private AlunoDAO alunoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gen_qr_code);

        getWindow().setStatusBarColor(Color.parseColor("#E0F3FD"));
        getWindow().setNavigationBarColor(Color.parseColor("#E0F3FD"));

        // Inicializando o DAO para interagir com o banco de dados
        alunoDAO = new AlunoDAO(this);

        // Inicializando os elementos da interface
        btnScan = findViewById(R.id.btnScan);
        txtResultWrong = findViewById(R.id.txtResultWrong);
        txtResultCorrect = findViewById(R.id.txtResultCorrect);
        btnManutencao = findViewById(R.id.btnManutencao);
        btnSaida = findViewById(R.id.btnSaida);  // Botão para o leitor de saída

        // Configurar ação do botão para iniciar a leitura do QR Code de entrada
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarLeituraQRCode();
            }
        });

        // Configurar ação do botão de Manutenção
        btnManutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar para a tela de Manutencao
                Intent intent = new Intent(genQrCode.this, Manutencao.class);
                startActivity(intent);
            }
        });

        // Configurar ação do botão de Saída
        btnSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecionar para a tela de LeitorQrCodeSaida
                Intent intent = new Intent(genQrCode.this, LeitorQrCodeSaida.class);
                startActivity(intent);
            }
        });
    }

    private void iniciarLeituraQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Escaneie o código QR");
        integrator.setCameraId(0); // 0 é a câmera traseira
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Obter o conteúdo do QR Code
                String qrCodeContent = result.getContents();

                // Processar o conteúdo do QR Code para cadastrar o aluno
                processarQRCode(qrCodeContent);
            } else {
                txtResultWrong.setText("Leitura cancelada");
            }
        }
    }

    private void processarQRCode(String qrCodeContent) {
        try {
            // Exemplo de QR Code: "ID:1, Nome:João, CPF:12345678900, Telefone:987654321"
            // Dividir a string pelo delimitador ", "
            String[] dados = qrCodeContent.split(", ");

            // Variáveis para armazenar os dados extraídos
            String id = dados[0].split(":")[1].trim();
            String nome = dados[1].split(":")[1].trim();
            String cpf = dados[2].split(":")[1].trim();
            String telefone = dados[3].split(":")[1].trim();

            // Verificar se o CPF já está cadastrado
            if (alunoDAO.isCpfCadastrado(cpf)) {
                Toast.makeText(this, "CPF já cadastrado!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Criar um objeto aluno com os dados extraídos
            Aluno aluno = new Aluno();
            aluno.setId(Integer.parseInt(id));  // Pode ser deixado vazio caso o ID seja autoincrementado
            aluno.setNome(nome);
            aluno.setCpf(cpf);
            aluno.setTelefone(telefone);

            // Salvar o aluno no banco de dados
            long alunoId = alunoDAO.insert(aluno);

            if (alunoId > 0) {
                // Exibir a mensagem de sucesso
                Toast.makeText(this, "Aluno cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                // Exibir as informações do aluno com quebras de linha e a mensagem "Bem-vindo!"
                txtResultCorrect.setText("Bem-vindo!\n\n" +
                        "ID: " + aluno.getId() + "\n" +
                        "Nome: " + aluno.getNome() + "\n" +
                        "CPF: " + aluno.getCpf() + "\n" +
                        "Telefone: " + aluno.getTelefone());
            } else {
                // Caso o cadastro tenha falhado
                Toast.makeText(this, "Erro ao cadastrar aluno.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Caso haja erro ao processar o QR Code
            Toast.makeText(this, "Erro ao processar QR Code: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
