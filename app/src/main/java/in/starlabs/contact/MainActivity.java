package in.starlabs.contact;

import android.content.Context;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private EditText search;
    List<String> myDataset;
    HashMap<String, Integer> mapIndex;
    private final static String API_KEY = "7e8f60e325cd06e164799af1e317d7a7";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = (EditText) findViewById(R.id.search);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        ApiInterface apiService =
                ConnectionApi.getClient().create(ApiInterface.class);

        Call<MovieResponse> call = apiService.getTopRatedMovies(API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                List<Movie> movies = response.body().getResults();
                for(int i=0;i<movies.size();i++){
                    myDataset.add(movies.get(i).getTitle());
                }
                Log.d("", "Number of movies received: " + movies.size());



            }

            @Override
            public void onFailure(Call<MovieResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e("", t.toString());
            }
        });


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        myDataset = new ArrayList<String>();
//        for (int i = 0; i < 26; i++) {
//            myDataset.add(Character.toString((char) (65 + i)) + " Row item");
//        }
        mapIndex = calculateIndexesForName(myDataset);
        mAdapter = new MyAdapter(myDataset, mapIndex);
        mRecyclerView.setAdapter(mAdapter);
        FastScrollRecyclerViewItemDecoration decoration = new FastScrollRecyclerViewItemDecoration(MainActivity.this);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());




        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {


                query = query.toString().toLowerCase();
                Log.i("Action Log  - ", "search query - " + query);

                final List<String> filteredList = new ArrayList<>();

                for (int i = 0; i < myDataset.size(); i++) {

                    final String text = myDataset.get(i).toLowerCase();
                    if (text.contains(query)) {

                        filteredList.add(myDataset.get(i));

                    }
                }

                mAdapter = new MyAdapter(filteredList, mapIndex);
                mRecyclerView.setAdapter(mAdapter);
                FastScrollRecyclerViewItemDecoration decoration = new FastScrollRecyclerViewItemDecoration(MainActivity.this);
                mRecyclerView.addItemDecoration(decoration);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });


    }


    private HashMap<String, Integer> calculateIndexesForName(List<String> items) {
        HashMap<String, Integer> mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < items.size(); i++) {
            String name = items.get(i);
            String index = name.substring(0, 1);
            index = index.toUpperCase();

            if (!mapIndex.containsKey(index)) {
                mapIndex.put(index, i);
            }
        }
        return mapIndex;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();

            if (search.isFocused()) {
                Rect outRect = new Rect();
                search.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    search.clearFocus();
                    search.setText("");
                    //
                    // Hide keyboard
                    //
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
