package com.example.bancomvc.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bancomvc.model.Aluno;
import com.example.bancomvc.util.Conexao;

import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    private Conexao conexao;
    private SQLiteDatabase banco;

    // construtor
    public AlunoDAO(Context context){
        // Abrir a conexão com o banco
        conexao = new Conexao(context);
        banco = conexao.getWritableDatabase(); // Para escrita no banco
    }

    // Método para inserir dados
    public long insert(Aluno aluno){
        ContentValues values = new ContentValues();
        values.put("nome", aluno.getNome());
        values.put("cpf", aluno.getCpf());
        values.put("telefone", aluno.getTelefone());
        return banco.insert("aluno", null, values); // Inserindo os dados na tabela
    }

    // Método para verificar se o CPF já está cadastrado
    public boolean isCpfCadastrado(String cpf) {
        SQLiteDatabase db = conexao.getReadableDatabase(); // Para leitura no banco
        Cursor cursor = null;
        boolean existe = false;

        try {
            String query = "SELECT 1 FROM aluno WHERE cpf = ? LIMIT 1";
            cursor = db.rawQuery(query, new String[]{cpf});
            existe = cursor.moveToFirst(); // Retorna true se encontrou algum registro
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return existe;
    }

    // Deletar aluno por CPF
    public boolean deletarAlunoPorCpf(String cpf) {
        SQLiteDatabase db = conexao.getWritableDatabase(); // Para escrita no banco
        int linhasAfetadas = db.delete("aluno", "cpf = ?", new String[]{cpf});
        return linhasAfetadas > 0; // Retorna true se ao menos uma linha foi afetada (deletada)
    }


    // Método para atualizar dados
    public void update(Aluno aluno){
        ContentValues values = new ContentValues();
        values.put("nome", aluno.getNome());
        values.put("cpf", aluno.getCpf());
        values.put("telefone", aluno.getTelefone());
        String[] args = {aluno.getId().toString()};
        banco.update("aluno", values, "id=?", args);
    }

    // Método para deletar dados
    public void delete(Aluno aluno){
        String[] args = {aluno.getId().toString()};
        banco.delete("aluno", "id=?", args);
    }

    // Consultar todos os alunos
    public List<Aluno> obterAlunos(){
        List<Aluno> alunos = new ArrayList<>();

        Cursor cursor = banco.query("aluno", new String[]{"id", "nome", "cpf", "telefone"},
                null, null, null, null, null);
        while(cursor.moveToNext()){
            Aluno a = new Aluno();
            a.setId(cursor.getInt(0));
            a.setNome(cursor.getString(1));
            a.setCpf(cursor.getString(2));
            a.setTelefone(cursor.getString(3));
            alunos.add(a);
        }
        cursor.close();
        return alunos;
    }

    // Consultar aluno específico por ID
    public Aluno read(Integer id) {
        String[] args = {String.valueOf(id)};
        Aluno a = null;  // Altere para 'null' em vez de criar um novo objeto 'Aluno'

        Cursor cursor = banco.query("aluno", new String[]{"id", "nome", "cpf", "telefone"},
                "id=?", args, null, null, null, null);

        // Verifique se o cursor tem algum dado
        if (cursor != null && cursor.moveToFirst()) {
            a = new Aluno();
            a.setId(cursor.getInt(0));
            a.setNome(cursor.getString(1));
            a.setCpf(cursor.getString(2));
            a.setTelefone(cursor.getString(3));
        }

        // Certifique-se de fechar o cursor
        if (cursor != null) {
            cursor.close();
        }

        return a;  // Retorna 'null' se não encontrar o aluno
    }

}
