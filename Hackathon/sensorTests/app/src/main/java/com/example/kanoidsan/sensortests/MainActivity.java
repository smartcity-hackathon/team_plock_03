package com.example.kanoidsan.sensortests;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    int iloscMiejsc;

    TextView textView;
    Button button;

    SensorManager sensorManager;
    Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("miejsce");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(MainActivity.this, dataSnapshot.child("msg").getValue().toString(), Toast.LENGTH_SHORT).show();
                iloscMiejsc = Integer.parseInt(dataSnapshot.child("msg").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        textView = (TextView) findViewById(R.id.sensorText);
        button = (Button) findViewById(R.id.button);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorManager.registerListener(new MySensorListener(), sensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    private class MySensorListener implements SensorEventListener {
        boolean first = true;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if(!first)
                {
                    double czy_zajete = sensorEvent.values[0];
                    textView.setText("" + czy_zajete);
                    databaseReference.child("msg").setValue((czy_zajete == 0.0 ? --iloscMiejsc : ++iloscMiejsc));
                }else if(first){
                    first = false;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}
