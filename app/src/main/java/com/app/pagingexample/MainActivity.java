package com.app.pagingexample;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pagingexample.API.ApiClient;
import com.app.pagingexample.Adapter.ItemAdapter;
import com.app.pagingexample.Model.StackApiResponse;
import com.app.pagingexample.ViewModel.ViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private List<String> list;
    private ItemTouchHelper dragHelper;
    private ItemTouchHelper swipeHelper;
    private int width;
    private Drawable deleteIcon;
    private Drawable archiveIcon;
    private int deleteColor;
    private int archiveColor;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int height = dp((int) (displayMetrics.heightPixels / displayMetrics.density));
        width = dp((int) (displayMetrics.widthPixels / displayMetrics.density));

        deleteIcon = getResources().getDrawable(R.drawable.ic_launcher_foreground, null);
        archiveIcon = getResources().getDrawable(R.drawable.ic_launcher_foreground, null);

        deleteColor = getResources().getColor(android.R.color.holo_red_light);
        archiveColor = getResources().getColor(android.R.color.holo_green_light);

        list = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            list.add("Item " + i);
        }

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

        swipeHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                list.remove(pos);
                adapter.notifyItemRemoved(pos);

//                Snackbar.make(
//                        findViewById(R.id.ll_main),
//                        direction == ItemTouchHelper.RIGHT ? "Deleted" : "Archived",
//                        Snackbar.LENGTH_SHORT
//                ).show();
            }

            @Override
            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    int itemHeight = itemView.getHeight();

                    // Calculate bounds of the itemView for drawing purposes
                    int top = itemView.getTop();
                    int bottom = itemView.getBottom();
                    int left = itemView.getLeft();
                    int right = itemView.getRight();

                    // Define Paint objects for colors
                    Paint backgroundPaint = new Paint();

                    // Adjust background color based on swipe direction
                    if (Math.abs(dX) < width / 3) {
                        backgroundPaint.setColor(Color.GRAY);
                    } else if (dX > width / 3) {
                        backgroundPaint.setColor(deleteColor);
                    } else {
                        backgroundPaint.setColor(archiveColor);
                    }

                    // Draw background color within the bounds of the item
                    canvas.drawRect(left, top, right, bottom, backgroundPaint);

                    // Print icons within the bounds of the itemView
                    int iconSize = getResources().getDimensionPixelSize(R.dimen.icon_size); // Adjust as needed
                    int textMargin = getResources().getDimensionPixelSize(R.dimen.text_margin);

                    // Calculate icon positions
                    int deleteIconLeft = left + textMargin;
                    int deleteIconTop = top + (itemHeight - iconSize) / 2; // Center vertically
                    int deleteIconRight = deleteIconLeft + iconSize;
                    int deleteIconBottom = deleteIconTop + iconSize;

                    int archiveIconRight = right - textMargin;
                    int archiveIconLeft = archiveIconRight - iconSize;
                    int archiveIconTop = top + (itemHeight - iconSize) / 2; // Center vertically
                    int archiveIconBottom = archiveIconTop + iconSize;

                    // Set bounds for icons
                    deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                    archiveIcon.setBounds(archiveIconLeft, archiveIconTop, archiveIconRight, archiveIconBottom);

                    // Draw icons based on swipe direction
                    if (dX > 0) {
                        deleteIcon.draw(canvas);
                    } else {
                        archiveIcon.draw(canvas);
                    }

                    super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        });

        swipeHelper.attachToRecyclerView(recyclerView);

        dragHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                viewHolder.itemView.setElevation(dp(16));

                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                Collections.swap(list, from, to);
                adapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (viewHolder != null) {
                    viewHolder.itemView.setElevation(dp(16));
                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // No action needed for swipe
            }
        });
        dragHelper.attachToRecyclerView(recyclerView);

    }

    private int dp(int value) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics()));
    }

    public void startDragging(RecyclerView.ViewHolder holder) {
        dragHelper.startDrag(holder);
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