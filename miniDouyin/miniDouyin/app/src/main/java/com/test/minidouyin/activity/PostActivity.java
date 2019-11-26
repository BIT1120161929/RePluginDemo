package com.test.minidouyin.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.loader.a.PluginAppCompatActivity;
import com.test.minidouyin.R;
import com.test.minidouyin.network.RetrofitManager;
import com.test.minidouyin.network.beans.PostVideoResponse;
import com.test.minidouyin.network.service.VideoListService;
import com.test.minidouyin.utils.GetPathFromUri;
import com.test.minidouyin.utils.GetUriFromPath;
import com.test.minidouyin.utils.ResourceUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.test.minidouyin.utils.SaveBitmaputil.getVideoThumb;
import static com.test.minidouyin.utils.SaveBitmaputil.saveBitmap;

/**
 * 上传Activity，上方为VideoView，下方为ImageView，点击按钮上传
 * 可以有缺省的图片URI，实现了自动获取第一帧的封面图上传
 */
public class PostActivity extends PluginAppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;

    private VideoView videoView;
    private ImageView imageView;
    private Button btnPost;

    private Uri mSelectedImage;
    private Uri mSelectedVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        videoView = findViewById(R.id.ac_post_vv_video);
        imageView = findViewById(R.id.ac_post_iv_cover);
        btnPost = findViewById(R.id.ac_post_btn_post);
        Uri shootUri = getIntent().getData();

        if (shootUri != null) {
            mSelectedVideo = shootUri;
            videoView.setVideoURI(mSelectedVideo);
            videoView.start();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestReadExternalStoragePermission("select an image")) {
                    chooseImage();
                }
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestReadExternalStoragePermission("select a video")) {
                    chooseVideo();
                }
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(mSelectedVideo);
                if (mSelectedVideo != null && "POST".equals(btnPost.getText())) {
                    postVideo();
                }
                if ("BACK".equals(btnPost.getText())) {
                    btnPost.setBackgroundColor(Color.GRAY);
                    btnPost.setText("POST");
                    PostActivity.this.finish();
                }
            }
        });

    }


    private void init() {
        imageView.setImageBitmap(null);
        videoView.destroyDrawingCache();
        btnPost.setBackgroundColor(Color.GRAY);
        btnPost.setText("POST");
        mSelectedImage = null;
        mSelectedVideo = null;
    }

    private boolean requestReadExternalStoragePermission(String explanation) {
        if (ActivityCompat.checkSelfPermission(PostActivity.this==null? RePlugin.getPluginContext():this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PostActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(PostActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, MainActivity.REQUEST_PERMISSIONS);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * 选择上传的图片
     */
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    /**
     * 选择上传的视频
     */
    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                imageView.setImageURI(mSelectedImage);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                videoView.setVideoURI(mSelectedVideo);
                videoView.start();
            }
        }
    }

    /**
     * 实现了自动获取封面
     */
    private void postVideo() {
        Retrofit retrofit = RetrofitManager.get("http://test.androidcamp.bytedance.com/");

        if(mSelectedImage==null){
            String videoPath = GetPathFromUri.getPath(this,mSelectedVideo);
            Bitmap bitmap = getVideoThumb(videoPath);
            String path = saveBitmap(this,bitmap);

            File file = new File(path);
            mSelectedImage = GetUriFromPath.getImageContentUri(this,file);
        }
        MultipartBody.Part video = getMultipartFromUri("video", mSelectedVideo);
        MultipartBody.Part image = getMultipartFromUri("cover_image", mSelectedImage);

        Call<PostVideoResponse> call = retrofit.create(VideoListService.class).postVideo("1120161929", "QSJ", image, video);
        call.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                PostVideoResponse body = response.body();
                if (body != null && body.isSuccess()) {
                    btnPost.setBackgroundColor(Color.GREEN);
                    btnPost.setText("BACK");
                }
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
            }
        });
    }

    /**
     * 得到上传的Multipart
     * @param name
     * @param uri
     * @return
     */
    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }


}
