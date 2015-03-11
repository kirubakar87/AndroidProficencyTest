package com.androidproficiency.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.ImageView;

import com.androidproficiency.AndroidProficiency;

public class ImageLoader {
    
    private MemoryCache memoryCache=new MemoryCache();
    
    private FileCache fileCache;
    
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    
    private ExecutorService executorService;

    /** Handler to display images in UI thread **/
    Handler handler=new Handler();
    
    /** ImageLoader Constructor**/
    public ImageLoader(Context context){
    	fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
    /** Sets DownloadedImage to view **/ 
	public void DisplayImage(String url, ImageView imageView){
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
            imageView.setImageDrawable(convertBitmap(bitmap));
        else{
        	queueImage(url, imageView);
        	Bitmap bitmaps = BitmapFactory.decodeResource(AndroidProficiency.getAppContext().getResources(), android.R.color.darker_gray);
        	imageView.setImageDrawable(convertBitmap(bitmaps));
        }
    }
    
	/** Queueing image from URL to load in Imageview**/
    private void queueImage(String url, ImageView imageView){
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }
    
    /** Load from SD cache and web **/
    private Bitmap getBitmap(String url){
        File f=fileCache.getFile(url);
        
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }

    /** Decodes image and scales it to reduce memory consumption **/
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        	Logger.getLogger(e.getMessage());
        } 
        catch (IOException e) {
        	Logger.getLogger(e.getMessage());
        }
        return null;
    }
    
    /** Task for the queue **/
    private class PhotoToLoad{
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
    
    /** Loads Bitmap to MemoryCache **/ 
    private class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    /** Checks wether imageview is reused or not **/
    private boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    
    /** Used to display bitmap in the UI thread **/
    private class BitmapDisplayer implements Runnable{
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
		public void run(){
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
                photoToLoad.imageView.setImageDrawable(convertBitmap(bitmap));
            }else{
            	Bitmap bitmap = BitmapFactory.decodeResource(AndroidProficiency.getAppContext().getResources(), android.R.color.darker_gray);
            	photoToLoad.imageView.setImageDrawable(convertBitmap(bitmap));
            }
        }
    }

    /**Clears MemeoryCache**/
    public void clearCache() {
        memoryCache.clear();
    }

    
    /** Converts BitmapDrawable to Drawable**/
    public Drawable convertBitmap(Bitmap bitmap){
    	
    	Drawable drawable = new BitmapDrawable(AndroidProficiency.getAppContext().getResources(), bitmap); 
    	return drawable;

    }

}
