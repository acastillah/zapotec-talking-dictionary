package com.example.valleyzapotectalkingdictionary;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ImageCaptureFragment extends Fragment {
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 1;
	
	static final String INSIDE_WORD_DEFINITION = "INSIDE_WORD_DEFINITION";
	static final String LAUNCH_CAMERA = "LAUNCH_CAMERA";
	static final String FILE_NAME = "FILE_NAME";
	
	private static String mFileName = null;
	
	private ImageCaptureButton mImageCaptureButton = null;
	private TextView mPreviewText = null;
	private ImageView mImageView = null;
	private FileNameEditText mFileNameEditText = null;
	private SaveButton mSaveButton = null;
	
	private boolean mImageCaptured = false;
	String mCurrentPhotoPath;
	
    private static final String mFileExtension = ".jgp";
    private static final String dictionaryDirectoryName = "Zapotec Talking Dictionary";
    private static final String photoDirectoryName = "photos";
    private static String photoDirectoryFullPath = null;
    
//    private static String mFileName = null;
    
    private boolean imageSaved = false;
    
    private File image = null;
    
    private Bundle bundle = null;
	
    public ImageCaptureFragment() {}
    
	public ImageCaptureFragment(Bundle bundle) {
		this.bundle = bundle;
	}
	
	public static ImageCaptureFragment newInstance(Bundle bundle) {
		ImageCaptureFragment f = new ImageCaptureFragment(bundle);
        return f;
    }
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);
    	
    	Log.i("IMAGE", "ImageCaptureFragment entered onCreate method");
    	
    	
        
        Log.i("DIR", "Setting audio dir path");
        
//         if (Environment.getExternalStorageState() == true) {
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
        photoFragmentDescription.setText(R.string.imageFragmentDescription);
        photoFragmentDescription.setPadding(20, 20, 20, 20);
        fragmentLayout.addView(photoFragmentDescription);
        
        mImageCaptureButton = new ImageCaptureButton(activity);
        fragmentLayout.addView(mImageCaptureButton,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        mImageView = new ImageView(activity);
        mImageView.setPadding(0, 20, 0, 0);
        fragmentLayout.addView(mImageView);
        
        mPreviewText = new TextView(activity);
        mPreviewText.setText(R.string.preview_image);
        mPreviewText.setVisibility(View.INVISIBLE);
        fragmentLayout.addView(mPreviewText,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        
        LinearLayout fileNameLayout = new LinearLayout(activity);
        fileNameLayout.setOrientation(LinearLayout.HORIZONTAL);
        
        TextView namePhotoTextView = new TextView(activity);
        namePhotoTextView.setText(R.string.photoName);
        fileNameLayout.addView(namePhotoTextView);
        
        mFileNameEditText = new FileNameEditText(activity);
        mFileNameEditText.setEms(10);
        mFileNameEditText.setFilters(MainActivity.inputFilters);
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
        
        
//        Gallery gallery = new Gallery(activity);
//        
//        gallery.setAdapter(new ImageAdapter(activity));
//        
//        Gallery.LayoutParams galleryLayoutParams = (LayoutParams) gallery.getLayoutParams();
//        galleryLayoutParams.height = galleryLayoutParams.WRAP_CONTENT;
//        galleryLayoutParams.width = galleryLayoutParams.FILL_PARENT;
//        gallery.setLayoutParams(galleryLayoutParams);
        
        
        if (bundle == null || bundle.getBoolean(LAUNCH_CAMERA, true))
        	dispatchTakePictureIntent();
        
        if (bundle != null)
        	mFileNameEditText.setText(bundle.getString(FILE_NAME, ""));
        
//        if (savedInstanceState != null && savedInstanceState.containsKey(LAUNCH_CAMERA))
//        	Log.i("BUNDLE", "launch camera=" + savedInstanceState.getBoolean(LAUNCH_CAMERA));
        
        return fragmentLayout;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//	        Bundle extras = data.getExtras();
//	        Bitmap imageBitmap = (Bitmap) extras.get("data");
//	        Log.i("BUNDLE", "extras=" + extras.getByte("data"));
//	        mImageView.setImageBitmap(imageBitmap);
	    	
//	    	File image = new File(mFileName);
	    	
	    	image = new File(mFileName);
	    	
	    	Log.i("IMAGE", "exist? "+image.exists());
	    			
	    			
	    	Log.i("FILE", "File path=" + mFileName);
	    	
	    	mImageCaptured = true;
			mPreviewText.setVisibility(View.VISIBLE);

	    	Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), new BitmapFactory.Options());

	    	double ratio = bitmap.getWidth() / bitmap.getHeight();
	    	Log.i("BITMAP", "original width=" + bitmap.getWidth() + " height=" + bitmap.getHeight() + " ratio=" + ratio);
	    	
	    	Point size = new Point();
	    	getActivity().getWindowManager().getDefaultDisplay().getSize(size);
	    	
	    	Log.i("BITMAP", "window width=" + size.x + " height=" + size.y);
	    	
	    	double width = bitmap.getWidth() / 3;
	    	double height = bitmap.getHeight() / 3;
	    	
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

		    	mImageView.setImageBitmap(bitmap);
	    	
