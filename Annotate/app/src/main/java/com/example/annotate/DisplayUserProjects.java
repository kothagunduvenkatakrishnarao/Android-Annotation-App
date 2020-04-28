package com.example.annotate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayUserProjects extends AppCompatActivity {
    View mProgressView;
    View mLoginFormView;
    TextView tvLoad;

    ListView listprojects;
    ProjectUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_projects);
        mLoginFormView= findViewById(R.id.login_form);
        mProgressView= findViewById(R.id.login_progress);
        tvLoad= findViewById(R.id.tvLoad);
        listprojects=findViewById(R.id.listprojects);
        showProgress(true);
        List<Projects> userprojects = new ArrayList<>();
        String[] stringId = ApplicationClass.userData.get(0).getEnrolledProjects().split(",");
        Arrays.sort(stringId);
        for(int i=0;i<stringId.length;i++)
        {
            for(int j=0;j<ApplicationClass.projects.size();j++)
            {
                if(ApplicationClass.projects.get(j).getProjectId() == Long.parseLong(stringId[i]))
                {
                    userprojects.add(ApplicationClass.projects.get(j));
                    break;
                }
            }
        }
        adapter = new ProjectUserAdapter(DisplayUserProjects.this,userprojects);
        listprojects.setAdapter(adapter);
        showProgress(false);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
                Backendless.UserService.logout(new AsyncCallback<Void>()
                {
                    public void handleResponse( Void response )
                    {
                        Toast.makeText(DisplayUserProjects.this,"logout successfull",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DisplayUserProjects.this,Login.class));
                        DisplayUserProjects.this.finish();
                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        Toast.makeText(DisplayUserProjects.this,"Error "+fault.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.showallprojects:
                startActivity(new Intent(DisplayUserProjects.this,DisplayAllProjects.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
