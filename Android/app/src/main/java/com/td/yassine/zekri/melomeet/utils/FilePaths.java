package com.td.yassine.zekri.melomeet.utils;

import android.os.Environment;

public class FilePaths {
    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";

    //firebase
    public String FIREBASE_THUMB_PROFILE_IMAGE = "profile_thumb_images";
    public String FIREBASE_POST_IMAGE_STORAGE = "images/posts";
}
