package edu.neu.madcourse.testmusic;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderBoardViewHolder extends RecyclerView.ViewHolder {
    TextView rank, username, city, step;


    public LeaderBoardViewHolder(@NonNull View itemView) {
        super(itemView);
        rank = itemView.findViewById(R.id.rank);
        username = itemView.findViewById(R.id.user);
        city = itemView.findViewById(R.id.city);
        step = itemView.findViewById(R.id.step);

    }
}
