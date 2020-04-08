package net.cararea.inspector;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cararea.inspector.obd.ObdError;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class ErrorAdapter extends RecyclerView.Adapter<ErrorAdapter.ParameterViewHolder> {
    private List<ObdError> mDataSet;

    static class ParameterViewHolder extends RecyclerView.ViewHolder {
        TextView code;
        TextView text;

        ParameterViewHolder(LinearLayout view) {
            super(view);
            code = view.findViewById(R.id.error_code);
            text = view.findViewById(R.id.error_text);
        }
    }

    ErrorAdapter(List<ObdError> errorList) {
        mDataSet = errorList;
    }

    @NonNull
    @Override
    public ErrorAdapter.ParameterViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.error_element, parent, false);
        ParameterViewHolder vh = new ParameterViewHolder(v);

        v.setOnClickListener((view) -> {
            Intent intent = new Intent(view.getContext(), ErrorInfoActivity.class);
            intent.putExtra("errorCode", mDataSet.get(vh.getAdapterPosition()).getCode());
            intent.putExtra("errorText", mDataSet.get(vh.getAdapterPosition()).getText());
            view.getContext().startActivity(intent);
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ParameterViewHolder holder, int position) {
        holder.code.setText(mDataSet.get(holder.getAdapterPosition()).getCode());
        holder.text.setText(mDataSet.get(holder.getAdapterPosition()).getText());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
