package com.example.annotate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;

public class DisplayAdminProjects extends AppCompatActivity {
    View mProgressView;
    View mLoginFormView;
    TextView tvLoad;

    ListView listprojects;
    ProjectAdminAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_admin_projects);
        mLoginFormView= findViewById(R.id.login_form);
        mProgressView= findViewById(R.id.login_progress);
        tvLoad= findViewById(R.id.tvLoad);
        listprojects=findViewById(R.id.listprojects);
        listprojects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(DisplayAdminProjects.this,ProjectInfo.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        showProgress(true);
        List<Projects>  adminprojects = new ArrayList<>();
        for(int i=0;i<ApplicationClass.projects.size();i++)
        {
            if(ApplicationClass.projects.get(i).getUserEmail().equals(ApplicationClass.user.getEmail()))
            {
                adminprojects.add(ApplicationClass.projects.get(i));
            }
        }
        ApplicationClass.adminpprojects=adminprojects;
        adapter= new ProjectAdminAdapter(DisplayAdminProjects.this,adminprojects);
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
        menu.removeItem(R.id.showallprojects);
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
                        Toast.makeText(DisplayAdminProjects.this,"logout successfull",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DisplayAdminProjects.this,Login.class));
                        DisplayAdminProjects.this.finish();
                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        Toast.makeText(DisplayAdminProjects.this,"Error "+fault.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.showallprojects:
                startActivity(new Intent(DisplayAdminProjects.this,DisplayAllProjects.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
