package com.example.finallogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

// import android.support.v7.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener {

    EditText id, nameS;
    Button button, date1, date2;

    CalendarView calendar;
    TextView text;
    String date, leave, come;

    GoogleSignInClient mGoogleSignInClient;
    String name, email;
    Button signOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            name = acct.getDisplayName();
            email = acct.getEmail();
            String personId = acct.getId();


        }

        calendar = (CalendarView) findViewById(R.id.calendarView);
        text = (TextView) findViewById(R.id.text);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
             public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                 date = year + "/" + (month + 1) + "/" + dayOfMonth;
             }
         });

        id = findViewById(R.id.id);
        nameS = findViewById(R.id.nameS);

        date1 = findViewById(R.id.date1);
        date1.setOnClickListener(this);

        date2 = findViewById(R.id.date2);
        date2.setOnClickListener(this);

        button = findViewById(R.id.button);
        button.setOnClickListener(this);

        signOut=findViewById(R.id.button_sign_out);
        signOut.setOnClickListener(this);

        leave = "";
        come = "";

    }

    //This is the part where data is transferred from Your Android phone to Sheet by using HTTP Rest API calls

    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
        final String myID = id.getText().toString().trim();
        final String mynameS = nameS.getText().toString().trim();
        final String myLeave = leave;
        final String myCome = come;
        final String myName = name;
        final String myEmail = email;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxVoToxz4cjCFA2fdVZ1uLD08pnDUH40I3ANTIvelZfzKYbZUSz/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        loading.dismiss();

                        Toast.makeText(CalendarActivity.this,response,Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                System.out.println(myID);
                System.out.println(mynameS);
                System.out.println(myLeave);
                System.out.println(myCome);
                System.out.println(myName);
                System.out.println(myEmail);

                //here we pass params
                params.put("action","addStudent");
                params.put("studentID",myID);
                params.put("nameS",mynameS);
                params.put("leave",myLeave);
                params.put("come",myCome);
                params.put("officialName", myName);
                params.put("email", myEmail);

                return params;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);

    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(CalendarActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }



    @Override
    public void onClick(View v) {
        if (v == date1) {
            System.out.println("date = " + date);
            leave = date;
            Toast.makeText(CalendarActivity.this, "외박일 입력 완료", LENGTH_SHORT).show();
        } else if (v == date2) {
            System.out.println("date = " + date);
            come = date;
            Toast.makeText(CalendarActivity.this, "귀가일 입력 완료", LENGTH_SHORT).show();
        } else if(v==button){
            addItemToSheet();
            //Define what  to do when button is clicked
        } else if (v==signOut) {
            System.out.println("sign out");
            signOut();
        }
    }
}

