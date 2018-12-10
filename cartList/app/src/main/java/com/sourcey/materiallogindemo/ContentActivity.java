package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sourcey.materiallogindemo.R;

import org.w3c.dom.Text;

public class ContentActivity extends AppCompatActivity {
    private Button btn_back;
    private TextView textNumber;
    private TextView textName;
    private TextView textPrice;
    private TextView textContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        textNumber = (TextView)findViewById(R.id.textNumber);
        textName = (TextView)findViewById(R.id.textName);
        textPrice = (TextView)findViewById(R.id.textPrice);
        textContent = (TextView)findViewById(R.id.textContent);

        textNumber.setText(ListActivity.number);
        textName.setText(ListActivity.name);
        textPrice.setText(ListActivity.price);
        textContent.setText(ListActivity.content);

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
