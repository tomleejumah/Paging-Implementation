package com.app.pagingexample.DataSource;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.app.pagingexample.Model.StackApiResponse;

public class ItemDataSourceFactory extends DataSource.Factory {
    //creating the mutable live data
    private MutableLiveData<PageKeyedDataSource<Integer,
            StackApiResponse.Item>> itemLiveDataSource = new MutableLiveData<>();

    @Override
    public DataSource<Integer, StackApiResponse.Item> create() {
        ItemDataSource itemDataSource = new ItemDataSource();
        //posting the datasource to get the values
        itemLiveDataSource.postValue(itemDataSource);
        return itemDataSource;
    }

    public MutableLiveData<PageKeyedDataSource<Integer, StackApiResponse.Item>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }
}
