package com.frostwire.gnutella.gui.android;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CopyToDesktopTask extends Task {
	
	private Device _device;
	private File _path;
	private FileDescriptor[] _fileDescriptors;
	private int _currentFileIndex;
	
	public CopyToDesktopTask(Device device, File path, FileDescriptor[] fileDescriptors) {
		_device = device;
		_path = path;
		_fileDescriptors = fileDescriptors;
		_currentFileIndex = -1;
	}
	
	public Device getDevice() {
		return _device;
	}
	
	public File getPath() {
		return _path;
	}
	
	public int getCurrentFileIndex() {
		return _currentFileIndex;
	}
	
	public int getTotalFiles() {
	    return _fileDescriptors.length;
	}

	@Override
	public void run() {
		
		if (isCanceled()) {
			return;
		}
		
		try {
		    
    		setProgress(0);
    		
    		long totalBytes = getTotalBytes();
    		long totalWritten = 0;
    		
    		for (int i = 0; i < _fileDescriptors.length; i++) {
    			
    			if (isCanceled()) {
    				return;
    			}
    			
    			_currentFileIndex = i;
    			
    			FileDescriptor fileDescriptor = _fileDescriptors[i];
    
    			URL url = _device.getDownloadURL(fileDescriptor.fileType, fileDescriptor.id);
    			
    			InputStream is = null;
    			FileOutputStream fos = null;
    			
    			try {
    				is = url.openStream();
    				
    				File file = new File(_path, fileDescriptor.fileName);
    				
    				fos = new FileOutputStream(file);
    				
    				byte[] buffer = new byte[4 * 1024];
    				int n = 0;
    				
    				while ((n = is.read(buffer, 0, buffer.length)) != -1) {
    					
    					if (isCanceled()) {
    						return;
    					}
    					
    					fos.write(buffer, 0, n);
    					totalWritten += n;
    					setProgress((int) ((totalWritten * 100) / totalBytes));
    				}
    				
    			} catch (IOException e) {
    				fail(e);
    				break;
    			} finally {
    				close(fos);
    				close(is);
    			}
    		}
    		
    		setProgress(100);
    		AndroidMediator.instance().getDesktopExplorer().refresh();
		
		} catch (Exception e) {
            fail(e);
        }
	}
	
	private long getTotalBytes() {
	    long total = 0;
		for (FileDescriptor fileDescriptor : _fileDescriptors) {
			total += fileDescriptor.fileSize;
		}
		return total;
	}
	
	private void close(Closeable closable) {
		if (closable != null) {
			try {
				closable.close();
			} catch (IOException e) {
			}
		}
	}
}
