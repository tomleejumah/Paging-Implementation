package com.app.pagingexample;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pagingexample.Adapter.ItemAdapter;

public class SingleSwipeCallback extends ItemTouchHelper.Callback {
    private static final float TWO_BUTTON_RATIO = 1f / 3f;
    private static final float ONE_BUTTON_RATIO = 1f / 4f;
    private float lastDX = 0;
    private int currentPosition = RecyclerView.NO_POSITION;
    private static final String TAG = "SingleSwipeCallback";
    private final ItemAdapter adapter; // Add this line
    private RecyclerView recyclerView; // Add this line
    private boolean isSwiping = false;
    private final Drawable deleteIcon;
    private final Drawable editIcon;
    private final Drawable archiveIcon;
    private final int deleteColor;
    private final int editColor;
    private final int archiveColor;

    public SingleSwipeCallback(ItemAdapter adapter, Drawable deleteIcon, Drawable editIcon, Drawable archiveIcon,
                               int deleteColor, int editColor, int archiveColor) {
        this.adapter = adapter;
        this.deleteIcon = deleteIcon;
        this.editIcon = editIcon;
        this.archiveIcon = archiveIcon;
        this.deleteColor = deleteColor;
        this.editColor = editColor;
        this.archiveColor = archiveColor;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT) {
            // Implement archive action
//            Log.d(TAG, "onSwiped: swiped left");
        } else if (direction == ItemTouchHelper.RIGHT) {
//            Log.d(TAG, "onSwiped: swipe rigth");
            // Implement delete or edit action
        }
    }

//    @Override
//    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
//        if (viewHolder.getAdapterPosition() == adapter.openedPosition) {
//            // Item is fully swiped, update the openedPosition
//            adapter.openedPosition = viewHolder.getAdapterPosition();
//        }
//    }

    private void closeItem(int position) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            viewHolder.itemView.animate().translationX(0).setDuration(200).start();
        }
//        adapter.openedPosition = -1;
    }

    private void clearSwipeState() {
        if (adapter.openedPosition != -1) {
            closeItem(adapter.openedPosition);
        }
    }
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder != null) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    View itemView = viewHolder.itemView;
                    if (Math.abs(lastDX) < itemView.getWidth() * ONE_BUTTON_RATIO / 2) {
                        // If swiped less than half of the button width, close the item
                        adapter.closeOpenedItem();
                    } else {
                        // Update the opened position and offset
                        adapter.openedPosition = position;
                        adapter.openedOffset = lastDX;
                    }
                }
            }
            currentPosition = RecyclerView.NO_POSITION;
        } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (viewHolder != null) {
                currentPosition = viewHolder.getAdapterPosition();
                if (adapter.openedPosition != -1 && adapter.openedPosition != currentPosition) {
                    adapter.closeOpenedItem();
                }
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder.getAdapterPosition() != adapter.openedPosition) {
            viewHolder.itemView.setTranslationX(0);
        }
    }


    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        this.recyclerView = recyclerView;
        int position = viewHolder.getAdapterPosition();

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            float limitedDX;

            int itemWidth = itemView.getWidth();
            int itemHeight = itemView.getHeight();
            int top = itemView.getTop();
            int bottom = itemView.getBottom();
            int left = itemView.getLeft();
            int right = itemView.getRight();

            Paint backgroundPaint = new Paint();
            int iconSize = itemHeight / 2; // Adjust size as needed
            int textMargin = 16;

            // Calculate button widths based on screen ratios
            int twoButtonWidth = (int) (itemWidth * TWO_BUTTON_RATIO);
            int oneButtonWidth = (int) (itemWidth * ONE_BUTTON_RATIO);
            int singleButtonWidth = twoButtonWidth / 2;

            float newDX = dX;

            if (position == adapter.openedPosition) {
                newDX += adapter.openedOffset;
            } else if (adapter.openedPosition != -1 && adapter.openedPosition != position) {
                adapter.closeOpenedItem();
            }

            float swipeThreshold = itemView.getWidth() * ONE_BUTTON_RATIO;
            float maxSwipe = itemView.getWidth() * TWO_BUTTON_RATIO;

            // Limit the swipe
            if (newDX > 0) {
                newDX = Math.min(newDX, maxSwipe);
            } else {
                newDX = Math.max(newDX, -swipeThreshold);
            }

            lastDX = newDX;

            // Limit the swipe distance

            if (dX > 0) { // Swiping right
                limitedDX = Math.min(dX, twoButtonWidth);
            } else { // Swiping left
                limitedDX = Math.max(dX, -oneButtonWidth);
            }

            if (adapter.openedPosition != -1 && adapter.openedPosition != viewHolder.getAdapterPosition()) {
                // If another item is open, don't allow swiping
                viewHolder.itemView.setTranslationX(0);
            } else {
                viewHolder.itemView.setTranslationX(limitedDX);
            }


            if (dX > 0) { // Swiping right (delete and edit)
                boolean isTwoItemsExposed = limitedDX > oneButtonWidth;

                // Draw background for delete button
                backgroundPaint.setColor(deleteColor);
                canvas.drawRect(left, top, left + singleButtonWidth, bottom, backgroundPaint);

                // Draw delete icon
                deleteIcon.setBounds(left + textMargin, top + (itemHeight - iconSize) / 2,
                        left + textMargin + iconSize, top + (itemHeight + iconSize) / 2);
                deleteIcon.draw(canvas);

                if (isTwoItemsExposed) {
                    // Draw background for edit button
                    backgroundPaint.setColor(editColor);
                    canvas.drawRect(left + singleButtonWidth, top, left + twoButtonWidth, bottom, backgroundPaint);

                    // Draw edit icon
                    editIcon.setBounds(left + singleButtonWidth + textMargin, top + (itemHeight - iconSize) / 2,
                            left + singleButtonWidth + textMargin + iconSize, top + (itemHeight + iconSize) / 2);
                    editIcon.draw(canvas);
                }
            } else if (dX < 0) { // Swiping left (archive)
                // Draw background for archive button
                backgroundPaint.setColor(archiveColor);
                canvas.drawRect(right + limitedDX, top, right, bottom, backgroundPaint);

                // Draw archive icon
                archiveIcon.setBounds(right + (int)limitedDX + textMargin, top + (itemHeight - iconSize) / 2,
                        right + (int)limitedDX + textMargin + iconSize, top + (itemHeight + iconSize) / 2);
                archiveIcon.draw(canvas);
            }

            // Translate itemView by limitedDX
            itemView.setTranslationX(limitedDX);
        } else {
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
