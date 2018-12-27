package com.td.yassine.zekri.melomeet.posts;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.td.yassine.zekri.melomeet.R;
import com.td.yassine.zekri.melomeet.utils.FilePaths;
import com.td.yassine.zekri.melomeet.utils.FileSearch;
import com.td.yassine.zekri.melomeet.utils.GridImageAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryPostFragment extends Fragment {

    private static final String TAG = "GalleryPostFragment";
    private static final int NUM_GRID_COLUMNS = 3;

    @BindView(R.id.spinnerDirectory)
    Spinner mSpinner;
    @BindView(R.id.gridView)
    GridView mGridView;
    @BindView(R.id.galleryImageView)
    ImageView mGalleryImage;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_gallery, container, false);
        Log.d(TAG, "onCreateView: started.");

        ButterKnife.bind(this, view);

        mContext = getActivity();
        directories = new ArrayList<>();

        init();

        return view;
    }

    private void init() {
        FilePaths filePaths = new FilePaths();

        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        ArrayList<String> directoriesNames = new ArrayList<>();
        directories.add(filePaths.CAMERA);
        for (int i = 0; i < directories.size(); i++) {
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoriesNames.add(string);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemSelected: selected: " + directories.get(position));
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupGridView(String selectedDirectory) {
        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
        final ArrayList<String> imgUrls = FileSearch.getFilePaths(selectedDirectory);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        mGridView.setColumnWidth(imageWidth);

        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imgUrls);
        mGridView.setAdapter(adapter);

        setImage(imgUrls.get(0), mGalleryImage, mAppend);
        mSelectedImage = imgUrls.get(0);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemClick: selected an image: " + imgUrls.get(position));

                setImage(imgUrls.get(position), mGalleryImage, mAppend);
                mSelectedImage = imgUrls.get(position);
            }
        });
    }

    private void setImage(String imgURL, ImageView image, String append) {
        Log.d(TAG, "setImage: setting image.");
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image);
    }
}
