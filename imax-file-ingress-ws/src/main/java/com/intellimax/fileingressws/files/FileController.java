package com.intellimax.fileingressws.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intellimax.fileingressws.Application;


@RestController
public class FileController {
	
    public static final String HEADER_FILE_ID = "fileId";
    public static final String HEADER_FILE_NAME = "fileName";
    public static final String HEADER_FILE_TYPE = "fileType";
	public static final String HEADER_DEVICE_MODEL = "deviceModel";
	public static final String HEADER_DEVICE_SERIAL = "deviceSerial";
	static Logger log = Logger.getLogger(FileController.class);
    
    
    @PostMapping("/deviceFile" )
    public ResponseEntity greeting(@RequestParam(value=HEADER_DEVICE_SERIAL) String deviceSerial,
    		@RequestParam(value=HEADER_DEVICE_MODEL) String deviceModel,
    		@RequestParam(value=HEADER_FILE_TYPE) String fileType,
    		@RequestParam(value=HEADER_FILE_NAME) String fileName,
    		@RequestParam(value=HEADER_FILE_ID) String fileId,
    		InputStream dataStream) {
    	
    	try {
    	 
    	    File targetFile = new File(Application.FILE_STORE + "/" + deviceModel + "/" + deviceSerial + "/" + fileType + "/" + fileId + "/" + fileName);
    	    targetFile.getParentFile().mkdirs();
    	    log.info("Writing file: " + targetFile.getAbsolutePath());
    	    OutputStream outStream = Files.newOutputStream(Paths.get(targetFile.getAbsolutePath()));
    	    Integer copied = IOUtils.copy(dataStream, outStream);
    	    log.info("Wrote " + copied + " bytes");

    		return new ResponseEntity<>(HttpStatus.CREATED);
    	}
    	catch (Exception e) {
    		
    		log.error("General Exception", e);
    		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}
