package com.rr.ars.ui.bancodedados;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MeuBancoDeDados.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "minhaTabela";
    private static final String UID = "UID";
    private static final String CODIGO_PRODUTO = "CodigoProduto";
    private static final String LOTE_PRODUTO = "LoteProduto";
    private static final String SUB_LOTE_PRODUTO = "SubLoteProduto";
    private static final String ALMOXARIFADO = "Almoxarifado";
    private static final String LOTE_FORNECEDOR = "LoteFornecedor";
    private static final String SERIE_NOTA = "SerieNota";
    private static final String NOTA_FISCAL = "NotaFiscal";
    private static final String ENDERECO_ESTOQUE = "EnderecoEstoque";


    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            UID + " VARCHAR(32) PRIMARY KEY," +
            CODIGO_PRODUTO + " VARCHAR(15)," +
            LOTE_PRODUTO + " VARCHAR(10)," +
            SUB_LOTE_PRODUTO + " VARCHAR(6)," +
            ALMOXARIFADO + " VARCHAR(2)," +
            LOTE_FORNECEDOR + " VARCHAR(18)," +
            SERIE_NOTA + " VARCHAR(3)," +
            NOTA_FISCAL + " VARCHAR(9)," +
            ENDERECO_ESTOQUE + " VARCHAR(9));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Métodos públicos para acessar os nomes das colunas
    public static String getCodigoProdutoColumnName() {
        return CODIGO_PRODUTO;
    }
    public static String getLoteProdutoColumnName() {
        return LOTE_PRODUTO;
    }
    public static String getSubLoteProdutoColumnName() {
        return SUB_LOTE_PRODUTO;
    }
    public static String getAlmoxarifadoColumnName() {
        return ALMOXARIFADO;
    }
    public static String getLoteFornecedorColumnName() {
        return LOTE_FORNECEDOR;
    }
    public static String getSerieNotaColumnName() {
        return SERIE_NOTA;
    }
    public static String getNotaFiscalColumnName() {
        return NOTA_FISCAL;
    }
    public static String getEnderecoEstoqueColumnName() {
        return ENDERECO_ESTOQUE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Apaga a tabela antiga se existir
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Cria a tabela novamente
        onCreate(db);
    }

    public boolean insertData(String uid, String codigoProduto, String loteProduto, String subLoteProduto, String almoxarifado, String loteFornecedor, String serieNota, String notaFiscal, String enderecoEstoque) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UID, uid);
        contentValues.put(CODIGO_PRODUTO, codigoProduto);
        contentValues.put(LOTE_PRODUTO, loteProduto);
        contentValues.put(SUB_LOTE_PRODUTO, subLoteProduto);
        contentValues.put(ALMOXARIFADO, almoxarifado);
        contentValues.put(LOTE_FORNECEDOR, loteFornecedor);
        contentValues.put(SERIE_NOTA, serieNota);
        contentValues.put(NOTA_FISCAL, notaFiscal);
        contentValues.put(ENDERECO_ESTOQUE, enderecoEstoque);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();

        // Se result == -1, então ocorreu um erro
        return result != -1;
    }

    public boolean isUidExist(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + UID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { uid });
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getDadosPorUid(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + UID + "=?", new String[]{uid});
    }

}
