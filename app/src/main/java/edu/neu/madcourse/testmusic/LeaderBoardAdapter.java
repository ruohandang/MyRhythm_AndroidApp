package edu.neu.madcourse.testmusic;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardViewHolder> {
    Context context;
    List<User> users;

    public LeaderBoardAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }


    @NonNull
    @Override
    public LeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new LeaderBoardViewHolder(LayoutInflater.from(context).inflate(R.layout.leaderborditemview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position) {
        holder.rank.setText(String.valueOf(position+1));
        holder.username.setText(users.get(position).getUserName());
        holder.city.setText(users.get(position).getCity());
        double ss = (double) users.get(position).getStep()/1609;
        String strDouble = String.format("%.2f", ss);
        holder.step.setText(strDouble);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
