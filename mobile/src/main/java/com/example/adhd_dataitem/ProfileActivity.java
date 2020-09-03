package com.example.adhd_dataitem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "MyDataMAP.....";


    public ProgressBar progressBar;
    public FirebaseAuth.AuthStateListener authListener;
    public FirebaseAuth auth;
    public TextView mTextView, txt, txt_wel;

    public FirebaseDatabase mFirebaseDatabase;
    public DatabaseReference myRef, myFirebaseInfo, data, w;
    public String userID, high, weight, sex;
    public ListView mListView;
    public Button btnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();

        mTextView = (TextView) findViewById(R.id.text);
        txt = (TextView) findViewById(R.id.txt_show);
        txt_wel = (TextView) findViewById(R.id.txt_welcome);
        btnSend = (Button) findViewById(R.id.next_action);

        final String time = getIntent().getStringExtra("time");
        String timeCount = getIntent().getStringExtra("timeCount");
        final int heartRate = getIntent().getIntExtra("one",1);
        final int step = getIntent().getIntExtra("two",2);

        //Log
        Log.v(TAG, "" + time);
        Log.v(TAG,""+timeCount);
        Log.v(TAG, "" + heartRate);
        Log.v(TAG, "" + step);

        //calculate blood pressure
        int edv = 142;
        int esv = 47;
        int hr = heartRate;

        float mapH = 93;
        float mapL = 70;
        int sv = 95;
        int co = sv * hr;
        //Log.d(TAG,  "value" + co);
        double svrH = (mapH / co);
        double svrL = (mapL / co);


        double bpH = (edv - esv) * hr * svrH;
        double bpL = (edv - esv) * hr * svrL;

        final int max = (int)Math.round(bpH);
        final int min = (int)Math.round(bpL);

        Log.d(TAG,  String.valueOf(max));
        Log.d(TAG,  String.valueOf(min));
        //-------------------------------------------------------



        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();


        myRef = mFirebaseDatabase.getReference("alluser").child(userID).child("id");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String myID = dataSnapshot.getValue(String.class);
                txt.setText(myID);
                final UserInfo userID = new UserInfo();
                userID.setId(myID);

                Log.v(TAG, ".........................." + userID.getId());

                myFirebaseInfo = mFirebaseDatabase.getReference("dataprofile").child(userID.getId());

                myFirebaseInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();

                        userID.setHigh(map.get("high"));
                        userID.setWeight(map.get("weight"));
                        userID.setSex(map.get("sex"));
                        userID.setDOB(map.get("birthday"));
                        Log.d(TAG, "Sex" + userID.getSex() + "High" + userID.getHigh() + "Weight" + userID.getWeight() + "DOB" + userID.getDOB());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        data = mFirebaseDatabase.getReference("datagraph/");

                         //calculate cal
                        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

                        String inputText = userID.getDOB();

                        try {
                            Date  date = inputFormat.parse(inputText);
                            String outputText = sdf.format(date);

                            Date d = sdf.parse(outputText);
                            Calendar c = Calendar.getInstance();
                            c.setTime(d);

                            int yearB = c.get(Calendar.YEAR);
                            int monthB = c.get(Calendar.MONTH) + 1;
                            int dateB = c.get(Calendar.DATE);

                            LocalDate ll = LocalDate.of(yearB, monthB, dateB);
                            LocalDate nowl = LocalDate.now();
                            Period age = Period.between(ll, nowl);

                            userID.setAge(age.getYears());
                            //Log.d(TAG, String.valueOf(age.getYears()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        int age = userID.getAge();
                        int weight = Integer.parseInt(userID.getWeight());

                        double aM = (-55.0969);

                        double bM = 0.6309 * heartRate;

                        double cM = 0.1088 * weight;
                        double dM = 0.2017 * age;

                        double aF = (-20.4022);

                        double bF = 0.4472 * heartRate;
                        double cF = 0.1263 * weight;
                        double dF = 0.0740 * age;


                        String sex = userID.getSex();


                        switch (sex){
                            case "male":
                                double xX = ((aM + bM + cM + dM)/4.184) * 60 * 24;
                                int gg = (int)Math.round(xX);
                                userID.setSum(gg);
                                break;
                            case "female":
                                double y = ((aF + bF + cF + dF)/4.184) * 60 * 24;
                                int ff = (int)Math.round(y);
                                userID.setSum(ff);
                                break;

                        }

                        int sum = userID.getSum();

                        //----------------------------

                        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
                        DateTimeFormatter month = DateTimeFormatter.ofPattern("M");
                        DateTimeFormatter date = DateTimeFormatter.ofPattern("DD");
                        DateTimeFormatter mY = DateTimeFormatter.ofPattern("yyyy-MM");
                        LocalDateTime now = LocalDateTime.now();




                        //Blood
                        Map<String, Object> m = new HashMap<>();
                        m.put("max", max);
                        m.put("min", min);

                        //cal
//                        int[] numbers = new int[]{10, 10, 10, 10};
//                        int sum = 0;
//
//                        for (int i = 0; i < numbers.length; i++) {
//                            sum = sum + numbers[i];
//                        }

                       // Log.d(TAG, "Value" + sum);



                         data.child("bloodpressure/"+ userID.getId()).child(year.format(now)).child(month.format(now)).child(date.format(now)).child(time).setValue(m);
                        data.child("heartrate/" + userID.getId()).child(year.format(now)).child(month.format(now)).child(date.format(now)).child(time).setValue(heartRate);
                        data.child("pedometer/" + userID.getId()).child(mY.format(now)).child(date.format(now)).child(time).setValue(step);
                        data.child("calburn/" + userID.getId()).child(mY.format(now)).child(date.format(now)).child("sum").setValue(sum);

                        Toast.makeText(ProfileActivity.this, userID.getId(), Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final Button logout_action = (Button) findViewById(R.id.logout_action);
        logout_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Logged out!", Toast.LENGTH_LONG).show();
                signOut();
            }
        });
    }

    //sign out method
    public void signOut() {
        auth.signOut();
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