//		    	boolean renameSuccessful = image.renameTo(new File(photoDirectoryFullPath + "/PHOTO.jpg"));
		    	
		    	if (bundle.getBoolean(INSIDE_WORD_DEFINITION, false))
		    		mImageCaptureButton.setText(R.string.retakePhoto);
		    	else
		    		mImageCaptureButton.setText(R.string.takeAnotherPhoto);
		    	
		    	mSaveButton.setEnabled(true);
		    	
	    	}
	    	
	    	
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
//	    
//	    imageFileName = "temp";
	    
//	    if (mFileName != null) {
//	    	File tempFile = new File(mFileName);
//	    	tempFile.delete();
//	    }
	    
//	    File storageDir = Environment.getExternalStoragePublicDirectory(
//	            Environment.DIRECTORY_PICTURES);
	    File storageDir = new File(photoDirectoryFullPath);
	    		
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );
	    
//	    image = new File(photoDirectoryFullPath, imageFileName + ".jpg");
	    
	    mFileName = image.getAbsolutePath();
	    
//	    image = new File(mFileName);
//	    image.createNewFile();
	    
	    Log.i("PHOTO", "createImageFile file path=" + mFileName);
	    
	    
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

				
//				if (imageSaved)
////					startActivity(new Intent(getContext(), ImageCaptureFragment.class));
					
				
				dispatchTakePictureIntent();
							
				
				// user must enter file name before saving & there must be picture captured to save
				if (!mFileNameEditText.getText().toString().equals("")) {
//					mSaveButton.setEnabled(true); 
            	}
            	else {
            		mSaveButton.setEnabled(false); 
            	}
			}
			
		};
		
		public ImageCaptureButton(Context context) {
			super(context);
			setOnClickListener(clicker);
			setText(R.string.takePhoto);
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
				
//				File image = new File(mFileName);
//	            File image = new File(mFileName);
				File newImage = new File(newFileName);
				try {
					newImage.createNewFile();
				
				Log.i("PHOTOS", "Old file path="+mFileName);
				Log.i("PHOTOS", "New file path="+newFileName);
				
				image.setReadable(true);
				image.setWritable(true);
				newImage.setReadable(true);
				newImage.setWritable(true);
				
				boolean renameSuccessful = image.renameTo(newImage);
				
				Log.i("PHOTOS", "image canRead="+image.canRead()+" canWrite="+image.canWrite());
				Log.i("PHOTOS", "newImage canRead="+newImage.canRead()+" canWrite="+newImage.canWrite());
				
				Log.i("PHOTOS", "Rename successful="+renameSuccessful);
				

				if (renameSuccessful)
					Toast.makeText(getActivity(), "Photo saved", Toast.LENGTH_SHORT).show();
				
				
				if (newImage.exists()) {
            		Log.i("SAVE AUDIO", "New file exists");
            		Log.i("SAVE AUDIO", "New file name=" + newImage.getAbsolutePath());
            	}
            	else {
            		Log.i("SAVE AUDIO", "New does not file exist");
            	}
            	
            	if (image.exists()) {
            		Log.i("SAVE AUDIO", "Default file exists");
//            		image.delete();
            	}
            	else {
            		Log.i("SAVE AUDIO", "Default does not file exist");
            	}
//				
				} catch (IOException e) {
					Log.e("SAVE PHOTO", "Could not create new photo file");
				}
				
					
				imageSaved = true;
				
				// Reset form so user can take another photo
				mImageCaptureButton.setEnabled(true);
				
				if (bundle != null)
		        	mFileNameEditText.setText(bundle.getString(FILE_NAME, ""));

				mFileNameEditText.setEnabled(true);
				
				
			}
			
		};
		
		public SaveButton(Context context) {
			super(context);
			setOnClickListener(clicker);
			setText(R.string.save);
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
	
	public class ImageAdapter extends BaseAdapter {

		private Context context;
		
		public ImageAdapter(Context c) {
			context = c;
//			TypedArray a = obtainStyledAttributes(R.styleable.MyGallery);
//			itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
//			
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
