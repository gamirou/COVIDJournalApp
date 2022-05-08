package com.gamiro.covidjournal.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.interfaces.listeners.UserTestListener;
import com.gamiro.covidjournal.models.user.UserTest;

import java.util.ArrayList;

import androidx.annotation.InspectableProperty;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserTestsAdapter extends RecyclerView.Adapter<UserTestsAdapter.UserTestsViewHolder> {

    private static final String TAG = "UserTestsAdapter";

    private Context context;
    private ArrayList<UserTest> tests;
    private UserTestListener mListener;

    public UserTestsAdapter(Context context, ArrayList<UserTest> tests, UserTestListener mListener) {
        this.context = context;
        this.tests = tests;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public UserTestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_test_line_row, parent, false);
        return new UserTestsViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserTestsViewHolder holder, int position) {
        UserTest test = tests.get(position);
        if (!test.getDateResult().isEmpty()) {
            holder.textTitle.setText("Result:");
            holder.textDate.setText(test.getDateResult());
        } else {
            holder.textTitle.setText("Ordered:");
            holder.textDate.setText(test.getDateMade());
        }

        holder.textStatus.setText(test.getResult());
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }

    protected class UserTestsViewHolder extends RecyclerView.ViewHolder {

        public TextView textTitle, textDate, textStatus;
        public UserTestListener mListener;

        public UserTestsViewHolder(@NonNull View itemView, UserTestListener mListener) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.tv_user_test_inline_title);
            textDate = itemView.findViewById(R.id.tv_user_test_inline_data);
            textStatus = itemView.findViewById(R.id.tv_user_test_inline_status);

            this.mListener = mListener;

            itemView.setOnClickListener(view -> {
                mListener.onUserTestClick(getAdapterPosition());
            });
        }
    }
}
