package com.exe.sensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SensorActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private RecyclerView recyclerView;
    private SensorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        if (adapter == null) {
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                int position = rv.getChildAdapterPosition(child);

                if (child != null && position == 1) {
                    // Pobierz kliknięty czujnik
                    Sensor clickedSensor = sensorList.get(position);

                    // Rozpocznij nową aktywność (LocationActivity) z informacją o klikniętym czujniku
                    Intent intent = new Intent(SensorActivity.this, LocationActivity.class);
                    intent.putExtra("SENSOR_TYPE", clickedSensor.getType());
                    startActivity(intent);
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }

        });
    }
        private class SensorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ImageView sensorIcon;
            private TextView sensorName;
            private Sensor sensor;

            public SensorHolder(LayoutInflater inflater, @NonNull View parent) {
                super(inflater.inflate(R.layout.sensor_list_item, (ViewGroup) parent, false));
                itemView.setOnClickListener(this);

                sensorIcon = itemView.findViewById(R.id.sensorIcon);
                sensorName = itemView.findViewById(R.id.sensorName);
            }

            public void onClick(View view) {
                if (sensor != null) {
                    onSensorClicked(sensor);
                }
            }

            public void bind(Sensor sensor) {
                this.sensor = sensor;
            }

            private void onSensorClicked(Sensor sensor) {

                Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                // Przekazanie informacji o klikniętym sensorze do aktywności detalów
                intent.putExtra("SENSOR_TYPE", sensor.getType());
                startActivity(intent);
            }
        }

        private class SensorAdapter extends RecyclerView.Adapter<SensorHolder> {
            private List<Sensor> sensorList;

            public SensorAdapter(List<Sensor> sensorList) {
                this.sensorList = sensorList;
            }

            @NonNull
            @Override
            public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                return new SensorHolder(layoutInflater, parent);
            }

            @Override
            public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
                Sensor sensor = sensorList.get(position);

                String sensorName = sensor.getName();
                if (sensorName == null || sensorName.isEmpty()) {
                    sensorName = "Unknown Sensor"; //ustawiam domyślna nazwe, gdy brakuje informacji
                }
                holder.sensorName.setText(sensorName);
                holder.bind(sensor);

                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.highlightedColor));
                } else if (sensor.getType() == Sensor.TYPE_LIGHT) {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.highlightedColor));
                } else {
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT); // Domyślne tło dla pozostałych sensorów
                }

            }

            @Override
            public int getItemCount() {
                return sensorList.size();
            }
        }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;
        }
        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            int itemId = item.getItemId();

            if (itemId == R.id.action_show_sensor_count) {
                showSensorCount();
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }

        private void showSensorCount () {
            int totalSensors = sensorList.size();
            String message = "Total Sensors: " + totalSensors;

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }