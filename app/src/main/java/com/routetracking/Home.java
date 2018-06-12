package com.routetracking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.routetracking.POJO.Routes;

public class Home extends AppCompatActivity {
    private Button startNewRoute, viewSavedRoute, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
    }

    private void init() {
        bindResources();
        allClicks();
    }


    private void bindResources() {
        startNewRoute = findViewById(R.id.start_new_route);
        viewSavedRoute = findViewById(R.id.view_saved_route);
        settings = findViewById(R.id.settings);
    }

    private void allClicks() {
        startNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onenRouteDialog();

            }
        });

        viewSavedRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent goToSavedRoutes = new Intent(Home.this, SavedRoutes.class);
                startActivity(goToSavedRoutes);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSettings = new Intent(Home.this, Settings.class);
                startActivity(goToSettings);

            }
        });
    }

    private void onenRouteDialog() {
        LayoutInflater li = LayoutInflater.from(Home.this);
        View promptsView = li.inflate(R.layout.rout_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Home.this
        );

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText routeName = promptsView.findViewById(R.id.route_name);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text

                                Routes routes = new Routes(routeName.getText().toString());
                                routes.save();


                                System.out.println("Cust" + routes.getId());
                                 Intent goToRouteTrack = new Intent(Home.this, RoutTrackActivity.class);
                                 goToRouteTrack.putExtra("route_id",routes.getId());
                                 startActivity(goToRouteTrack);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
