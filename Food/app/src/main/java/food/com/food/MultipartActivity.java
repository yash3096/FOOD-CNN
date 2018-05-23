package food.com.food;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import food.com.food.MultiPartUtility;
public class MultipartActivity extends AppCompatActivity {

    public static final int PICK_IMAGE_REQUEST=2, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private Uri mImageUri;
    File f;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multipart);
        progressBar= (ProgressBar) findViewById(R.id.progressbar);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

   /* public void onGallery(View view)
    {
        Intent intent = new Intent();
        System.out.println("ON gallery click called");
// Show only images, no videos or anything else
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
*/
    public void onImage(View view) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photo;
        try {
            File filesDir= this.getFilesDir();
            // place where to store camera taken picture temporarily
            photo = createTemporaryFile("capture", ".jpg");
            System.out.println("HI");
            //photo.delete();
        } catch (Exception e) {
            Log.v("Djsce Image capture", "Can't create file to take picture!");
            Toast.makeText(getApplicationContext(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG).show();
            return;
        }
        mImageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, 100);
    }
    private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir = Environment.getExternalStorageDirectory();


        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)){
            Log.d("myAppName", "Error: external storage is unavailable");
        }
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d("myAppName", "Error: external storage is read only.");
        }
        else
        Log.d("myAppName", "External storage is not read only or unavailable");

        if (ContextCompat.checkSelfPermission(this, // request permission when it is not granted.
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("myAppName", "permission:WRITE_EXTERNAL_STORAGE: NOT granted!");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                System.out.println("Show an expanation to the user *");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                System.out.println("No explanation needed, we can request the permission");
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        System.out.println("checking for directory: "+tempDir.exists());
        tempDir = new File(tempDir.getAbsolutePath() + "/FOOD/");
        if (!tempDir.exists()) {
            System.out.println("Making diretory");
            tempDir.mkdir();
        }
        System.out.println(tempDir.toString());
        return File.createTempFile(part, ext, tempDir);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100
                && resultCode == RESULT_OK) {

            try {


                f = grabImageFile(true, 80); //true for compression , 80% quality, To get the File object of the image
                if (f != null) {
                    Toast.makeText(getApplicationContext(), "File to upload is " + f.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    //call image uplaod code here
                    //doFileUpload(f, Constants.IMAGE,f.getName());

                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected void onPreExecute() {
                            progressBar.setVisibility(View.VISIBLE);
                            super.onPreExecute();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                doFileUpload(f, 2, f.getName());//uploads the file

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        protected void onPostExecute(Void result) {
                            Toast.makeText(getApplicationContext(),
                                    "image uploaded", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }.execute();


                } else {
                    Toast.makeText(getApplicationContext(), "image mila hi nahi bro!!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }

        }

    else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageURI = data.getData();

            f = new File(getRealPathFromURI(selectedImageURI));

            System.out.println("file created");
            if (f != null) {
                Toast.makeText(getApplicationContext(), "File to upload is " + f.getAbsolutePath(), Toast.LENGTH_LONG).show();
                //call image uplaod code here
                //doFileUpload(f, Constants.IMAGE,f.getName());

                new AsyncTask<Void, Void, Void>() {


                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            System.out.println("trying to upload file from gallery");
                            doFileUpload(f, 2, f.getName());//uploads the file

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),
                                "image uploaded", Toast.LENGTH_LONG).show();
                    }
                }.execute();


            } else {
                Toast.makeText(getApplicationContext(), "image mila hi nahi bro!!", Toast.LENGTH_LONG).show();
            }

        }

    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        System.out.println("hi1");
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        System.out.println("hi2");
        if (cursor == null) { // Source is Dropbox or other similar local file path
            System.out.println("cursor null");
            result = contentURI.getPath();
        } else {
            System.out.println("cursor not null");
            cursor.moveToFirst();
            System.out.println("hi1");
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            System.out.println("hi2");
            result = cursor.getString(idx);
            System.out.println("final result path "+ result);
            cursor.close();
        }
        return result;
    }
        public void doFileUpload(File f, int type, String data)
                throws JSONException, IOException {
            Log.e("CHAT VIEW", "Started");
            System.out.println("Entered douploadfile");
            String charset = "UTF-8";
            String requestURL = "https://17839076.ngrok.io/predict";

            MultiPartUtility multipart;
            try {
                multipart = new MultiPartUtility(requestURL, charset);

                multipart.addFilePart("source", f);
                List<String> response = multipart.finish(); // response from server.
                for (String s : response) Log.e("Response is", s);
                String s = response.get(0);
                System.out.println("response: " + s);
                String foods[] = s.split("  ");
                String send[] = new String[foods.length*2];

                for (int i = 0,k=0; i < foods.length; i++) {
                    System.out.println(foods[i]);
                    switch (foods[i]) {

                        case "pizza":
                            send[k] = "Pizza";
                            k++;
                            send[k] = "Dominos%20Pizza";
                            k++;
                            break;
                        case "french fries":
                            send[k] = "french%20fries";
                            k++;
                            send[k] = "fries";
                            k++;
                            break;
                        case "fried rice":
                            send[k] = "fried%20rice";
                            k++;
                            send[k] = "schezwan%20fried%20rice";
                            k++;
                            break;
                        case "donuts":
                            send[k] = "donut";
                            k++;
                            send[k] = "chocolate%20 donut";
                            k++;
                            break;
                        case "samosa":
                            send[k] = "vegetable%20samosa";
                            k++;
                            send[k] = "samosa";
                            k++;

                    }
                    System.out.println("sending food "+(i+1)+" as "+send[i]);

                }
                Intent intent = new Intent(getBaseContext(), FoodList.class);
                intent.putExtra("food_classified", send);
                startActivity(intent);

                //Toast.makeText(getApplicationContext(),"response is "+s , Toast.LENGTH_LONG).show();
                String urlstring="https://api.nal.usda.gov/ndb/search/?format=json&q="+s+"&sort=n&max=25&offset=0&api_key=9iytT5nrdPD3gTRLZRUIyRlYonP7h7UC4s6ci1rt";

                //System.out.println("json object returned: "+object.toString());
                //Toast.makeText(getApplicationContext(),"json response is : "+ jsonObject.toString(), Toast.LENGTH_LONG).show();
               /* String url = (new JSONObject(s)).getJSONArray("files").getJSONObject(0).getString("url");
                //you will have to send the url back from the server so that you can store it as further use in the app
                Integer status = (new JSONObject(s)).getInt("request_status");


                Log.e("url is", url);*/

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("SORRY BRO ERROR AAYA ");
                //Toast.makeText(getApplicationContext(),"some error occurred" , Toast.LENGTH_LONG).show();


            }

        }




        public File grabImageFile(boolean compress,int quality) {
            File returnFile= null;
            try {
                returnFile = new File(mImageUri.getPath());
                if(returnFile.exists() && compress){
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(returnFile.getAbsolutePath(), bmOptions);
                    File compressedFile = createTemporaryFile("capture_compressed", ".jpg");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(compressedFile);
                    System.out.println("image file successfully created");
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    returnFile.delete();
                    returnFile = compressedFile;
                }
//
            } catch (Exception e){
                Log.e("Image123 Capture Error",e.toString());
            }
            return returnFile;
        }
    }
