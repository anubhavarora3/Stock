package com.example.anubharora.stock.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.anubharora.stock.R;
import com.example.anubharora.stock.adapter.StockListAdapter;
import com.example.anubharora.stock.helper.CSVFile;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class StockListActivity extends AppCompatActivity {

    private ListView listView;
    private StockListAdapter stockListAdapter;
    private List<String[]> companyDetails;
    private EditText searchCompany;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);

        listView = (ListView) findViewById(R.id.stock_list);
        searchCompany = (EditText) findViewById(R.id.find);


        InputStream inputStream = getResources().openRawResource(R.raw.companylist);
        CSVFile csvFile = new CSVFile(inputStream);
        companyDetails = csvFile.read();

        stockListAdapter = new StockListAdapter(StockListActivity.this, companyDetails);
        listView.setAdapter(stockListAdapter);
        stockListAdapter.notifyDataSetChanged();


        searchCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //StockListActivity.this.stockListAdapter.getFilter().filter(charSequence);
                String text = searchCompany.getText().toString().toLowerCase(Locale.getDefault());
                stockListAdapter.filter(text);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }
}
