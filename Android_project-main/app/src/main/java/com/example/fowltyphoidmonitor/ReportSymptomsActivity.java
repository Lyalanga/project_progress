
package com.example.fowltyphoidmonitor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReportSymptomsActivity extends AppCompatActivity {
    CheckBox fever, diarrhea, lossAppetite;
    Button submitSymptoms;
    TextView suggestionOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_symptoms);

        fever = findViewById(R.id.fever);
        diarrhea = findViewById(R.id.diarrhea);
        lossAppetite = findViewById(R.id.lossAppetite);
        submitSymptoms = findViewById(R.id.submitSymptoms);
        suggestionOutput = findViewById(R.id.suggestionOutput);

        submitSymptoms.setOnClickListener(v -> {
            boolean hasFever = fever.isChecked();
            boolean hasDiarrhea = diarrhea.isChecked();
            boolean hasLossAppetite = lossAppetite.isChecked();

            if (hasFever && hasLossAppetite) {
                suggestionOutput.setText("Possible Fowl Typhoid. Consult a vet and isolate infected poultry.");
            } else if (hasDiarrhea) {
                suggestionOutput.setText("Mild condition. Provide clean water and monitor.");
            } else {
                suggestionOutput.setText("No major symptoms. Poultry seems healthy.");
            }
        });
    }
}
