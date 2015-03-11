package com.androidproficiency.utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;

public class MemoryCache {

	//Last argument true for LRU ordering
	private Map<String, Bitmap> cache=Collections.synchronizedMap(
			new LinkedHashMap<String, Bitmap>(10,1.5f,true));

	//current allocated size
	private long size=0;

	//max memory in bytes
	private long limit=1000000;

	public MemoryCache(){
		//use 25% of available heap size
		setLimit(Runtime.getRuntime().maxMemory()/4);
	}

	/** Sets memory Limit **/
	public void setLimit(long new_limit){
		limit=new_limit;
	}

	/** Gets Bitmap from cache with id provided **/
	public Bitmap get(String id){
		try{
			if(!cache.containsKey(id))
				return null;
			return cache.get(id);
		}catch(NullPointerException ex){
			return null;
		}
	}

	/** Puts Bitmap to cache with id provided **/
	public void put(String id, Bitmap bitmap){
		if(cache.containsKey(id))
			size-=getSizeInBytes(cache.get(id));
		cache.put(id, bitmap);
		size+=getSizeInBytes(bitmap);
		checkSize();
	}

	/** Check whether the Size reached maximum limit **/
	private void checkSize() {
		if(size>limit){
			Iterator<Entry<String, Bitmap>> iter=cache.entrySet().iterator();//least recently accessed item will be the first one iterated  
			while(iter.hasNext()){
				Entry<String, Bitmap> entry=iter.next();
				size-=getSizeInBytes(entry.getValue());
				iter.remove();
				if(size<=limit)
					break;
			}
		}
	}

	/** Clears cache and made size to 0 **/
	public void clear() {
		cache.clear();
		size=0;
	}

	/** Returns Size in Bytes **/
	long getSizeInBytes(Bitmap bitmap) {
		if(bitmap==null)
			return 0;
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
}