package com.example.polyline

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private val TAG = "so47492459"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val barcelona = LatLng(28.516241878423713, 77.31394774935228)
        mMap!!.addMarker(MarkerOptions().position(barcelona).title("Marker in Delhi").icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker)))

        val madrid = LatLng(28.509529474951165, 77.30171687613256)
        mMap!!.addMarker(MarkerOptions().position(madrid).title("Marker in Delhi").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))
        val zaragoza = LatLng(28.509529474951165, 77.30171687613256)

        //Define list to get all latlng for the route
        val path: MutableList<LatLng> = ArrayList()


        //Execute Directions API request
        val context: GeoApiContext = GeoApiContext.Builder()
            .apiKey("YOUR_KEY")
            .build()
        val req: DirectionsApiRequest =
            DirectionsApi.getDirections(context, "28.516241878423713, 77.31394774935228", "28.509529474951165, 77.30171687613256")
        try {
            val resDir: DirectionsResult = req.await()

            //Loop through legs and steps to get encoded polylines of each step
            if (resDir.routes != null && resDir.routes.size > 0) {
                val route: DirectionsRoute = resDir.routes.get(0)
                if (route.legs != null) {
                    for (i in 0 until route.legs.size) {
                        val leg: DirectionsLeg = route.legs.get(i)
                        if (leg.steps != null) {
                            for (j in 0 until leg.steps.size) {
                                val step: DirectionsStep = leg.steps.get(j)
                                if (step.steps != null && step.steps.size > 0) {
                                    for (k in 0 until step.steps.size) {
                                        val step1: DirectionsStep = step.steps.get(k)
                                        val points1: EncodedPolyline = step1.polyline
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            val coords1: MutableList<com.google.maps.model.LatLng>? =
                                                points1.decodePath()
                                            if (coords1 != null) {
                                                for (coord1 in coords1) {
                                                    path.add(LatLng(coord1.lat, coord1.lng))
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    val points: EncodedPolyline = step.polyline
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        val coords: MutableList<com.google.maps.model.LatLng>? =
                                            points.decodePath()
                                        if (coords != null) {
                                            for (coord in coords) {
                                                path.add(LatLng(coord.lat, coord.lng))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.localizedMessage)
        }
         val DASH: PatternItem = Dash(20.0f)

        //Draw the polyline
        if (path.size > 0) {
            val opts =
                PolylineOptions().startCap(RoundCap()).endCap(RoundCap()).jointType(JointType.ROUND).addAll(path).color(Color.BLACK).width(5f)
            mMap!!.addPolyline(opts)
        }
        mMap!!.uiSettings.isZoomGesturesEnabled = true
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 15f))
    }
}