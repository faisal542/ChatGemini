package com.testapp.chatgimmini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.ai.client.generativeai.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private GenerativeModelFutures model;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    RecyclerView recyclerViewChat;
    String key="AIzaSyAEsDbSDqi8z_9J30HfXdzAePkQZh82ouM";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GenerativeModel gm = new GenerativeModel(
                /* modelName */ "gemini-1.5-flash",
                /* apiKey */ key
        );
        model = GenerativeModelFutures.from(gm);
        EditText editTextInput = findViewById(R.id.editTextInput);

        Button buttonSend = findViewById(R.id.buttonSend);

        // Initialize the chat messages list and adapter
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);

        // Setup RecyclerView
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);
        // Set up the button click listener
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = editTextInput.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    // Add user message to the list
                    chatMessages.add(new ChatMessage(userMessage, true));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);

                    // Scroll to the latest message
                    recyclerViewChat.scrollToPosition(chatMessages.size() - 1);

                    // Clear the input
                    editTextInput.setText("");

                    // Call the API to generate content
                    generateContent(userMessage);
                }
            }
        });
    }

    private void generateContent(String prompt) {
        // Build the content object
        Content content = new Content.Builder().addText(prompt).build();

        // Use a single-threaded executor for illustrative purposes
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        // Add a callback to handle the response
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        // Update the TextView with the result text
                        String aiResponse = result.getText();
                        runOnUiThread(() -> chatMessages.add(new ChatMessage(aiResponse, false)));
                        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Handle errors, update the TextView with error message
                        runOnUiThread(() -> chatMessages.add(new ChatMessage("Error: " + t.getMessage(), false)));
                        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
                        t.printStackTrace();
                    }
                },
                executor
        );
    }
}