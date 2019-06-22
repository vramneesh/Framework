package io.github.luzzu.communications.resources.v4;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import io.github.luzzu.operations.properties.PropertyManager;

import io.github.luzzu.communications.utils.APIExceptionJSONBuilder;
import io.github.luzzu.communications.utils.APIResponse;
import io.github.luzzu.operations.lowlevel.ExceptionOutput;

@Path("/v4/")
public class ImportDataset {
	
	final static Logger logger = LoggerFactory.getLogger(ImportDataset.class);
	private String JSON = MediaType.APPLICATION_JSON;
	
	@Path("import/")
	  @OPTIONS
	  public Response dataset(){
	      Response response = Response.ok("{}", this.JSON)
	    		  .header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
				       .header("Access-Control-Allow-Headers", "Content-Type, X-Auth-Token, Origin, Authorization").build();
	      return response;
	  }


	@POST
	@Path("import/")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response dataset(@FormDataParam("file") InputStream uploadedInputStream,
		    @FormDataParam("file") FormDataContentDisposition fileDetails){			
		
		long timeStamp = System.currentTimeMillis()/1000;
		String fileName = fileDetails.getFileName();
		String extension = "";
		int lastIndexPos = fileName.lastIndexOf(".");
		if (lastIndexPos > 0) {
			extension = fileName.substring(lastIndexPos+1);
			fileName = fileName.substring(0, lastIndexPos)+"_"+Long.toString(timeStamp);
			
		}
		String importDatasetDir = System.getProperty("user.dir") + "/dataset";
		importDatasetDir=importDatasetDir.replace('\\', '/');
		File importedFile = new File(importDatasetDir+"/");
		importedFile.mkdirs();
		logger.info("Importing Dataset : "+ importDatasetDir+"/"+fileName+"."+extension);
		//logger.info("Importing Dataset : "+ importDatasetDir+"/"+fileDetails.getFileName());
		
		
		String jsonResponse = "";
		
		try {
			writeToFile(uploadedInputStream, importDatasetDir+"/"+fileName+"."+extension);
			//writeToFile(uploadedInputStream, importDatasetDir+"/"+fileDetails.getFileName());
			jsonResponse = "{\"message\":\"File Upload Successful.\", \"fileName\":\""+fileName+"."+extension+"\", \"filePath\":\""+importDatasetDir+"/\"}";
			//jsonResponse = "{\"message\":\"File Upload Successful.\", \"fileName\":\""+fileDetails.getFileName()+"\", \"filePath\":\""+importDatasetDir+"/\"}";
		} catch (IOException e) {
			ExceptionOutput.output(e, "Dataset Import FAiled",  logger);
			jsonResponse = "{\"message\":\"File Upload Failed.\"}";
			return APIResponse.ok(jsonResponse.toString(), this.JSON);
		}
		
		return APIResponse.ok(jsonResponse.toString(), this.JSON);
	}
	
	 private void writeToFile(InputStream uploadedInputStream,
	            String uploadedFileLocation) throws IOException {
	     
  
             OutputStream out = null;
             int read = 0;
             byte[] bytes = new byte[1024];
  
             out = new FileOutputStream(new File(uploadedFileLocation));
             while ((read = uploadedInputStream.read(bytes)) != -1) {
                 out.write(bytes, 0, read);
             }
             out.flush();
             out.close();
  
         
	 }
	

}
