package com.example.bancomvc;

import android.content.Intent;
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

public class LeitorQrCodeSaida extends AppCompatActivity {

    private ImageButton btnScanSaida;
    private TextView txtResultCorrect;
    private TextView txtResultWrong;
    private Button btnVoltar;  // Botão de voltar
    private AlunoDAO alunoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leitor_qr_code_saida);

        alunoDAO = new AlunoDAO(this);
        btnScanSaida = findViewById(R.id.btnScanSaida);
        txtResultWrong = findViewById(R.id.txtResultWrong);
        txtResultCorrect = findViewById(R.id.txtResultCorrect);
        btnVoltar = findViewById(R.id.btnVoltar);  // Inicializa o botão de voltar

        btnScanSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarLeituraQRCode();
            }
        });

        // Ação do botão de voltar
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Finaliza a atividade e volta para a tela anterior
            }
        });
    }

    private void iniciarLeituraQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Escaneie o QR Code de Saída");
        integrator.setCameraId(0);
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
                String qrCodeContent = result.getContents();
                processarQRCodeSaida(qrCodeContent);
            } else {
                txtResultWrong.setText("Leitura cancelada");
            }
        }
    }

    private void processarQRCodeSaida(String qrCodeContent) {
        try {
            // Exemplo de QR Code: "ID:1, Nome:João, CPF:12345678900, Telefone:987654321"
            // Dividir a string pelo delimitador ", "
            String[] dados = qrCodeContent.split(", ");

            // Variáveis para armazenar os dados extraídos
            String id = dados[0].split(":")[1].trim();  // ID
            String nome = dados[1].split(":")[1].trim();  // Nome
            String cpf = dados[2].split(":")[1].trim();  // CPF
            String telefone = dados[3].split(":")[1].trim();  // Telefone

            // Verificar se o CPF está cadastrado
            if (alunoDAO.isCpfCadastrado(cpf)) {
                // Tenta deletar o aluno após confirmar a saída
                boolean deletado = alunoDAO.deletarAlunoPorCpf(cpf);

                if (deletado) {
                    Toast.makeText(this, "Saída liberada: " + nome, Toast.LENGTH_SHORT).show();
                    txtResultCorrect.setText("Saída liberada para: \n\n" +
                            "ID: " + id + "\n" +
                            "Nome: " + nome + "\n" +
                            "CPF: " + cpf + "\n" +
                            "Telefone: " + telefone);
                } else {
                    Toast.makeText(this, "Erro ao remover cadastro do CPF: " + cpf, Toast.LENGTH_SHORT).show();
                    txtResultWrong.setText("Erro ao remover cadastro do CPF: " + cpf);
                }
            } else {
                Toast.makeText(this, "CPF não registrado!", Toast.LENGTH_SHORT).show();
                txtResultWrong.setText("CPF não registrado!");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao processar QR Code: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
