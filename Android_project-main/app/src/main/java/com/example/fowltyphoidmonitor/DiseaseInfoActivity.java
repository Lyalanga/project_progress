package com.example.fowltyphoidmonitor;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DiseaseInfoActivity extends AppCompatActivity {

    TextView btnCauses, btnSymptoms, btnTreatment, btnPrevention, txtInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_info);

        btnCauses = findViewById(R.id.btnCauses);
        btnSymptoms = findViewById(R.id.btnSymptoms);
        btnTreatment = findViewById(R.id.btnTreatment);
        btnPrevention = findViewById(R.id.btnPrevention);
        txtInfo = findViewById(R.id.txtInfo);

        btnCauses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtInfo.setText(R.string.fowl_typhoid_husababishwa_na_bakteria_aitwaye_salmonella_gallinarum_huenea_kwa_njia_ya_chakula_au_maji_machafu);
            }
        });

        btnSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtInfo.setText(R.string.dalili_kuu_ni_homa_kali_kuhara_ya_manjano_kupungua_mayai_kukosa_hamu_ya_kula_na_udhaifu);
            }
        });

        btnTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtInfo.setText("Matibabu yanajumuisha kutumia antibiotics kama Sulfonamides na Tetracycline. Tafadhali wasiliana na mtaalamu kabla ya kutumia.");
            }
        });

        btnPrevention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtInfo.setText(R.string.zuia_ugonjwa_kwa_usafi_wa_vyombo_chanjo_ya_mara_kwa_mara_na_udhibiti_wa_maambukizi_kati_ya_kuku);
            }
        });
    }
}
