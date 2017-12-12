package net.rymapps.qrscan.qrbarcodescannerpro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.rymapps.qrscan.qrbarcodescannerpro.entidades.Codigo;

/**
 * Created by rober on 06/12/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    final String CREAR_TABLE_CODIGOS = "CREATE TABLE codigos(id INTEGER PRIMARY KEY AUTOINCREMENT, contenido TEXT)";

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREAR_TABLE_CODIGOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS codigos");
        onCreate(sqLiteDatabase);
    }

    public boolean addData(Codigo codigo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("contenido", codigo.getContenido());

        long result = db.insert("codigos", null, values);

        return result == 1;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM codigos ORDER BY id DESC";
        return db.rawQuery(query, null);
    }
}
