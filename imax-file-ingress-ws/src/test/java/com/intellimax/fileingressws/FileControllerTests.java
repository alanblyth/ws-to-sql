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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;

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
    	
    	PrintWriter writer = new PrintWriter("test.txt", "UTF-8");
    	writer.println("Hello World!");
    	writer.close();
	    File testFile = new File("test.txt");
	    byte[] fileContent = Files.readAllBytes(testFile.toPath());	  
	    
		mockMvc.perform(MockMvcRequestBuilders.post("/deviceFile")
				  .content(fileContent)
				  .accept(MediaType.TEXT_PLAIN)
				  .param("fileName", "test.txt") //TODO Move header names into static strings.
				  .param("fileType", "type1")
				  .param("deviceSerial", "serial1")
				  .param("deviceModel", "model1")
				  .param("fileId", "id1")).andDo(print()).andExpect(status().isCreated());
                //.andExpect(jsonPath("$.content").value("Hello, World!"));
		//TODO Check File Written correctly. Matches Source.
		testFile.delete();
    }

    //TODO Missing Param Tests

	private void deleteTempFileStore() {
		File tempFileStore = new File("tempFileStore");
		System.out.println(Boolean.toString(deleteDir(tempFileStore)));
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
