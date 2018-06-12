package com.routetracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.routetracking.Adapter.RouteAdapter;
import com.routetracking.POJO.Routes;

import java.util.ArrayList;
import java.util.List;

public class SavedRoutes extends AppCompatActivity {
    private RecyclerView savedRouteRecycler;
    private static List<Routes> routeData;
    private RouteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_routes);
        routeData = new ArrayList<>();


        init();
    }

    private void init() {
        bindResources();



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        final int spacing = 4;
        savedRouteRecycler.setLayoutManager(linearLayoutManager);
        savedRouteRecycler.setHasFixedSize(true);


        setupRecyclerView();
    }




    private void bindResources() {
        savedRouteRecycler = findViewById(R.id.saved_route_recycler);
    }


    private void setupRecyclerView() {

        ///  System.out.println("catagoryData.size() = " + catagoryData.size());
        routeData = Routes.listAll(Routes.class);

        adapter = new RouteAdapter(routeData, this);

        RouteAdapter.OnItemClickListener onItemClickListener = new RouteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                System.out.println("id"+routeData.get(position).getId());

                Intent goToSaveRouteMap = new Intent(SavedRoutes.this, SavedRouteMap.class);
                goToSaveRouteMap.putExtra("route_id",routeData.get(position).getId());
                startActivity(goToSaveRouteMap);

            }
        };



      adapter.setOnItemClickListener(onItemClickListener);

        savedRouteRecycler.setAdapter(adapter);


    }
}
