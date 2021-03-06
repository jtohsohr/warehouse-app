package com.generic.androidtracker.warehousemvp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.generic.androidtracker.interfaces.AddWarehouseDialogListener;
import com.generic.androidtracker.interfaces.OnWarehouseListener;
import com.generic.androidtracker.shipmentmvp.ShipmentActivity;
import com.generic.androidtracker.R;
import com.generic.androidtracker.interfaces.WarehouseTrackerMVP;
import com.generic.androidtracker.WarehouseApplication;
import com.generic.models.Warehouse;
import com.generic.utils.IParser;
import com.generic.utils.JsonParser;
import com.generic.utils.XmlParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class WarehouseActivity extends AppCompatActivity
        implements WarehouseTrackerMVP.WarehouseView, AddWarehouseDialogListener, OnWarehouseListener {

    public static final int REQUEST_CODE = 4;
    public static final String JSON_MIME_TYPE = "application/json";
    public static final String XML_MIME_TYPE = "text/xml";
    private List<Warehouse> warehouses;
    private WarehouseTrackerMVP.WarehousePresenter presenter;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_recycler_view);

        Toolbar toolbar =  findViewById(R.id.my_toolbar);
        toolbar.setTitle("Warehouses");
        toolbar.inflateMenu(R.menu.warehouse_menu);
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });

        verifyStoragePermissions(this);

        final WarehouseApplication application = (WarehouseApplication) getApplication();

        presenter = new WarehouseActivityPresenter(application);
        presenter.setView(this);

        FloatingActionButton addButton = findViewById(R.id.fab);

        addButton.setOnClickListener(e -> presenter.addWarehouseClicked());

    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity the activity getting permissions
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * Imports data from file explorer.
     * @param mimeType the file mime type.
     */
    public void importData(String mimeType)
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if (data != null){
                Uri uri = data.getData();
                String path = uri.getPath();
                path = path.substring(path.indexOf(":") + 1);
                String type = path.substring(path.indexOf(".") + 1);
                parseData(type, path);

            }
        }
    }

    /**
     * Handles parsing of file.
     * @param type file format.
     * @param path path of file.
     */
    private void parseData(String type, String path) {

        if (type.equalsIgnoreCase("json")){
            IParser jsonParser = new JsonParser();
            try {
                jsonParser.parse(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            IParser xmlParser = new XmlParser();
            try {
                xmlParser.parse(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        presenter.setView(this);
    }

    @Override
    public void showWarehouses(List<Warehouse> warehouses) {
        this.warehouses = warehouses;
        RecyclerView recyclerView = findViewById(R.id.cardList);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        WarehouseRecyclerAdapter warehouseRecyclerAdapter = new WarehouseRecyclerAdapter(warehouses, this);

        recyclerView.setAdapter(warehouseRecyclerAdapter);
    }

    @Override
    public void showAddNewWarehouse() {
        showEditDialog();
    }

    private void showEditDialog(){
        FragmentManager fm = getSupportFragmentManager();
        AddWarehouseDialogFragment editNameDialogFragment = AddWarehouseDialogFragment.newInstance("Add New Warehouse");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    @Override
    public void onFinishEditDialog(String warehouseIDInput,
                                   String warehouseNameInput,
                                   String freightStatus) {

        presenter.addWarehouseCompleted(warehouseNameInput, warehouseIDInput, freightStatus);
        presenter.setView(this);

    }

    @Override
    public void onBackPressed(){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onWarehouseClicked(int position) {
        String warehouseID = warehouses.get(position).getId();
        boolean freightStatus = warehouses.get(position).getFreightReceiptEnabled();
        Intent intent = new Intent(this, ShipmentActivity.class);
        intent.putExtra("warehouseID", warehouseID);
        intent.putExtra("freightStatus", freightStatus);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_data:
                Toast.makeText(this, "Import Data Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.export_data:
                Toast.makeText(this, "Export Data Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.import_json:
                importData(JSON_MIME_TYPE);
                Toast.makeText(this, "Import JSON Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.import_xml:
                importData(XML_MIME_TYPE);
                Toast.makeText(this, "Import XML Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.export_json:
                Toast.makeText(this, "Export JSON Selected", Toast.LENGTH_SHORT).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
