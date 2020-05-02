package com.example.annotate;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.DataPermission;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.HashSet;
import java.util.List;

public class DisplayAllProjectsInfo extends AppCompatActivity {
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    TextView tvtittle,tvdescription;
    TextView btnaccept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_projects_info);
        mLoginFormView= findViewById(R.id.login_form);
        mProgressView= findViewById(R.id.login_progress);
        tvLoad= findViewById(R.id.tvLoad);
        tvtittle = findViewById(R.id.tvtittle);
        tvdescription = findViewById(R.id.tvdescription);
        btnaccept = findViewById(R.id.btnaccept);
        final int index=getIntent().getIntExtra("position",0);
        tvtittle.setText(ApplicationClass.projects.get(index).getProjectName());
        tvdescription.setText(ApplicationClass.projects.get(index).getDescription());
        btnaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                if (!ApplicationClass.userenrolledprojectids.contains(ApplicationClass.projects.get(index).getProjectId())) {
                    final String enrolledprojects = ApplicationClass.userData.get(0).getEnrolledProjects() + ApplicationClass.projects.get(index).getProjectId() + ",";
                    ApplicationClass.userData.get(0).setEnrolledProjects(enrolledprojects);
                    Backendless.Persistence.of(UserScore.class).save(ApplicationClass.userData.get(0), new AsyncCallback<UserScore>() {
                        @Override
                        public void handleResponse(UserScore response) {
                            Toast.makeText(DisplayAllProjectsInfo.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            fetchFullData();
                            showProgress(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(DisplayAllProjectsInfo.this, "Error " + fault.getMessage(), Toast.LENGTH_LONG).show();
                            showProgress(false);

                        }
                    });
                }
                else
                {
                    Toast.makeText(DisplayAllProjectsInfo.this,"This project is already enrolled by you!",Toast.LENGTH_LONG).show();
                    showProgress(false);
                }
            }
        });
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
                Toast.makeText(DisplayAllProjectsInfo.this, "Error "+fault.getMessage(), Toast.LENGTH_SHORT).show();
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
                if(response.size()!=0){
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
                showProgress(false);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(DisplayAllProjectsInfo.this,"Unable to fetch Data",Toast.LENGTH_LONG).show();
                showProgress(false);
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
