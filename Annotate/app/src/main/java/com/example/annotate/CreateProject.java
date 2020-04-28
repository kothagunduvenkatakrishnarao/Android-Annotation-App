package com.example.annotate;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;


public class CreateProject extends AppCompatActivity {

    private EditText ettittle,etdescription,etdatasetsize,etuploaddataurl,etitems;
    private Button btncreate;
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        mLoginFormView= findViewById(R.id.login_form);
        mProgressView= findViewById(R.id.login_progress);
        tvLoad= findViewById(R.id.tvLoad);

        ettittle=findViewById(R.id.ettittle);
        etdescription=findViewById(R.id.etdescription);
        etdatasetsize=findViewById(R.id.etdatasetsize);
        etuploaddataurl=findViewById(R.id.etuploaddataurl);
        etitems=findViewById(R.id.etitems);
        btncreate=findViewById(R.id.btncreate);
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
                        String tittle=ettittle.getText().toString().trim();
                        String description=etdescription.getText().toString().trim();
                        int size=Integer.parseInt(etdatasetsize.getText().toString().trim());
                        String url=etuploaddataurl.getText().toString().trim();
                        String items=etitems.getText().toString().trim();
                        Log.i("debug",tittle+" "+description+" "+size+" "+items);
                        Projects project=new Projects();
                        project.setDataToShow(items);
                        project.setDriveLink(url);
                        project.setProjectName(tittle);
                        project.setDescription(description);
                        project.setTotalCount(size);
                        project.setRemainingCount(size);
                        project.setProjectId((long) 3);
                        project.setUserEmail(ApplicationClass.user.getEmail());
                        showProgress(true);
                        Backendless.Persistence.save(project, new AsyncCallback<Projects>() {
                            @Override
                            public void handleResponse(Projects response) {
                                Toast.makeText(CreateProject.this,"Project Created Successfully!",Toast.LENGTH_SHORT).show();
                                showProgress(false);
                                startActivity(new Intent(CreateProject.this,MainActivity.class));
                                CreateProject.this.finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
//                                Backendless.Counters.getAndDecrement("project id");
                                Toast.makeText(CreateProject.this,"Error "+fault.getMessage(),Toast.LENGTH_LONG).show();
                                showProgress(false);
                            }
                        });


                }
            }
        });

    }
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

}
