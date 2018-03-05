package com.circlecorp.red_win3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    InstagramTopJSONCatcher catcher;
    HeaderJSONCatcher header;
    BlogJSONCatcher blog;

    CircleImageView avatarHeader;
    TextView nameHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        avatarHeader = navigationView.getHeaderView(0).findViewById(R.id.avatar_header);
        nameHeader = navigationView.getHeaderView(0).findViewById(R.id.name_header);

        header = new HeaderJSONCatcher(this);
        header.execute();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        HomeFragment fragment = new HomeFragment();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.nav_home)
        {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            HomeFragment fragment = new HomeFragment();
            transaction.replace(R.id.container, fragment);
            transaction.commit();
        }
        else if (id == R.id.nav_blog)
        {
            blog = new BlogJSONCatcher(this);
            blog.execute();
        }
        else if (id == R.id.nav_instagram)
        {
            catcher = new InstagramTopJSONCatcher(this);
            catcher.execute();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class InstagramTopJSONCatcher extends AsyncTask<Void, Void, String>
    {
        private ProgressDialog dialog;

        public InstagramTopJSONCatcher(Context context)
        {
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialogInterface)
                {
                    // остановка AsyncTask
                }
            });
            dialog.show();

        }

        @Override
        protected String doInBackground(Void... voids)
        {
            ConnectivityManager cm = (ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected())
            {
                HttpRequest request = HttpRequest.get
                        ("https://api.instagram.com/v1/users/self/?access_token=6728403252.fb5c9d0.fd45fe431e5f41f9b8b314639fb2181e");
                request.connectTimeout(1000);
                if (request.code() == 200)
                {
                    return request.body();
                }
                else
                    return "connection_failed";
            }
            else
            {
                return "connection_failed";
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            dialog.dismiss();

            if (!s.equals("connection_failed"))
            {
                InstagramFragment.body_json_top = s;

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                InstagramFragment fragment = new InstagramFragment();
                transaction.replace(R.id.container, fragment);
                transaction.commit();
            }
            else
            {
                Toast.makeText(getBaseContext(), "Cannot load data. Please, check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class HeaderJSONCatcher extends AsyncTask<Void, Void, String>
    {
        private ProgressDialog dialog;
        Context context;

        public HeaderJSONCatcher(Context context)
        {
            this.dialog = new ProgressDialog(context);
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialogInterface)
                {
                    // остановка AsyncTask
                }
            });
            dialog.show();

        }

        @Override
        protected String doInBackground(Void... voids)
        {
            ConnectivityManager cm = (ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected())
            {
                HttpRequest request = HttpRequest.get
                        ("https://api.instagram.com/v1/users/self/?access_token=6728403252.fb5c9d0.fd45fe431e5f41f9b8b314639fb2181e");
                request.connectTimeout(1000);
                if (request.code() == 200)
                {
                    return request.body();
                }
                else
                    return "connection_failed";
            }
            else
            {
                return "connection_failed";
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            dialog.dismiss();

            if (!s.equals("connection_failed"))
            {
                try
                {
                    JSONObject jsonObjectTop = new JSONObject(s);
                    JSONObject data = jsonObjectTop.getJSONObject("data");

                    String a = data.getString("profile_picture");

                    Glide.with(context).load(Uri.parse(a)).into(avatarHeader);
                    nameHeader.setText(data.getString("full_name"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public class BlogJSONCatcher extends AsyncTask<Void, Void, String>
    {
        private ProgressDialog dialog;
        Context context;

        public BlogJSONCatcher(Context context)
        {
            this.dialog = new ProgressDialog(context);
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialogInterface)
                {
                    // остановка AsyncTask
                }
            });
            dialog.show();

        }

        @Override
        protected String doInBackground(Void... voids)
        {
            ConnectivityManager cm = (ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected())
            {
                HttpRequest request = HttpRequest.get
                        ("https://api.instagram.com/v1/users/self/?access_token=6728403252.fb5c9d0.fd45fe431e5f41f9b8b314639fb2181e");
                request.connectTimeout(1000);
                if (request.code() == 200)
                {
                    return request.body();
                }
                else
                    return "connection_failed";
            }
            else
            {
                return "connection_failed";
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            dialog.dismiss();

            if (!s.equals("connection_failed"))
            {
                BlogFragment.header_json = s;

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                BlogFragment fragment = new BlogFragment();
                transaction.replace(R.id.container, fragment);
                transaction.commit();
            }
            else
            {
                Toast.makeText(getBaseContext(), "Cannot load data. Please, check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }
}
