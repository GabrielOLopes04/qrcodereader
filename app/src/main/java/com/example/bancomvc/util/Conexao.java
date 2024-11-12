package com.example.bancomvc.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Conexao extends SQLiteOpenHelper {

    // declarando banco de dados
    private static final String NAME="cadastro_aluno.db";
    private static final int VERSION=1;

    // construtor
    public Conexao( Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // criando tabela no banco de dados (ID chave primaria)
        db.execSQL("CREATE TABLE aluno (id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "nome VARCHAR(50), cpf VARCHAR(50), telefone VARCHAR(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS aluno";
        db.execSQL(sql);
        onCreate(db);
    }

    // Método para obter a instância do banco de dados para leitura
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    // Método para obter a instância do banco de dados para escrita
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }
}
