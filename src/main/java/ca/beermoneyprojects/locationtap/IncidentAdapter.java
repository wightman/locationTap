package ca.beermoneyprojects.locationtap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder> {

    private ArrayList<IncidentModel> incidentModelArrayList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onSaveClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class IncidentViewHolder extends RecyclerView.ViewHolder {
        private ImageButton incidentIVButton;
        private TextView incidentNameTV;

        public IncidentViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            incidentIVButton = itemView.findViewById(R.id.idIVIncidentButton);
            incidentNameTV = itemView.findViewById(R.id.idTVIncidentName);

            incidentIVButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onSaveClick(position);

                            // change image to checkmark. Reverts when a new element is created :(
                            // IncidentModel's setIncident_image(R.drawable.ic_baseline_check_circle_24);
                            IncidentModel item = getData().get(position);
                            item.setIncident_image(R.drawable.ic_baseline_check_circle_24);
                            incidentIVButton.setImageResource(R.drawable.ic_baseline_check_circle_24);
                            //incidentIVButton.setImageResource(model.getIncident_image());
                            // disable button
                            incidentIVButton.setEnabled(false);
                            Snackbar.make(view, "Incident saved", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }
            });
        }
    }

    // Constructor
    public IncidentAdapter(ArrayList<IncidentModel> incidentModelArrayList) {
        this.incidentModelArrayList = incidentModelArrayList;
    }

    @Override
    public IncidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        IncidentViewHolder viewHolder = new IncidentViewHolder(view, mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IncidentViewHolder holder, int position) {

        // to set data to textview and imageview of each card layout
        IncidentModel model = incidentModelArrayList.get(position);
        holder.incidentNameTV.setText(model.getIncident_name().getPosition().getM());
        holder.incidentIVButton.setImageResource(model.getIncident_image());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return incidentModelArrayList.size();
    }

    public ArrayList<IncidentModel> getData() {
        return incidentModelArrayList;
    }

    public void removeItem(int position) {
        incidentModelArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(IncidentModel incidentModel, int position) {
        incidentModelArrayList.add(position, incidentModel);
        notifyItemInserted(position);
    }

    public void onSaveClick(int position) {
        IncidentModel incidentModel = incidentModelArrayList.get(position);
        // TODO: The save.
    }
}

