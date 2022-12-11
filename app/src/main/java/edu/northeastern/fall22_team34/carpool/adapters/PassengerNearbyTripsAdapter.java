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
import edu.northeastern.fall22_team34.carpool.OnJoinClicklistener;
import edu.northeastern.fall22_team34.carpool.models.Trip;

public class PassengerNearbyTripsAdapter extends
        RecyclerView.Adapter<PassengerNearbyTripsAdapter.PsgNearbyTripsViewHolder> {

    private List<Trip> trips;
    private Context context;

    private Geocoder mGeocoder;

    private OnJoinClicklistener onJoinClicklistener;

    public PassengerNearbyTripsAdapter(List<Trip> trips, Context context,
                                       OnJoinClicklistener onJoinClicklistener) {
        this.trips = trips;
        this.context = context;
        this.onJoinClicklistener = onJoinClicklistener;

        mGeocoder = new Geocoder(context, Locale.getDefault());
    }

    @NonNull
    @Override
    public PsgNearbyTripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_container_carpool_psg_nearby_trips, parent, false);
        return new PassengerNearbyTripsAdapter.PsgNearbyTripsViewHolder(view, onJoinClicklistener);
    }

    @Override
    public void onBindViewHolder(@NonNull PsgNearbyTripsViewHolder holder, int position) {
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

                String capacity = "";
                if (trip.driver != null && trip.driver.vehicleProfile != null) {
                    capacity = String.valueOf(trip.driver.vehicleProfile.seat);
                }
                if (trip.passenger == null || trip.passenger.size() == 0) {
                    holder.passengerNum.setText("People Joined: 0/" + capacity);
                } else {
                    holder.passengerNum.setText("People Joined: " + trip.passenger.size() + "/"
                            + capacity);
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

    static class PsgNearbyTripsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView from;
        public TextView dest;
        public TextView startTime;
        public TextView passengerNum;
        public Button joinBtn;

        public OnJoinClicklistener onJoinClicklistener;

        public PsgNearbyTripsViewHolder(@NonNull View itemView, OnJoinClicklistener onJoinClicklistener) {
            super(itemView);
            this.from = itemView.findViewById(R.id.psg_tripFrom);
            this.dest = itemView.findViewById(R.id.psg_tripTo);
            this.startTime = itemView.findViewById(R.id.psg_tripTime);
            this.passengerNum = itemView.findViewById(R.id.psg_tripPassenger);
            this.joinBtn = itemView.findViewById(R.id.psg_trip_join_btn);
            this.onJoinClicklistener = onJoinClicklistener;

            joinBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onJoinClicklistener.onJoinClick(getAbsoluteAdapterPosition());
        }
    }
}
