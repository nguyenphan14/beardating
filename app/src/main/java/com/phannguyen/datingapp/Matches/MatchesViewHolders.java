package com.phannguyen.datingapp.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phannguyen.datingapp.R;
import com.phannguyen.datingapp.ViewProfileActivity;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchId,mMatchName;
    public ImageView imageView;
    public String mMatchUId;
    public MatchesViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
//        mMatchId = (TextView) itemView.findViewById(R.id.MatchId);
        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);
        imageView = (ImageView) itemView.findViewById(R.id.MatchImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("matchId",mMatchUId);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
    }
}
