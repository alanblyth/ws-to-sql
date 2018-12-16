package com.intellimax.fileingressws;

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


@RestController
public class FileController {
	
    static Logger log = Logger.getLogger(FileController.class);
    static String fileStore = "tempFileStore";
    
    @PostMapping("/deviceFile" )
    public ResponseEntity greeting(@RequestParam(value="deviceSerial") String deviceSerial,
    		@RequestParam(value="deviceModel") String deviceModel,
    		@RequestParam(value="fileType") String fileType,
    		@RequestParam(value="fileName") String fileName,
    		@RequestParam(value="fileId") String fileId,
    		InputStream dataStream) {
    	
    	try {
    	 
    	    File targetFile = new File(fileStore + "/" + deviceModel + "/" + deviceSerial + "/" + fileType + "/" + fileId + "/" + fileName);
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
