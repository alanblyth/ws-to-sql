/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellimax.fileingressws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.intellimax.fileingressws.files.FileController;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTests {

    @Autowired
    private MockMvc mockMvc;
    
    @After
    public void cleanup() {
    	
		deleteTempFileStore();
    }
    
    @Test
    public void uploadFile() throws Exception {
    	
    	String testFileName = "test.txt";
		PrintWriter writer = new PrintWriter(testFileName, "UTF-8");
    	writer.println("Hello World!");
    	writer.close();
	    File testFile = new File(testFileName);
	    byte[] fileContent = Files.readAllBytes(testFile.toPath());	  
	    
		String fileType = "type1";
		String deviceSerial = "serial1";
		String deviceModel = "model1";
		String fileId = "id1";
		mockMvc.perform(MockMvcRequestBuilders.post("/deviceFile")
				  .content(fileContent)
				  .accept(MediaType.TEXT_PLAIN)
				  .param(FileController.HEADER_FILE_NAME, testFileName)
				  .param(FileController.HEADER_FILE_TYPE, fileType)
				  .param(FileController.HEADER_DEVICE_SERIAL, deviceSerial)
				  .param(FileController.HEADER_DEVICE_MODEL, deviceModel)
				  .param(FileController.HEADER_FILE_ID, fileId)).andDo(print()).andExpect(status().isCreated());
		
	    File receivedFile = new File(Application.FILE_STORE + "/" + deviceModel + "/" + deviceSerial + "/" + fileType + "/" + fileId + "/" + testFileName);

	    assertTrue(sameContent(testFile.toPath(), receivedFile.toPath()));
		testFile.delete();
    }

    //TODO Missing Param Tests

	private void deleteTempFileStore() {
		File tempFileStore = new File(Application.FILE_STORE);
		System.out.println(Boolean.toString(deleteDir(tempFileStore)));
	}
	
	boolean sameContent(Path file1, Path file2) throws IOException {
	    final long size = Files.size(file1);
	    if (size != Files.size(file2))
	        return false;

	    if (size < 4096)
	        return Arrays.equals(Files.readAllBytes(file1), Files.readAllBytes(file2));

	    try (InputStream is1 = Files.newInputStream(file1);
	         InputStream is2 = Files.newInputStream(file2)) {
	        // Compare byte-by-byte.
	        // Note that this can be sped up drastically by reading large chunks
	        // (e.g. 16 KBs) but care must be taken as InputStream.read(byte[])
	        // does not neccessarily read a whole array!
	        int data;
	        while ((data = is1.read()) != -1)
	            if (data != is2.read())
	                return false;
	    }

	    return true;
	}
    


public static boolean deleteDir(File path) {
    if (path.exists()) {
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
            	deleteDir(files[i]);
            } else {
                files[i].delete();
            }
        }
    }
    return (path.delete());
}


}
