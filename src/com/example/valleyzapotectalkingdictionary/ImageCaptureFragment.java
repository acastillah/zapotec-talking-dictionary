package com.example.valleyzapotectalkingdictionary;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class ImageCaptureFragment extends Fragment {
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 1;
	
	private static String mFileName = null;
	
	private ImageCaptureButton mImageCaptureButton = null;
	private ImageView mImageView = null;
	private FileNameEditText mFileNameEditText = null;
	private SaveButton mSaveButton = null;
	
	private boolean mImageCaptured = false;
	String mCurrentPhotoPath;
	
    private static final String mFileExtension = ".jgp"; // WHAT FILE TYPE?
    private static final String dictionaryDirectoryName = "Zapotec Talking Dictionary";
    private static final String photoDirectoryName = "photos";
    private static String photoDirectoryFullPath = null;
	
	public ImageCaptureFragment() {}
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);
    	
    	FragmentActivity activity = getActivity();
        
        LinearLayout fragmentLayout = new LinearLayout(activity);
        fragmentLayout.setOrientation(LinearLayout.VERTICAL);
        
        mImageCaptureButton = new ImageCaptureButton(activity);
        fragmentLayout.addView(mImageCaptureButton,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        mImageView = new ImageView(activity);
        fragmentLayout.addView(mImageView,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        mFileNameEditText = new FileNameEditText(activity);
        fragmentLayout.addView(mFileNameEditText,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        mSaveButton = new SaveButton(activity);
        fragmentLayout.addView(mSaveButton,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        activity.setContentView(fragmentLayout);
        
        Log.i("DIR", "Setting audio dir path");
        
        // if (Environment.getExternalStorageState() == true) {
         	photoDirectoryFullPath = Environment.getExternalStorageDirectory().getAbsolutePath();
         	Log.i("DIR", "audio dir full path=" + photoDirectoryFullPath);
         	
         	File appDir = new File(photoDirectoryFullPath, dictionaryDirectoryName);
         	if (!appDir.exists()) {
         		boolean success = appDir.mkdir();
         		Log.i("DIR", "appDir created=" + success);
         	}
         	else {
         		Log.i("DIR", "appDir exists");
         	}
         	
         	if (appDir.exists() && appDir.isDirectory()) {
         		Log.i("DIR", "appDir exists & is a directory");
         		
         		if (!appDir.canWrite())
         			appDir.setWritable(true);
         		
         		photoDirectoryFullPath += "/" + dictionaryDirectoryName;
         		Log.i("DIR", "audio dir full path=" + photoDirectoryFullPath);
         		        		
         		File audioDir = new File(photoDirectoryFullPath, photoDirectoryName);
         		if (!audioDir.exists()) {
         			audioDir.mkdir();
         		}
         		
         		if (audioDir.isDirectory()) {
         			if (!audioDir.canWrite())
         				audioDir.setWritable(true);
         			
         			photoDirectoryFullPath += "/" + photoDirectoryName;
         			Log.i("DIR", "audio dir full path=" + photoDirectoryFullPath);
         		}
         		
         	}
         	else {
         		Log.i("DIR", "app directory is not a directory");
         	}
         	
//         }
//         else {
//         	mRecordButton.setEnabled(false);
//         }
         	
         	
         	
         	
         	mFileName = photoDirectoryFullPath;
             mFileName += "/temp";
             mFileName += mFileExtension;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_image_capture, container, false);
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        mImageView.setImageBitmap(imageBitmap);
	    }
	}
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
		
//		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//	    // Ensure that there's a camera activity to handle the intent
//	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//	        // Create the File where the photo should go
//	        File photoFile = null;
//	        try {
//	            photoFile = createImageFile();
//	        } catch (IOException ex) {
//	            // Error occurred while creating the File
//	            //...
//	        }
//	        // Continue only if the File was successfully created
//	        if (photoFile != null) {
//	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//	                    Uri.fromFile(photoFile));
//	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//	        }
//	    }
	}
	
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	    return image;
	}
	
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    getActivity().sendBroadcast(mediaScanIntent);
	}
	
	private class ImageCaptureButton extends Button {

		OnClickListener clicker = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File image = new File(Environment.getExternalStorageDirectory(), "testImage.jpg");
				
				if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
			        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
			        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
			    }
				
				
				
				
				dispatchTakePictureIntent();
				
				////// not getting to here... it's making a new intent?
				
				
				mImageCaptured = true;
				
				// user must enter file name before saving & there must be picture captured to save
				if (!mFileNameEditText.getText().toString().equals("")) {
					mSaveButton.setEnabled(true); 
            	}
            	else {
            		mSaveButton.setEnabled(false); 
            	}
			}
			
		};
		
		public ImageCaptureButton(Context context) {
			super(context);
			setOnClickListener(clicker);
			setText("Take Picture");
		}
		
	}
	
	private class SaveButton extends Button {

		OnClickListener clicker = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mImageCaptureButton.setEnabled(false);
				mFileNameEditText.setEnabled(false);
			}
			
		};
		
		public SaveButton(Context context) {
			super(context);
			setOnClickListener(clicker);
			setText("Save");
			setEnabled(false);
		}
		
	}
	
	private class FileNameEditText extends EditText {

		TextWatcher watcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				// user must enter file name before saving & there must be picture captured to save
				if (s.length() != 0 && mImageCaptured == true) {
					mSaveButton.setEnabled(true); 
            	}
            	else {
            		mSaveButton.setEnabled(false); 
            	}
			}
			
		};
		
		public FileNameEditText(Context context) {
			super(context);
			addTextChangedListener(watcher);
		}
		
	}
}
