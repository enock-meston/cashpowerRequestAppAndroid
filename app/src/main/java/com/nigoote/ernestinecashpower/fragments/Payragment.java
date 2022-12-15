package com.nigoote.ernestinecashpower.fragments;

import static com.nigoote.ernestinecashpower.MainActivity.SHARED_PREFERENCES_NAME;

import static java.lang.System.out;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.nigoote.ernestinecashpower.Constant;
import com.nigoote.ernestinecashpower.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Payragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Payragment extends Fragment {

    SharedPreferences sharedPreferences;
    TextView MessageTXT;
    Button payButton;
    EditText payNumber;
    String URL = Constant.host;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Payragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Payragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Payragment newInstance(String param1, String param2) {
        Payragment fragment = new Payragment();
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
        View view= inflater.inflate(R.layout.fragment_payragment, container, false);
        MessageTXT = (TextView) view.findViewById(R.id.txtMessage);
        payButton = (Button) view.findViewById(R.id.paiButton);
        payNumber = (EditText) view.findViewById(R.id.payNumber);

        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String TXTID = sharedPreferences.getString("id", "");
        ViewMessage(TXTID);
//        payButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String phoneNumber = payNumber.getText().toString();
//                if (TextUtils.isEmpty(phoneNumber)) {
//                    Constant constant = new Constant(getActivity(), "Message");
//                    Toast.makeText(getContext(), "Please Enter Phone Number", Toast.LENGTH_LONG).show();
//
////                    constant.openDialog("Please Enter Amount");
//                } else {
////                    paymentMethod(amount, phoneNumber);
//                    try {
//                        sendPop(phoneNumber);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

        return  view;
    }

    private void ViewMessage(String txtid) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Registration process...");
        progressDialog.show();
        Constant constant = new Constant(getContext(),"message");
        String url1 = URL+"status.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Nta Burenganzira Bwo Kwishyura")){
                    Log.d("enock",response+"working");
                    progressDialog.dismiss();
//                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                    MessageTXT.setText(response);
//                    constant.openDialog(response);

                }else if(response.equals("Mwamaze guhabwa Cashpower device!")){
                    MessageTXT.setText(response);
                }
                else{
                    payButton.setVisibility(View.VISIBLE);
                    payNumber.setVisibility(View.VISIBLE);
                    payButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String phone = payNumber.getText().toString();
                            if (TextUtils.isEmpty(phone)) {
                                Constant constant = new Constant(getActivity(), "Message");
                                Toast.makeText(getContext(), "Please Enter Phone Number", Toast.LENGTH_LONG).show();

//                    constant.openDialog("Please Enter Amount");
                            } else {
//                    paymentMethod(amount, phoneNumber);
                                try {
                                    sendPop(phone);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                    constant.openDialog(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                constant.openDialog(error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("id",txtid);
                return param;
            }
        };
        requestQueue.add(request);
    }


    private void saveData(String phone, String amount, String Transactionref, String status, String TXTID) {
        String URL1 = Constant.host + "kwishyura.php";
        Constant constant = new Constant(getActivity(), "Message");
        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, URL1, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("HTTPErr", response);
                Toast.makeText(getContext(), "Transaction request sent successful", Toast.LENGTH_SHORT).show();
                constant.openDialog("Transaction request sent successful");

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", error.toString());
                Toast.makeText(getContext(), "Error from Volley"+error, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("phone", phone);
                param.put("amount", amount);
                param.put("Transactionref", Transactionref);
                param.put("status", status);
                param.put("userid",TXTID);
                return param;
            }
        };
        Volley.newRequestQueue(getContext()).add(request);
    }

    void sendPop(String number) throws IOException {
        String amount ="10";
        String transactionId = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d = new Date();
        String date = inputFormat.format(d);
        out.println(date);
        final int random = new Random().nextInt(99999) + 1;
        out.println("==================amafaranga==" + amount + "=======================" + random + " number is " + number);

//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        String appTransactionId = "ffe037792vcdmx51e8l5h4603qf0064a09" + random;
//        RequestBody body = RequestBody.create(mediaType, "{\n  \"telephoneNumber\" : \"25" + number + "\",\n  \"amount\" : " + Integer.parseInt(amount) + ",\n  \"organizationId\" : \"6af87ea4-ced1-44f8-aea1-75098962e0e4\",\n  \"description\" : \"Funeral Management System\",\n  \"callbackUrl\" : \"https://menyeshaapp.000webhostapp.com/android/kwishyura_callback.php\",\n  \"transactionId\" : \"" + appTransactionId + "\"\n}\n");
//        Log.d("PhoneVal","25"+number);
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url("https://opay-api.oltranz.com/opay/paymentrequest")
//                .post(body)
//                .addHeader("Content-Type", "application/json")
//                .build();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("ref","pay")  //ni iyihe
                .addFormDataPart("tel",number)
                .addFormDataPart("tx_ref",appTransactionId)
                .addFormDataPart("amount",amount)
                .addFormDataPart("link","https://menyeshaapp.000webhostapp.com/android/kwishyura_callback.php")
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://payment.hdev.rw/api_pay/api/HDEV-48d87cf2-c648-49c1-9c7c-a1a12dbc30eb-ID/HDEV-79d8e552-5bed-4f5a-9551-cd051e32e406-KEY")
                .method("POST", body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String res = response.body().string();
                Log.d("TAG", res);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String status = jsonObject.getString("status");


                    Log.d("tId", "transactionId");
                    if(status == "error"){
                        status = "failed";
                    }else{
                        status = "pending";
                    }
                    sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                    String TXTID = sharedPreferences.getString("id", "");
                    saveData(number, amount, appTransactionId, status,TXTID);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}