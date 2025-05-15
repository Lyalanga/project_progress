package com.example.fowltyphoidmonitor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.widget.Toast;

public class VetConsultationActivity extends AppCompatActivity {

    private ListView chatListView;
    private EditText messageEditText;
    private Button sendButton;
    private ArrayList<String> chatMessages;
    private ArrayAdapter<String> chatAdapter;

    // Role selection (for demo purposes - in a real app you might determine this from login)
    private boolean isVeterinarian = false; // Default as farmer
    private Button switchRoleButton;
    private TextView currentRoleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_consultation);

        // Initialize UI components
        chatListView = findViewById(R.id.chatListView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        switchRoleButton = findViewById(R.id.switchRoleButton);
        currentRoleTextView = findViewById(R.id.currentRoleTextView);

        // Initialize chat components
        chatMessages = new ArrayList<>();
        chatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatMessages);
        chatListView.setAdapter(chatAdapter);

        // Add initial system message
        String welcomeMessage = "Karibu kwenye Mazungumzo ya Fowl Typhoid Monitor. " +
                "Tumia nafasi hii kushauriana kuhusu masuala ya afya ya kuku.";
        addSystemMessage(welcomeMessage);

        // Add sample messages for demonstration
        addSampleMessages();

        // Set up click listeners
        setupClickListeners();

        // Update the role display
        updateRoleDisplay();
    }

    private void setupClickListeners() {
        // Send button click listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Switch role button (for demonstration purposes)
        switchRoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVeterinarian = !isVeterinarian;
                updateRoleDisplay();
                Toast.makeText(VetConsultationActivity.this,
                        "Umebadili kuwa " + (isVeterinarian ? "Daktari wa Mifugo" : "Mkulima"),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRoleDisplay() {
        currentRoleTextView.setText("Wajibu wa sasa: " + (isVeterinarian ? "Daktari wa Mifugo" : "Mkulima"));
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            String sender = isVeterinarian ? "Daktari" : "Mkulima";
            String timestamp = getCurrentTimestamp();
            String formattedMessage = sender + " [" + timestamp + "]: " + messageText;

            chatMessages.add(formattedMessage);
            chatAdapter.notifyDataSetChanged();

            // Auto-scroll to the bottom
            chatListView.smoothScrollToPosition(chatMessages.size() - 1);

            // Clear the input field
            messageEditText.setText("");

            // For demo purposes, add a simulated response if the user is a farmer
            if (!isVeterinarian) {
                simulateVetResponse();
            } else {
                simulateFarmerResponse();
            }
        }
    }

    private void simulateVetResponse() {
        // Simulate a delay before the vet responds (1.5 seconds)
        chatListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] responses = {
                        "Kulingana na dalili ulizoeleza, inaweza kuwa ishara za awali za typhoid ya kuku. Je, unaweza kuangalia kama kuku wengine wanaonyesha dalili sawa?",
                        "Umeona dalili hizi kwa muda gani katika kundi lako?",
                        "Napendekeza kuwatenga kuku walioathirika mara moja. Je, kumekuwa na ongezeko lolote la hivi karibuni katika kundi lako?",
                        "Tafadhali fuatilia kiwango cha maji wanachokunywa na uhakikishe wana maji safi yanayopatikana.",
                        "Ninaweza kutembelea shamba lako kesho asubuhi kuchunguza kuku moja kwa moja. Je, hilo litafaa kwako?"
                };

                int randomIndex = (int) (Math.random() * responses.length);
                String timestamp = getCurrentTimestamp();
                String response = "Daktari [" + timestamp + "]: " + responses[randomIndex];

                chatMessages.add(response);
                chatAdapter.notifyDataSetChanged();
                chatListView.smoothScrollToPosition(chatMessages.size() - 1);
            }
        }, 1500);
    }

    private void simulateFarmerResponse() {
        // Simulate a delay before the farmer responds (1.5 seconds)
        chatListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] responses = {
                        "Nimeona kuku kadhaa wana hamu ndogo ya kula na kuhara manjano tangu jana.",
                        "Ndiyo, kuku wengine wapatao 5 wanaonyesha dalili sawa sasa. Upanga wao pia unaonekana mweupe.",
                        "Niliongeza kuku 10 wapya katika kundi wiki iliyopita kutoka sokoni.",
                        "Matumizi yao ya maji yamepungua sana katika masaa 24 yaliyopita.",
                        "Asante, daktari. Kesho asubuhi inafaa kwangu. Je, ninapaswa kuandaa chochote kabla ya ziara yako?"
                };

                int randomIndex = (int) (Math.random() * responses.length);
                String timestamp = getCurrentTimestamp();
                String response = "Mkulima [" + timestamp + "]: " + responses[randomIndex];

                chatMessages.add(response);
                chatAdapter.notifyDataSetChanged();
                chatListView.smoothScrollToPosition(chatMessages.size() - 1);
            }
        }, 1500);
    }

    private void addSystemMessage(String message) {
        String timestamp = getCurrentTimestamp();
        String formattedMessage = "Mfumo [" + timestamp + "]: " + message;
        chatMessages.add(formattedMessage);
        chatAdapter.notifyDataSetChanged();
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void addSampleMessages() {
        // Add some sample messages to demonstrate the conversation
        chatMessages.add("Mkulima [10:30]: Hujambo daktari, ninaona tabia za ajabu kwa kuku wangu.");
        chatMessages.add("Daktari [10:32]: Hujambo! Unaweza kuelezea unachoona?");
        chatMessages.add("Mkulima [10:33]: Kuku kadhaa wanaonekana dhaifu na wana kuhara kwa rangi ya kijani.");
        chatMessages.add("Daktari [10:35]: Kuku wangapi wameathirika? Je, kuna mabadiliko yoyote ya hivi karibuni katika chakula au maji?");
    }
}