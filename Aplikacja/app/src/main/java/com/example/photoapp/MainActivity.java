package com.example.photoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    static String currentPhotoPath = "";
    static String adresSerwera = "192.168.31.47";

    private String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
    private String lineEnd = "\r\n";
    private String twoHyphens = "--";

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private Snackbar mySnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(policy);
        verifyStoragePermissions(this);

        if(!currentPhotoPath.isEmpty()){
            ImageView iv = findViewById(R.id.photoDisplay);
            iv.setImageURI(Uri.parse(currentPhotoPath));
            //setPic();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView iv = findViewById(R.id.photoDisplay);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //iv.setImageBitmap(imageBitmap);


            //iv.setImageURI(Uri.parse(currentPhotoPath));
            setPic();
            galleryAddPic();
        }
    }

    public void photoButtonOnClick(View view){
        dispatchTakePictureIntent();
    }

    public void sendButtonOnClick(View view){
        mySnackbar = Snackbar.make(view, "", 2000);
        postPhoto();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        //File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Zdjecia");
        //File storageDir = new File(this.getFilesDir().getAbsolutePath() + "/Zdjecia");
        System.out.println("starge dir " + storageDir.toString());
        if (!storageDir.exists()) {
            storageDir.mkdirs();
            System.out.println("STWORZONO");
        }

        if (storageDir.exists()) {
            System.out.println("ISTNIEJEO");
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        System.out.println("currentpath " + currentPhotoPath);
        System.out.println("image:  " + image.toString());
        //TextView tv = findViewById(R.id.tekst);
        //tv.setText(currentPhotoPath);
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println(ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.photoapp",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                //galleryAddPic();
                //Log.i("Tag","Wiadomosc");
            }
        }
    }

    //https://stackoverflow.com/questions/11766878/sending-files-using-post-with-httpurlconnection
    private void postPhoto(){

        HttpURLConnection connection = null;
        String fileName = "";

        try {
            URL url = new URL("http://" + adresSerwera + ":8080/test/put_image");
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(connection != null){
            Log.i("Connection: ","polaczono!");
        }

        try {

            DataOutputStream outputStream;
            int maxBufferSize = 128 * 1024;

            assert connection != null;
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            Log.i("Connection: ", String.valueOf(connection.getRequestMethod()));
            Log.i("Connection: ", String.valueOf(connection.getRequestProperties()));

            outputStream = new DataOutputStream(connection.getOutputStream());
            //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ File.separator+"IMG_20220325_130533.jpg";
            String path = currentPhotoPath;
            Log.i("File: ", path);
            File file = new File(path);
            if(file.exists()) {
                Log.i("File: ", "Istnieje");
            }else{
                Log.i("File: ", "Nie Istnieje");
            }

            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            Log.i("File size: ", String.valueOf(bytes.length));

            fileName = file.getName();
            Log.i("progress file ", String.valueOf(fileName));

            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"img\" ;filename=\"" + fileName + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
            outputStream.writeBytes("Content-Length: " + file.length() + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
            outputStream.writeBytes(lineEnd);


            int bytesAvailable = fileInputStream.available();
            int bytesAvailableStart = bytesAvailable;
            Log.i("bytesAvailable: ", String.valueOf(bytesAvailable));
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            Log.i("bufferSize: ", String.valueOf(bufferSize));
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            Log.i("bytesRead: ", String.valueOf(bytesRead));

            while (bytesRead > 0) {
                Log.i("progress ", bytesRead+" / "+bytesAvailableStart);
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            fileInputStream.close();

            //end output
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            outputStream.flush();
            outputStream.close();
            Log.i("Koniec", "Wysłano i zamknięto");

            //odpoweidz z serwera
            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i("odpowiedz:",stringBuilder.toString());


        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
            mySnackbar.setText("Wyslano zdjecie: " + fileName);
            mySnackbar.show();
        }

    }

    private void setPic() {
        ImageView imageView = findViewById(R.id.photoDisplay);

        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}