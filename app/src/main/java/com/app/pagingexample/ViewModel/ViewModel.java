package com.app.pagingexample.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

import com.app.pagingexample.DataSource.ItemDataSource;
import com.app.pagingexample.DataSource.ItemDataSourceFactory;
import com.app.pagingexample.Model.StackApiResponse;

public class ViewModel extends androidx.lifecycle.ViewModel {
    //creating livedata for PagedList  and PagedKeyedDataSource
    public LiveData<PagedList<StackApiResponse.Item>> itemPagedList;
    LiveData<PageKeyedDataSource<Integer, StackApiResponse.Item>> liveDataSource;

    public ViewModel(){
        ItemDataSourceFactory itemDataSourceFactory = new ItemDataSourceFactory();
//        liveDataSource = itemDataSourceFactory.getItemLiveDataSource();

        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setPageSize(ItemDataSource.PAGE_SIZE)
                .build();

        itemPagedList = (new LivePagedListBuilder(itemDataSourceFactory, config)).build();
    }
}
