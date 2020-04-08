package net.cararea.inspector;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cararea.inspector.obd.ObdParameter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class ParameterAdapter extends RecyclerView.Adapter<ParameterAdapter.ParameterViewHolder> {
    private List<ObdParameter> mDataSet;

    static class ParameterViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView value;
        TextView units;

        ParameterViewHolder(LinearLayout view) {
            super(view);
            name = view.findViewById(R.id.parameter_name);
            value = view.findViewById(R.id.parameter_value);
            units = view.findViewById(R.id.parameter_units);
        }
    }

    ParameterAdapter(List<ObdParameter> parameterList) {
        mDataSet = parameterList;
    }

    @NonNull
    @Override
    public ParameterAdapter.ParameterViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parameter_element, parent, false);
        ParameterViewHolder vh = new ParameterViewHolder(v);

        v.setOnClickListener((view) -> {
            Intent intent = new Intent(view.getContext(), ChartActivity.class);
            intent.putExtra("chartType", mDataSet.get(vh.getAdapterPosition()).getExtra());
            view.getContext().startActivity(intent);
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ParameterViewHolder holder, int position) {
        holder.name.setText(mDataSet.get(holder.getAdapterPosition()).getName());
        holder.value.setText(mDataSet.get(holder.getAdapterPosition()).getValue());
        int units = mDataSet.get(holder.getAdapterPosition()).getUnits();
        if (units != 0) holder.units.setText(units);
        else holder.units.setText("");
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
