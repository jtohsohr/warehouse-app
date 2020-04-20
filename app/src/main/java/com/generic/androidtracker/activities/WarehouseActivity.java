package com.generic.androidtracker.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.generic.androidtracker.R;
import com.generic.androidtracker.adapters.WarehouseAdapter;
import com.generic.models.Warehouse;
import com.generic.models.WarehouseFactory;

public class WarehouseActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_recycler_view);

        RecyclerView recyclerView = findViewById(R.id.cardList);

        Toolbar toolbar =  findViewById(R.id.my_toolbar);
        toolbar.setTitle("Warehouses");
        toolbar.inflateMenu(R.menu.bottom_nav_menu);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        WarehouseFactory warehouseFactory = WarehouseFactory.getInstance();
        Warehouse warehouse = new Warehouse("W121", "12100");
        Warehouse warehouse2 = new Warehouse("W1213", "12110");
        Warehouse warehouse3 = new Warehouse("W1293", "10110");
        Warehouse warehouse4 = new Warehouse("W1223", "11110");
        Warehouse warehouse5 = new Warehouse("W1203", "14110");
        Warehouse warehouse6 = new Warehouse("W1293", "10170");
        Warehouse warehouse7 = new Warehouse("W1293", "15110");


        warehouseFactory.addWarehouse(warehouse);
        warehouseFactory.addWarehouse(warehouse2);
        warehouseFactory.addWarehouse(warehouse3);
        warehouseFactory.addWarehouse(warehouse4);
        warehouseFactory.addWarehouse(warehouse5);
        warehouseFactory.addWarehouse(warehouse6);
        warehouseFactory.addWarehouse(warehouse7);




        WarehouseAdapter warehouseAdapter = new WarehouseAdapter(warehouseFactory.getWarehousesList());

        recyclerView.setAdapter(warehouseAdapter);

    }

}
