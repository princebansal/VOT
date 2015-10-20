package com.gdgvitvellore.volsbbonetouch.Database;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdgvitvellore.volsbbonetouch.MainActivity;
import com.gdgvitvellore.volsbbonetouch.R;

import java.util.List;

/**
 * Created by shalini on 03-06-2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {

    private LayoutInflater inflater;
    private Context c;
    private MainActivity activity;
    private List<Account> list;
    public RecyclerAdapter(Context context,List<Account> data,MainActivity ac){
        c=context;
        activity=ac;
        list=data;
        inflater=LayoutInflater.from(c);
    }
    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.accounts_recycler, parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);
        return recyclerHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        holder.accName.setText(list.get(position)._username);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView accName;
        ImageView accClear;
        public RecyclerHolder(View itemView) {
            super(itemView);
            accName=(TextView)itemView.findViewById(R.id.acc_name);
            accClear=(ImageView)itemView.findViewById(R.id.account_delete);
            accClear.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.applyItem(list.get(getPosition()));

                }
            });
        }

        @Override
        public void onClick(View v) {
            activity.deleteAccount(list.get(getPosition()));
            list.remove(getPosition());
            notifyDataSetChanged();
        }
    }


}
