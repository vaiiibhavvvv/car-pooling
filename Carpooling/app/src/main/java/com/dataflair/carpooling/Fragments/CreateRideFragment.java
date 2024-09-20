package com.dataflair.carpooling.Fragments;
/**
 * In this Fragment We will take Ride details as input
 * we will add Ride Details details to firebase
 */


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dataflair.carpooling.MainActivity;
import com.dataflair.carpooling.Model.Model;
import com.dataflair.carpooling.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class CreateRideFragment extends Fragment {


    EditText riderNameEditTxt, sourceAddressEditTxt, destinationAddressEditTxt, totalPassengersEditTxt, rideDateEditTxt, rideTimeEditTxt, ridePriceEditTxt, phoneNumberEditTxt;
    Button createRide;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CreateRideFragment() {
        // Required empty public constructor
    }


    public static CreateRideFragment newInstance(String param1, String param2) {
        CreateRideFragment fragment = new CreateRideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_ride, container, false);

        //Assigning the address of android component to perform appropriate action
        riderNameEditTxt = (EditText) view.findViewById(R.id.RiderNameEdit);
        sourceAddressEditTxt = (EditText) view.findViewById(R.id.SourceAddressEdit);
        destinationAddressEditTxt = (EditText) view.findViewById(R.id.DestinationAddressEdit);
        totalPassengersEditTxt = (EditText) view.findViewById(R.id.TotalPassengersEdit);
        rideDateEditTxt = (EditText) view.findViewById(R.id.RideDateEdit);
        rideTimeEditTxt = (EditText) view.findViewById(R.id.RideTimeEdit);
        ridePriceEditTxt = (EditText) view.findViewById(R.id.RidePriceEdit);
        phoneNumberEditTxt = (EditText) view.findViewById(R.id.PhoneNumberEdit);

        //CreateRide Butotn to update the ride details in Fireabse database
        createRide = (Button) view.findViewById(R.id.CreateRideBtn);


        //implementing the onclickListener to upload the Ride Details to fireabse on buttonClick
        createRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Gets the Test form the editTexts and assigning to Strings
                String riderName = riderNameEditTxt.getText().toString().trim().toLowerCase();
                String sourceAddress = sourceAddressEditTxt.getText().toString().trim().toLowerCase();
                String destinationAddress = destinationAddressEditTxt.getText().toString().trim().toLowerCase();
                String totalPassengers = totalPassengersEditTxt.getText().toString();
                String rideDate = rideDateEditTxt.getText().toString().trim().toLowerCase();
                String rideTime = rideTimeEditTxt.getText().toString().trim().toLowerCase();
                String ridePrice = ridePriceEditTxt.getText().toString().trim().toLowerCase();
                String phoneNumber = phoneNumberEditTxt.getText().toString().trim();

                //Shows the below toast message if eny EditText field is empty
                if (riderName.isEmpty() || sourceAddress.isEmpty() || destinationAddress.isEmpty() || totalPassengers.isEmpty() || rideDate.isEmpty() || rideTime.isEmpty() || ridePrice.isEmpty() || phoneNumber.isEmpty()) {
                    Toast.makeText(view.getContext(), "Please, Fill all the details", Toast.LENGTH_SHORT).show();
                } else {
                    //Calls the below method to update the data in firebase
                    AddDataToFirebase(riderName, sourceAddress, destinationAddress, totalPassengers, rideDate, rideTime, ridePrice, phoneNumber);
                }


            }
        });

        return view;
    }

    private void AddDataToFirebase(String riderName, String sourceAddress, String destinationAddress, String totalPassengers, String rideDate, String rideTime, String ridePrice, String phoneNumber) {

        //Accessing the google user id to create a unique ride  for unique users
        String userId = GoogleSignIn.getLastSignedInAccount(getActivity()).getId();

        //Fireabase dAtabase path to store the Ride dEtails
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Rides");

        //Creating the HashMap to store the Ride Details
        HashMap<String, String> user_details = new HashMap<>();


        //Assigining the Source Address and Destination Address to make Ride search Easy
        String sourceAddrAndDestinationAddr = sourceAddress + destinationAddress;

        //Storeing the ride Details in the HashMap
        user_details.put("riderName", riderName);
        user_details.put("sourceAddress", sourceAddress);
        user_details.put("destinationAddress", destinationAddress);
        user_details.put("sourceAddrAndDestinationAddr", sourceAddrAndDestinationAddr);
        user_details.put("rideDate", rideDate);
        user_details.put("rideTime", rideTime);
        user_details.put("ridePrice", ridePrice);
        user_details.put("totalPassengers", totalPassengers);

        user_details.put("userId", userId);
        user_details.put("phoneNumber", phoneNumber);

        //Setting the Ride details in the firebase Database
        myRef.child(userId).setValue(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    //Shows the bellow message when the Ride Details Successfully update to Database
                    Toast.makeText(getContext(), "Ride Created Successfully", Toast.LENGTH_SHORT).show();

                    //Setting the editText Fields to empty after updating the data in fireabse
                    riderNameEditTxt.setText("");
                    sourceAddressEditTxt.setText("");
                    destinationAddressEditTxt.setText("");
                    totalPassengersEditTxt.setText("");
                    rideDateEditTxt.setText("");
                    rideTimeEditTxt.setText("");
                    ridePriceEditTxt.setText("");
                    phoneNumberEditTxt.setText("");

                }
            }
        });


    }
}



