package com.example.annotate;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;

import java.util.HashSet;
import java.util.List;

public class Login extends AppCompatActivity {
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    EditText etMail,etPassword;
    Button btnLogin,btnRegister;
    TextView tvReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginFormView= findViewById(R.id.login_form);
        mProgressView= findViewById(R.id.login_progress);
        tvLoad= findViewById(R.id.tvLoad);
        etMail =findViewById(R.id.etMail);
        etPassword=findViewById(R.id.etPassword);
        btnLogin=findViewById(R.id.btnLogin);
        btnRegister=findViewById(R.id.btnRegister);
        tvReset=findViewById(R.id.tvReset);
        showProgress(true);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMail.getText().toString().trim().isEmpty() ||etPassword.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(Login.this,"Please Enter all the Feilds",Toast.LENGTH_LONG).show();
                }
                else
                {
                    String email=etMail.getText().toString().trim();
                    String password=etPassword.getText().toString().trim();
                    showProgress(true);
                    Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            ApplicationClass.user= response;
                            Toast.makeText(Login.this,"Login successfull!",Toast.LENGTH_LONG).show();
                            fetchFullData();
//                            fetchParticularUserData();
                            startActivity(new Intent(Login.this, MainActivity.class));
                            showProgress(false);
                            Login.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(Login.this,"Error "+fault.getMessage(),Toast.LENGTH_LONG).show();
                            showProgress(false);
                        }
                    }, true);
                }

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Register.class));
            }
        });
        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMail.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(Login.this,"enter Email",Toast.LENGTH_LONG).show();
                }
                else
                {
                    String email =etMail.getText().toString().trim();
                    showProgress(true);
                    Backendless.UserService.restorePassword(email, new AsyncCallback<Void>() {
                        @Override
                        public void handleResponse(Void response) {
                            Toast.makeText(Login.this,"Password ResetLink is Send to your mail",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(Login.this,"Error"+fault.getMessage(),Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });
                }
            }
        });

        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                if(response)
                {
                    String userObjectId = UserIdStorageFactory.instance().getStorage().get();
                    Backendless.Data.of(BackendlessUser.class).findById(userObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            ApplicationClass.user = response;
                            Log.i("email",""+ApplicationClass.user.getEmail());
                            showProgress(true);
                            fetchFullData();
                            startActivity(new Intent(Login.this, MainActivity.class));
                            showProgress(false);
                            Login.this.finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(Login.this,"Error "+fault.getMessage(),Toast.LENGTH_LONG).show();
                            showProgress(false);
                        }
                    });
                }
                else
                {
                    showProgress(false);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(Login.this,"Error"+fault.getMessage(),Toast.LENGTH_LONG).show();
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
                Toast.makeText(Login.this, "Error "+fault.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Login.this,"Unable to fetch Data",Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }
}