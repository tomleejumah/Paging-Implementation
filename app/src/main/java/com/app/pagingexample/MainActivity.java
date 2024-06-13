package com.app.pagingexample;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pagingexample.API.ApiClient;
import com.app.pagingexample.Adapter.ItemAdapter;
import com.app.pagingexample.Model.StackApiResponse;
import com.app.pagingexample.ViewModel.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.rcItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        ViewModel viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        final ItemAdapter adapter = new ItemAdapter(this);

        viewModel.itemPagedList.observe(this, new Observer<PagedList<StackApiResponse.Item>>() {
            @Override
            public void onChanged(PagedList<StackApiResponse.Item> items) {
                adapter.submitList(items);
            }
        });

        recyclerView.setAdapter(adapter);
    }
 void fetch(){
     Call<StackApiResponse> call = ApiClient.getInstance()
             .getApi().
             getAnswers(1,50,"stackoverflow");

     call.enqueue(new Callback<StackApiResponse>() {
         @Override
         public void onResponse(Call<StackApiResponse> call, Response<StackApiResponse> response) {
             StackApiResponse stackApiResponse = response.body();
             Log.d(TAG, "onResponse: "+ stackApiResponse.has_more);
         }

         @Override
         public void onFailure(Call<StackApiResponse> call, Throwable throwable) {

         }
     });
 }
}