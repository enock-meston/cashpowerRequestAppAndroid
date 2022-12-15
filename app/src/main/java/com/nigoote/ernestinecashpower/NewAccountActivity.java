package com.nigoote.ernestinecashpower;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class NewAccountActivity extends AppCompatActivity {
    EditText edtFirstName,edtLastName,edtPhoneNUmber,edtcell,edtSector;
    Button BtnRegister;
    String URL = Constant.host;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        edtFirstName = (EditText) findViewById(R.id.edtfname);
        edtLastName = (EditText) findViewById(R.id.edtlname);
        edtPhoneNUmber = (EditText) findViewById(R.id.editTextPhone);
        edtcell = (EditText) findViewById(R.id.txt_cell);
        edtSector = (EditText) findViewById(R.id.txt_Sector);
        BtnRegister = (Button) findViewById(R.id.savebutton);

        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtfn= edtFirstName.getText().toString();
                String txtln= edtLastName.getText().toString();
                String txtph= edtPhoneNUmber.getText().toString();
                String txtcell= edtcell.getText().toString();
                String txtSec= edtSector.getText().toString();

                if (TextUtils.isEmpty(txtfn) || TextUtils.isEmpty(txtln) || TextUtils.isEmpty(txtph)
                        || TextUtils.isEmpty(txtcell) || TextUtils.isEmpty(txtSec)){
                    Toast.makeText(NewAccountActivity.this, "All Fields are Required", Toast.LENGTH_SHORT).show();
                }else{
                    registerCitizen(txtfn,txtln,txtph,txtcell,txtSec);
                }
            }
        });
    }

    private void registerCitizen(String txtfn, String txtln, String txtph, String txtcell, String txtSec) {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Registration process...");
        progressDialog.show();
        Constant constant = new Constant(getApplicationContext(),"message");
        String url1 = URL+"register.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Successfully_Registered")){
                    Log.d("enock",response+"working");
                    progressDialog.dismiss();
                    Toast.makeText(NewAccountActivity.this, response, Toast.LENGTH_SHORT).show();

//                    constant.openDialog(response);
                    clearEditText();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(NewAccountActivity.this, response, Toast.LENGTH_SHORT).show();

//                    constant.openDialog(response);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(NewAccountActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

//                constant.openDialog(error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("firstname",txtfn);
                param.put("lastname",txtln);
                param.put("phonenumber",txtph);
                param.put("cell",txtcell);
                param.put("Sector",txtSec);
                return param;
            }
        };
        requestQueue.add(request);
    }

    public  void clearEditText(){
        edtFirstName.setText("");
        edtLastName.setText("");
        edtcell.setText("");
        edtSector.setText("");
        edtPhoneNUmber.setText("");
    }
}