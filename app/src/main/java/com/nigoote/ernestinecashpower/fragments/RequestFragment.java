package com.nigoote.ernestinecashpower.fragments;

import static android.app.Activity.RESULT_OK;


import static com.nigoote.ernestinecashpower.MainActivity.SHARED_PREFERENCES_NAME;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {
    String URL= Constant.host;
    SharedPreferences sharedPreferences;
    Spinner spinnerModel;
    EditText edtEUCL_Branch;
    ImageView imageView,imageUploadbtn;
    private static int IMG_REQUEST =1;
    private Bitmap bitmap;
    String[] sectorArr;
    ArrayAdapter<String> sectorAdapter;
    ArrayList<String> sectorList = new ArrayList<>();
    Button sendBtn;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
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
        View view= inflater.inflate(R.layout.fragment_request, container, false);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        imageUploadbtn = (ImageView) view.findViewById(R.id.uploadBtn);
        edtEUCL_Branch = (EditText) view.findViewById(R.id.txt_EUCL_Branch);
        spinnerModel = (Spinner) view.findViewById(R.id.spinner_sector);
        sendBtn = (Button) view.findViewById(R.id.sendBtn);
        selectSector();
        imageUploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,IMG_REQUEST);
            }
        });
// this sharedPref comes from  RequestFragment
        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String TXTUserID = sharedPreferences.getString("id", "");
//        click on button
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String branchTxt = edtEUCL_Branch.getText().toString();
                if (TextUtils.isEmpty(branchTxt)){
                    Toast.makeText(getContext(),"Banza Wuzuze Branch!", Toast.LENGTH_LONG).show();
                }else{
                    requestMethod(TXTUserID,branchTxt,spinnerModel.getSelectedItemId());
                }
            }
        });

        return view;
    }

    private void requestMethod(String txtUserID, String branchTxt, long selectedItemId) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("request process...");
        progressDialog.show();
        Constant constant = new Constant(getContext(),"Message");
        String url1 = URL+"request.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest request = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("Successfully_Submitted")){
                    Log.d("enock",response+"working");
                    imageView.setImageResource(0);
                    imageView.setVisibility(View.GONE);
                    progressDialog.dismiss();
//                    constant.openDialog(response);
                    Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                    constant.openDialog(response);

                }else{
                    progressDialog.dismiss();
//                    constant.openDialog(response);
                    Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                    constant.openDialog(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
//                constant.openDialog(error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("branch",branchTxt);
                param.put("CitizenID",txtUserID);
                param.put("sector_id", String.valueOf(spinnerModel.getSelectedItem()));
                param.put("image",imageToString(bitmap));
                return param;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        cdd
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null){

            Uri path = data.getData();
            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), path);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }

    private void selectSector() {
        String URL1 = Constant.host +"selectSec.php";
        StringRequest request= new StringRequest(Request.Method.POST, URL1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray userArray = new JSONArray(response);
                    sectorArr = new String[userArray.length()];

                    for (int sec =0; sec< userArray.length();sec++){
                        JSONObject UserObj = userArray.getJSONObject(sec);
                        String userId = UserObj.optString("id");
                        String sector = UserObj.optString("sector");

                        sectorList.add(sector);
                        sectorArr[sec] = sector;
                    }
                    sectorAdapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item,sectorArr);
                    spinnerModel.setAdapter(sectorAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(getActivity()).add(request);
    }
}