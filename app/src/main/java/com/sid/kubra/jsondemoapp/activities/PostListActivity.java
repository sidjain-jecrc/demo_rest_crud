package com.sid.kubra.jsondemoapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.sid.kubra.jsondemoapp.R;
import com.sid.kubra.jsondemoapp.adapter.PostListAdapter;
import com.sid.kubra.jsondemoapp.model.UserPost;
import com.sid.kubra.jsondemoapp.restclient.JsonPlaceRestClient;

import java.util.Comparator;
import java.util.List;

public class PostListActivity extends AppCompatActivity {

    protected static final String TAG = PostListActivity.class.getSimpleName();
    protected ListView list_user_post;
    protected ArrayAdapter<UserPost> userPostAdapter;
    protected String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        Intent intent = getIntent();
        userId = intent.getStringExtra("EXTRA_USER_ID");

        list_user_post = (ListView) findViewById(R.id.list_user_post);
        if (list_user_post != null) {
            new RetrieveUserPostListTask().execute(userId);
        } else {
            Log.d(TAG, "listview is NULL");
        }
    }

    private class RetrieveUserPostListTask extends AsyncTask<String, Void, List<UserPost>> {

        ProgressDialog pd = new ProgressDialog(PostListActivity.this, ProgressDialog.STYLE_SPINNER);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("loading posts..");
            pd.show();
        }

        @Override
        protected List<UserPost> doInBackground(String... input) {
            String userId = input[0];
            if (userId == null || userId.equals("")) {
                userId = "1";
                Log.d(TAG, "String id is coming as NULL or EMPTY");
            }
            JsonPlaceRestClient client = new JsonPlaceRestClient();
            List<UserPost> posts = client.requestUserPosts(userId);
            return posts;
        }

        protected void onPostExecute(List<UserPost> posts) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (posts != null && posts.size() > 0) {
                userPostAdapter = new PostListAdapter(PostListActivity.this, posts);
                list_user_post.setAdapter(userPostAdapter);
            } else {
                Log.d(TAG, "no posts found for selected user");
                Toast.makeText(PostListActivity.this, "no posts found for selected user", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_sort_asc:
                userPostAdapter.sort(new Comparator<UserPost>() {
                    @Override
                    public int compare(UserPost post1, UserPost post2) {
                        return post1.getTitle().compareTo(post2.getTitle());
                    }
                });
                list_user_post.setAdapter(userPostAdapter);
                userPostAdapter.notifyDataSetChanged();
                return true;

            case R.id.action_sort_desc:
                userPostAdapter.sort(new Comparator<UserPost>() {
                    @Override
                    public int compare(UserPost post1, UserPost post2) {
                        return post2.getTitle().compareTo(post1.getTitle());
                    }
                });
                list_user_post.setAdapter(userPostAdapter);
                userPostAdapter.notifyDataSetChanged();
                return true;

            case R.id.action_add_post:
                Intent jumpToAddPost = new Intent(PostListActivity.this, AddPostActivity.class);
                jumpToAddPost.putExtra("EXTRA_USER_ID", userId);
                startActivity(jumpToAddPost);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}