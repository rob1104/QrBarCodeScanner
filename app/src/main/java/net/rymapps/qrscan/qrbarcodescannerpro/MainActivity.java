package net.rymapps.qrscan.qrbarcodescannerpro;

import android.app.ListActivity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.Result;

import net.rymapps.qrscan.qrbarcodescannerpro.entidades.Codigo;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private SQLiteHelper con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkPermission()) {
                Toast.makeText(MainActivity.this, getString(R.string.permission_already_granted), Toast.LENGTH_LONG).show();
            }
            else {
                requestPermission();
            }
        }

        con = new SQLiteHelper(this, "bd_codigos", null, 1);
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(MainActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] { CAMERA }, REQUEST_CAMERA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.view_history:
                CallHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void CallHistory() {
        Intent i = new Intent(this, ListCodigosActivity.class);
        startActivity(i);;
    }

    public void onRequestPermissionResult(int requestCode, String permission[], int grantResults[]) {
        switch(requestCode) {
            case REQUEST_CAMERA:
                if(grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted) {
                        Toast.makeText(MainActivity.this, getString(R.string.permission_granted), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(shouldShowRequestPermissionRationale(CAMERA)) {
                                displayAlertMessage(getString(R.string.permission_message),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[] { CAMERA }, REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
            else {
                requestPermission();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), listener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.scan_result));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                scannerView.resumeCameraPreview(MainActivity.this);
            }
        });
        if( Patterns.WEB_URL.matcher(scanResult.toLowerCase()).matches()) {
            builder.setNeutralButton(getString(R.string.visit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                    startActivity(intent);
                }
            });
        }
        else {
            builder.setNeutralButton(getString(R.string.copy), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ClipboardManager _clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    _clipboard.setText(scanResult);
                    Toast.makeText(MainActivity.this, getString(R.string.copy_message), Toast.LENGTH_LONG).show();
                }
            });
        }

        Codigo codigo = new Codigo(result.getText());

        con.addData(codigo);

        builder.setMessage(scanResult);
        AlertDialog alert = builder.create();
        alert.show();
    }

}
