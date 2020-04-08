package net.cararea.inspector;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cararea.inspector.utils.LocationManager;
import net.cararea.inspector.utils.Util;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private List<Place> mDataSet;
    private Context context;
    private Location userLocation;

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        LinearLayout wrapper;
        TextView name;
        TextView address;
        TextView distance;

        PlaceViewHolder(LinearLayout view) {
            super(view);
            wrapper = view.findViewById(R.id.place_wrapper);
            name = view.findViewById(R.id.place_name);
            address = view.findViewById(R.id.place_address);
            distance = view.findViewById(R.id.place_distance);
        }
    }

    PlaceAdapter(List<Place> placeList, Context context) {
        mDataSet = placeList;
        this.context = context;
        userLocation = LocationManager.getLastLocation();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_element, parent, false);
        return new PlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        String name = mDataSet.get(holder.getAdapterPosition()).getName();
        String address =  mDataSet.get(holder.getAdapterPosition()).getAddress();

        if (name != null && address != null) {
            holder.name.setText(name);
            holder.address.setText(address);
            holder.address.setVisibility(View.VISIBLE);
        } else if (address != null) {
            holder.name.setText(address);
            holder.address.setVisibility(View.GONE);
        } else {
            holder.name.setText(R.string.unknown);
            holder.address.setVisibility(View.GONE);
        }

        double lat = mDataSet.get(holder.getAdapterPosition()).getLatitude();
        double lng = mDataSet.get(holder.getAdapterPosition()).getLongitude();

        holder.distance.setText(String.format(Locale.ENGLISH, "%.0f",
                Util.getDistanceBetweenTwoPoints(userLocation.getLatitude(),
                        userLocation.getLongitude(), lat, lng)));

        holder.wrapper.setOnClickListener((View v) -> {
            Intent intent = new Intent(context, InspectionActivity.class);
            double[] latLng = new double[] {lat, lng};
            intent.putExtra("latLng", latLng);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
