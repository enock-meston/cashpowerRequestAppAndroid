package com.nigoote.ernestinecashpower;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFERENCES_NAME = "login_portal";
    TextView link1;
    Button loginButton;
    EditText phoneNumberTXT;
    String HOST = Constant.host;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        link1 = (TextView) findViewById(R.id.link1);
        phoneNumberTXT = (EditText) findViewById(R.id.edtphonenumber);
        loginButton = (Button) findViewById(R.id.loginButtonID);

//        login now method
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneTXT = phoneNumberTXT.getText().toString();
                if (TextUtils.isEmpty(phoneTXT)){
                    Toast.makeText(MainActivity.this, "Banza wandike Telephone", Toast.LENGTH_SHORT).show();
                }else{
                    loginWithPhoneNUmber(phoneTXT);
                }
            }
        });
//        link to the next activity
        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,NewAccountActivity.class));
            }
        });
    }

    private void loginWithPhoneNUmber(String phoneTXT) {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Login ...");
        progressDialog.show();

        Constant constant = new Constant(this,"Message");
        String url2 = HOST+"login.php";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//one
                try {

                    JSONObject resp = new JSONObject(response);
                    String res = resp.getString("statuss");
                    String user_id = resp.optString("id");
                    String fname = resp.optString("Firstname");
                    String lname = resp.optString("Lastname");
                    String phone = resp.optString("phoneNumber");;
                    if(res.contains("Login_Success")){
                        progressDialog.dismiss();

                        if (res.equals("Login_Success")){
                            progressDialog.dismiss();
                            Log.d("LogResp", fname);
                            //shared pref
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("id", user_id);
                            editor.putString("fname", fname);
                            editor.putString("lname", lname);
                            editor.putString("phone", phone);
                            editor.commit();

                            Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    }else if(res.contains("Wrong_phone")){
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Wrong Phone", Toast.LENGTH_LONG).show();

//                        constant.openDialog("Wrong Phone");
                    }else if(res.contains("Wrong_Password")){
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Wrong Password...", Toast.LENGTH_LONG).show();

//                        constant.openDialog("Wrong Password...");
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Connection_Error", Toast.LENGTH_LONG).show();

//                        constant.openDialog("Connection_Error");
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "error 1"+e, Toast.LENGTH_LONG).show();

                    constant.openDialog("error 1"+e.toString());
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Constant constant = new Constant(getApplicationContext(),"Error from Volley");
                Toast.makeText(MainActivity.this, "error 1"+ error.toString(), Toast.LENGTH_SHORT).show();
//                constant.openDialog("error 1"+ error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<String,String>();
                param.put("phone",phoneTXT);
                return param;
            }
        };
//        add request to requestQueue
        requestQueue.add(request);
    }
}