package com.example.annotate;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;


public class CreateProject extends AppCompatActivity {

    private EditText ettittle,etdescription,etdatasetsize,etuploaddataurl,etitems;
    private Button btncreate,uploadjson;
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    private Integer proid;
    private JSONArray jarray;
    private static final int PERMISSION_REQUEST_STORAGE =1000,READ_RQUEST_CODE=500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
        mLoginFormView= findViewById(R.id.login_form);
        mProgressView= findViewById(R.id.login_progress);
        tvLoad= findViewById(R.id.tvLoad);
        uploadjson = findViewById(R.id.uploadjson);
        ettittle=findViewById(R.id.ettittle);
        etdescription=findViewById(R.id.etdescription);
        etdatasetsize=findViewById(R.id.etdatasetsize);
        etuploaddataurl=findViewById(R.id.etuploaddataurl);
        etitems=findViewById(R.id.etitems);
        btncreate=findViewById(R.id.btncreate);
//        proid=getIntent().getIntExtra("proid",0);
        Log.i("proid",""+proid);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_STORAGE);
        }
        uploadjson.setVisibility(View.GONE);
//        uploadjson.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProgress(true);
////                performFileSearch();
//                showProgress(false);
//            }
//        });
        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ettittle.getText().toString().trim().isEmpty() || etdescription.getText().toString().trim().isEmpty() ||
                    etdatasetsize.getText().toString().trim().isEmpty() || etuploaddataurl.getText().toString().trim().isEmpty()||
                    etitems.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(CreateProject.this,"Please Enter all Fields!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    showProgress(true);
                    String tittle=ettittle.getText().toString().trim();
                    String description=etdescription.getText().toString().trim();
                    int size=Integer.parseInt(etdatasetsize.getText().toString().trim());
                    String url=etuploaddataurl.getText().toString().trim();
                    String items=etitems.getText().toString().trim();
                    Projects project=new Projects();
                    project.setDataToShow(items);
                    project.setDriveLink(url);
                    project.setProjectName(tittle);
                    project.setDescription(description);
                    project.setTotalCount(size);
                    project.setRemainingCount(size);
                    proid=ApplicationClass.projects.size();
                    project.setProjectId(proid+1);
                    project.setUserEmail(ApplicationClass.user.getEmail());
                    Backendless.Persistence.of(Projects.class).save(project, new AsyncCallback<Projects>() {
                        @Override
                        public void handleResponse(Projects response) {
                            Toast.makeText(CreateProject.this,"Project Created Successfully!",Toast.LENGTH_SHORT).show();
                            fetchFullData();
                            startActivityForResult(new Intent(CreateProject.this,MainActivity.class),1);
                            CreateProject.this.finish();
                        }
                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(CreateProject.this,"Error "+fault.getMessage(),Toast.LENGTH_LONG).show();
                            showProgress(false);
                        }
                    });
                }
            }
        });
    }

//    private String readText(String input)
//    {
//        File file=new File(Environment.getExternalStorageDirectory(),input);
//        StringBuilder text=new StringBuilder();
//        try {
//            BufferedReader br=new BufferedReader(new FileReader(file));
//            String line;
//            while((line=br.readLine())!=null)
//            {
//                text.append(line);
//            }
//            br.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return text.toString();
//    }

//    private  void performFileSearch()
//    {
//        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType("applications/*");
////        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent,READ_RQUEST_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == PERMISSION_REQUEST_STORAGE)
//        {
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            {
//                Toast.makeText(CreateProject.this,"Permission granted!",Toast.LENGTH_LONG).show();
//            }else
//            {
//                Toast.makeText(CreateProject.this,"Permission not granted!",Toast.LENGTH_LONG).show();
//                finish();
//            }
//        }
//
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == READ_RQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                Uri uri = data.getData();
//                String path = uri.getPath();
////                path = path.substring(path.indexOf(":") + 1);
//                Log.i("path",path);
//                Toast.makeText(CreateProject.this, "" + path, Toast.LENGTH_SHORT).show();
//                try {
//                    jarray = new JSONArray(readText(path));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Log.i("text", ""+jarray);
//            }
//        }
//    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
                }
            });
        } else {

            mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
        }
    }

    public void fetchFullData( )
    {
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100);
        queryBuilder.addAllProperties();
        queryBuilder.setSortBy("created DESC");
        Backendless.Persistence.of(Projects.class).find(queryBuilder, new AsyncCallback<List<Projects>>() {
            @Override
            public void handleResponse(List<Projects> response) {
                ApplicationClass.projects=response;
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(CreateProject.this, "Error "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });
        queryBuilder = DataQueryBuilder.create();
        String whereClause =  "userEmail = '"+ ApplicationClass.user.getEmail() +"'";
        queryBuilder.setWhereClause(whereClause);
        Backendless.Persistence.of(UserScore.class).find(queryBuilder, new AsyncCallback<List<UserScore>>() {
            @Override
            public void handleResponse(List<UserScore> response) {
                ApplicationClass.userData=response;
                if(response.size()!=0)
                {
                    Log.i("User Data ",""+response.get(0).getEnrolledProjects());
                    HashSet<Long> set=new HashSet<>();
                    String[] ids=ApplicationClass.userData.get(0).getEnrolledProjects().split(",");
                    for(int i=0;i<ids.length;i++)
                    {
                        set.add(Long.parseLong(ids[i]));
                    }
                    ApplicationClass.userenrolledprojectids=set;
                    Log.i("set",""+set);
                }
                else Log.i("User Data ","unable to retrive");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(CreateProject.this,"Unable to fetch Data",Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }

}
