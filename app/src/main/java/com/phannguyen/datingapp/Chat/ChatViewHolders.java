package com.phannguyen.datingapp.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phannguyen.datingapp.R;

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMessage;
    public LinearLayout linearLayout,mLinearLayout;
    public ChatViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mMessage = itemView.findViewById(R.id.messages);
        linearLayout = itemView.findViewById(R.id.container);
        mLinearLayout = itemView.findViewById(R.id.linearLayout);
    }

    @Override
    public void onClick(View view) {

    }
}
