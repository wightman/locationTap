package ca.beermoneyprojects.locationtap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import ca.beermoneyprojects.incidents.*;



/* locationTap - find your location on a linear network

    Rick Wightman, Beer Money Projects, 2022
    Technical Sources:
    https://www.youtube.com/watch?v=QNb_3QKSmMk
    https://www.geeksforgeeks.org/cardview-in-android-with-example/
    https://www.geeksforgeeks.org/cardview-using-recyclerview-in-android-with-example/
    https://stackoverflow.com/questions/29024058/recyclerview-scrolled-up-down-listener
    https://gist.github.com/codinginflow/7b9dd1c12ba015f2955bdd15b09b1cb1
    https://syntaxbytetutorials.com/android-recyclerview-swipe-to-delete-and-undo-with-itemtouchhelper/
    https://guides.codepath.com/android/using-the-app-toolbar
    Somethings to aim for:
        https://www.youtube.com/watch?v=yYVIasp5KXo
        https://learntodroid.com/android-file-io-tutorial-with-internal-and-external-storage/
        https://www.codexpedia.com/java/jackson-parser-example-in-android/
 */
public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener listener;
    private FloatingActionButton incidentBtn;
    private Incident now;
    private String strLocation = "Click again";
    private String incidentProperty ="NB route";
    private RecyclerView incidentRV;
    private ConstraintLayout constraintLayout;

    // Arraylist for storing data
    private ArrayList<IncidentModel> incidentModelArrayList;
    private IncidentAdapter incidentAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        constraintLayout = findViewById(R.id.constraintLayout);
        incidentModelArrayList = new ArrayList<>();
        incidentBtn = findViewById(R.id.incidentBtn);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        buildRecyclerView();
        enableSwipeToDeleteAndUndo();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //ToDo: Remove the (truly) random value
                int randInt = (int)(Math.random() * 101);

                strLocation=String.format("%9.5f", location.getLatitude()) + " " + String.format("%9.5f", location.getLongitude());
                now = new Incident(new Property(incidentProperty), new Position(location.getLatitude(),location.getLongitude(), "4+"+randInt+"0") );
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        /*
         * Check for and load linear network data.
         * 1. Check for a file in the internal app storage named network.
         *      - Not present?
         *          - Initiate file picker for the external app storage
         *          - Read the file into memory, save it to the internal app file named network
         * 2. File is present, read it.
         *
         * Note that this activity should also be available from the app menu
         */
        configure_button();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                // User chose the "About" action
                openAboutWindow();
                return true;
            case R.id.load:
                // User chose the "Load" item, show the file picker
                // Snackbar.make(view, "load", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    private void openAboutWindow(){
        Intent aboutWindow = new Intent(MainActivity.this, AboutWindow.class);
        startActivity(aboutWindow);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 10){
                configure_button();
        }
    }

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},10);
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        incidentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
                // add incident to card
                if(now != null){
                    incidentModelArrayList.add(new IncidentModel(now, R.drawable.ic_baseline_save_24));
                }
                incidentAdapter.notifyDataSetChanged();

                //mLayout.addView();
                /* Snackbar.make(view, strLocation, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */

                Snackbar.make(view, getExternalFilesDir(null).toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void buildRecyclerView(){
        incidentRV = findViewById(R.id.idRVIncident);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        incidentAdapter = new IncidentAdapter(incidentModelArrayList);
        incidentRV.setLayoutManager(linearLayoutManager);
        incidentRV.setAdapter(incidentAdapter);

        incidentAdapter.setOnItemClickListener(new IncidentAdapter.OnItemClickListener() {
            @Override
            public void onSaveClick(int position) {
            }
        });
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final IncidentModel item = incidentAdapter.getData().get(position);

                incidentAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        incidentAdapter.restoreItem(item, position);
                        incidentRV.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(incidentRV);
    }
}