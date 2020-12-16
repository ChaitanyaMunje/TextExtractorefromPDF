package com.gtappdevelopers.textextractorefrompdf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    TextView hello_txt;
    Button btn;
    private DownloadManager downloadManager;
    private long refid;
    private Uri Download_Uri;
    String path="/storage/emulated/0/Download/sample.pdf";


    Button btn1,btn2;
    static final String TAG = "SymmetricAlgorithmAES";
    String secr="k";
    String secr2="d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hello_txt=findViewById(R.id.text);
        btn=findViewById(R.id.get_btn);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String parsedText="";
                    PdfReader reader = new PdfReader(String.valueOf(path));
                    int n = reader.getNumberOfPages();
                    for (int i = 0; i <n ; i++) {
                        parsedText   = parsedText+ PdfTextExtractor.getTextFromPage(reader, i+1).trim()+"\n"; //Extracting the content from the different pages
                    }
                    System.out.println(parsedText);
                    Log.e("TAG","READED DATA = "+parsedText);

                    reader.close();
                } catch (Exception e) {
                    System.out.println(e);
                    Log.e("TAG","EXCEPTION = "+e);
                }
            }
        });




        //code to use my specified defined key
        byte[] key = new byte[0];
        try {
            key = (secr+secr2).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit

        SecretKeySpec sks = new SecretKeySpec(key, "AES");

        // Original text
        String theTestText = "hello";

        TextView tvorig = (TextView)findViewById(R.id.txtoriginal);
        tvorig.setText("\n[ORIGINAL]:\n" + theTestText + "\n");

        // Encode the original data with AES
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(theTestText.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
        }
        TextView tvencoded = (TextView)findViewById(R.id.txtencoded);
        tvencoded.setText("" +
                Base64.encodeToString(encodedBytes, Base64.DEFAULT) + "\n");

        // Decode the encoded data with AES
        byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, sks);
            decodedBytes = c.doFinal(encodedBytes);
        } catch (Exception e) {
            Log.e(TAG, "AES decryption error");
        }
        TextView tvdecoded = (TextView)findViewById(R.id.tvdecoded);
        tvdecoded.setText("[DECODED]:\n" + new String(decodedBytes) + "\n");

    }


    private void downloadpdffromurl(String url){
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Download");
        request.setDescription("Downloading file..");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+System.currentTimeMillis());

        DownloadManager manager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + "sample.pdf");  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);
        Log.e("TAG","file path = "+path);

    }




}