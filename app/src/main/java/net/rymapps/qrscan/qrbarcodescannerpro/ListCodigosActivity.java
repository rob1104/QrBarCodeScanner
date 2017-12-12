package net.rymapps.qrscan.qrbarcodescannerpro;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListCodigosActivity extends AppCompatActivity {

    SQLiteHelper mDatabaseHelper;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_codigos);

        mDatabaseHelper = new SQLiteHelper(this, "bd_codigos", null, 1);
        lv = findViewById(R.id.lvCodigos);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Hola, Posicion: " + i + ", Id: " + l, Toast.LENGTH_LONG).show();
                return false;
            }
        });

        populateListView();
    }

    private void populateListView() {
        Log.d("ListCodigosActivity", "Listing codes on ListView");
        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()) {
            listData.add(data.getString(1));
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        lv.setAdapter(adapter);
    }


}
