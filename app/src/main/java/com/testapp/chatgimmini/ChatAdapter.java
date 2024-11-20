package com.testapp.chatgimmini;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        if (message.isUser()) {
            holder.userMessage.setVisibility(View.VISIBLE);
            holder.aiMessage.setVisibility(View.GONE);
            holder.userMessage.setText(message.getMessage());
        } else {
            holder.aiMessage.setVisibility(View.VISIBLE);
            holder.userMessage.setVisibility(View.GONE);
            holder.aiMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userMessage;
        TextView aiMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.textUserMessage);
            aiMessage = itemView.findViewById(R.id.textAIMessage);
        }
    }
}
