package com.example.fowltyphoidmonitor;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SymptomTrackerActivity extends AppCompatActivity {

    CheckBox chkFever, chkDiarrhea, chkLowEgg, chkLossAppetite, chkWeakness;
    Button btnSubmit;
    TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_tracker);

        chkFever = findViewById(R.id.chkFever);
        chkDiarrhea = findViewById(R.id.chkDiarrhea);
        chkLowEgg = findViewById(R.id.chkLowEgg);
        chkLossAppetite = findViewById(R.id.chkLossAppetite);
        chkWeakness = findViewById(R.id.chkWeakness);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtResult = findViewById(R.id.txtResult);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int score = 0;

                if (chkFever.isChecked()) score++;
                if (chkDiarrhea.isChecked()) score++;
                if (chkLowEgg.isChecked()) score++;
                if (chkLossAppetite.isChecked()) score++;
                if (chkWeakness.isChecked()) score++;

                if (score >= 3) {
                    txtResult.setText("Uwezekano mkubwa wa Fowl Typhoid. Tafadhali wasiliana na mtaalamu wa mifugo.");
                } else if (score == 2) {
                    txtResult.setText("Dalili zinaonyesha tahadhari. Angalia zaidi au wasiliana na mtaalamu.");
                } else {
                    txtResult.setText("Hatari ndogo. Endelea kufuatilia afya ya kuku wako.");
                }
            }
        });
    }
}
