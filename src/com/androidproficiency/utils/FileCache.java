package com.androidproficiency.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import android.content.Context;

public class FileCache {
    
    private File cacheDir;
    
    /** Find the dir to save cached images **/
    public FileCache(Context context){
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"AndroidProficiency");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

    /** Gets file from URL provided **/
    public File getFile(String url){
        String filename = null;
		try {
			filename = URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger(e.getMessage());
			return null;
		}
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    /** Clears filecache from directories**/
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}