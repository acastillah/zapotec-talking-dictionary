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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ImageCaptureFragment extends Fragment {
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 1;
	
	private static String mFileName = null;
	
	private ImageCaptureButton mImageCaptureButton = null;
	private TextView mPreviewText = null;
	private ImageView mImageView = null;
	private FileNameEditText mFileNameEditText = null;
	private SaveButton mSaveButton = null;
	
	private boolean mImageCaptured = false;
	String mCurrentPhotoPath;
	
    private static final String mFileExtension = ".jgp"; // WHAT FILE TYPE?
    private static final String dictionaryDirectoryName = "Zapotec Talking Dictionary";
    private static final String photoDirectoryName = "photos";
    private static String photoDirectoryFullPath = null;
    
    private static String mTempFilePath = null;
	
	public ImageCaptureFragment() {}
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);
    	
    	Log.i("IMAGE", "ImageCaptureFragment entered onCreate method");
    	
    	
        
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
             mFileName += "/blah";
             mFileName += mFileExtension;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_image_capture, container, false);
		
		FragmentActivity activity = getActivity();
        
        LinearLayout fragmentLayout = new LinearLayout(activity);
        fragmentLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        
        TextView photoFragmentDescription = new TextView(activity);
        photoFragmentDescription.setText("Take a photo and upload it.");
        photoFragmentDescription.setPadding(0, 20, 0, 20);
        fragmentLayout.addView(photoFragmentDescription);
        
        mImageCaptureButton = new ImageCaptureButton(activity);
        fragmentLayout.addView(mImageCaptureButton,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        mImageView = new ImageView(activity);
        fragmentLayout.addView(mImageView);
        
        mPreviewText = new TextView(activity);
        mPreviewText.setText(R.string.preview_image);
        mPreviewText.setVisibility(View.INVISIBLE);
        fragmentLayout.addView(mPreviewText);
        
        LinearLayout fileNameLayout = new LinearLayout(activity);
        fileNameLayout.setOrientation(LinearLayout.HORIZONTAL);
        
        TextView namePhotoTextView = new TextView(activity);
        namePhotoTextView.setText("Photo name:");
        fileNameLayout.addView(namePhotoTextView);
        
        mFileNameEditText = new FileNameEditText(activity);
        mFileNameEditText.setEms(10);
        fileNameLayout.addView(mFileNameEditText,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        TextView imageExtension = new TextView(activity);
        imageExtension.setText(".jpg");
        fileNameLayout.addView(imageExtension);
        
        fragmentLayout.addView(fileNameLayout,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        
        mSaveButton = new SaveButton(activity);
        fragmentLayout.addView(mSaveButton,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        return fragmentLayout;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//	        Bundle extras = data.getExtras();
//	        Bitmap imageBitmap = (Bitmap) extras.get("data");
//	        mImageView.setImageBitmap(imageBitmap);
	    	
//	    	File image = new File(mFileName);
	    	
	    	File image = new File(mTempFilePath);
	    			
	    			
	    	Log.i("FILE", "File path=" + mTempFilePath);
	    	
	    	mImageCaptured = true;
			mPreviewText.setVisibility(View.VISIBLE);

	    	BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    	Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
	    	
	    	
//	    	bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);

	    	double ratio = bitmap.getWidth() / bitmap.getHeight();
	    	Log.i("BITMAP", "original width=" + bitmap.getWidth() + " height=" + bitmap.getHeight() + " ratio=" + ratio);
	    	
	    	Point size = new Point();
	    	getActivity().getWindowManager().getDefaultDisplay().getSize(size);
	    	
	    	Log.i("BITMAP", "window width=" + size.x + " height=" + size.y);
	    	
//	    	double width = 300;
//	    	double height = width / (bitmap.getWidth() / bitmap.getHeight());
	    	
	    	double width = bitmap.getWidth() / 2;
	    	double height = bitmap.getHeight() / 2;
	    	
	    	Log.i("BITMAP", "new width=" + width + " height=" + height);
	    	
	    	ExifInterface exif = null;
	    	
	    	try {
				exif = new ExifInterface(image.getPath());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	
	    	if (exif != null) {
		    	int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		    	
		    	Matrix m = new Matrix();
		    	
				if (orientation == 3)
		    		m.postRotate(180);
		    	else if (orientation == 6)
		    		m.postRotate(90);
		    	if (orientation == 8)
		    		m.postRotate(270);
		    	
		    	
		    	bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int)width, (int)height, m, true);
		    	
//		    	bitmap = Bitmap.createScaledBitmap(bitmap, (int)width, (int)height, false);
		    	
		    	mImageView.setImageBitmap(bitmap);
	    	
	    	}
	    	
	    	
	    	boolean renameSuccessful = image.renameTo(new File(photoDirectoryFullPath + "/PHOTO.jpg"));
	    }
	}
	
	private void dispatchTakePictureIntent() {
//	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//	    }
		
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	            //...
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    
//	    imageFileName = "temp";
	    
	    if (mTempFilePath != null) {
	    	File tempFile = new File(mTempFilePath);
	    	tempFile.delete();
	    }
	    
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );
	    
	    mTempFilePath = image.getAbsolutePath();
	    
	    
//	    File image = new File(storageDir.getAbsoluteFile() + "/temp.jpg");
//	    if (image.exists())
//	    	image.delete();
		
		
//	    File storageDir = Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_PICTURES);
//	    File image = File.createTempFile("temp", ".jgp", new File(photoDirectoryFullPath));
	    
//	    File image = new File(mFileName);
//		File image = new File(mFileName);

	    // Save a file: path for use with ACTION_VIEW intents
//	    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	    return image;
	}
	
//	private void galleryAddPic() {
//	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//	    File f = new File(mCurrentPhotoPath);
//	    Uri contentUri = Uri.fromFile(f);
//	    mediaScanIntent.setData(contentUri);
//	    getActivity().sendBroadcast(mediaScanIntent);
//	}
	
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
				mSaveButton.setEnabled(false);
				
				String userDefinedFileName = mFileNameEditText.getText().toString();
				
				String newFileName = photoDirectoryFullPath;
	            newFileName += "/" + userDefinedFileName;
	            newFileName += mFileExtension;
				
				File image = new File(mTempFilePath);
				boolean renameSuccessful = image.renameTo(new File(newFileName));
				
				Toast.makeText(getActivity(), "Photo saved", Toast.LENGTH_SHORT).show();
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
