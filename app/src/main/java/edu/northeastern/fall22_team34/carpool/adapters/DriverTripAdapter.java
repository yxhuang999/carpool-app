package edu.northeastern.fall22_team34.carpool.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.carpool.models.Trip;

public class DriverTripAdapter extends RecyclerView.Adapter<DriverTripAdapter.DriverTripViewHolder> {

    private List<Trip> trips;
    private Context context;

    private Geocoder mGeocoder;

    public DriverTripAdapter(List<Trip> trips, Context context) {
        this.trips = trips;
        this.context = context;

        mGeocoder = new Geocoder(context, Locale.getDefault());
    }

    @NonNull
    @Override
    public DriverTripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_carpool_driver_trip,
                parent, false);
        return new DriverTripAdapter.DriverTripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverTripViewHolder holder, int position) {
        if (trips != null) {
            Trip trip = trips.get(position);
            try {
                List<Address> fromAddress = mGeocoder.getFromLocation(trip.startLat,
                        trip.startLong, 1);
                holder.from.setText("From: " + fromAddress.get(0).getAddressLine(0));

                List<Address> toAddress = mGeocoder.getFromLocation(trip.endLat,
                        trip.endLong, 1);
                holder.dest.setText("To: " + toAddress.get(0).getAddressLine(0));

                holder.startTime.setText("Start Time: " + trip.time);

                if (trip.passenger == null || trip.passenger.size() == 0) {
                    holder.passengerNum.setText("People Joined: 0");
                } else {
                    holder.passengerNum.setText("People Joined: " + trip.passenger.size());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (trips == null || trips.size() == 0) {
            return 0;
        } else {
            return trips.size();
        }
    }

    static class DriverTripViewHolder extends RecyclerView.ViewHolder {

        public TextView from;
        public TextView dest;
        public TextView startTime;
        public TextView passengerNum;

        public DriverTripViewHolder(@NonNull View itemView) {
            super(itemView);
            this.from = itemView.findViewById(R.id.driver_tripFrom);
            this.dest = itemView.findViewById(R.id.driver_tripTo);
            this.startTime = itemView.findViewById(R.id.driver_tripTime);
            this.passengerNum = itemView.findViewById(R.id.driver_tripPassenger);
        }
    }
}
