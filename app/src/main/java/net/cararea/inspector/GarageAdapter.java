package net.cararea.inspector;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cararea.inspector.db.Car;
import net.cararea.inspector.db.Setting;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import static net.cararea.inspector.GarageActivity.getSelectedCarId;
import static net.cararea.inspector.GarageActivity.setSelectedCarId;

//import android.content.Intent;

public class GarageAdapter extends RecyclerView.Adapter<GarageAdapter.CarViewHolder> {
    private final LayoutInflater mInflater;
    private int selectedCarPos;
    private List<Car> cars;

    class CarViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout carLayout;
        private TextView carTextView;
        private ImageView carImageView;

        private CarViewHolder(View itemView) {
            super(itemView);
            carLayout = itemView.findViewById(R.id.car_layout);
            carTextView = itemView.findViewById(R.id.car_text);
            carImageView = itemView.findViewById(R.id.car_image);
        }
    }

    GarageAdapter(Context context, List<Car> carList) {
        mInflater = LayoutInflater.from(context);
        cars = carList;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.car_element, parent, false);
        return new CarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        if (cars != null) {
            Car current = cars.get(position);
            holder.carTextView.setText(current.name);

            if (current.uid == getSelectedCarId()) {
                holder.carTextView.setTextColor(Color.BLACK);
                selectedCarPos = holder.getAdapterPosition();
            } else {
                holder.carTextView.setTextColor(Color.GRAY);
            }

            holder.carLayout.setOnClickListener((v) -> {
                GarageActivity activity = (GarageActivity) v.getContext();

                setSelectedCarId(current.uid);
                notifyItemChanged(selectedCarPos);
                selectedCarPos = holder.getAdapterPosition();
                notifyItemChanged(selectedCarPos);

                SettingRepository repository = new SettingRepository(activity);
                LiveData<Setting> setting = repository.get(MainActivity.SELECTED_CAR_SETTING);
                setting.observe(activity, (selectedCar) -> {
                    setting.removeObservers(activity);
                    selectedCar.value = current.uid;
                    repository.update(selectedCar);
                });
                /*
                Intent intent = new Intent(context, CarActivity.class).putExtra("carId",
                        current.uid).putExtra("carName", current.name);
                context.startActivity(intent);
                */
            });
        }
    }

    @Override
    public int getItemCount() {
        if (cars != null)
            return cars.size();
        else return 0;
    }
}
