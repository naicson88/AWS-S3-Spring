package com.naicson.s3.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class StorageService {
	
	@Value("${application.bucket.name}")
	private String bucket;
	
	@Autowired
	private AmazonS3 s3Client;
	
	public String uploadFile(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		File fileObj = convertMultipartToFile(file);
		PutObjectResult uploaded = s3Client.putObject(new PutObjectRequest(bucket, fileName, fileObj ));
		
		fileObj.delete();
		
		return "File uploaded successfully! ID: " +  uploaded.getVersionId();
		
	}
	
	public byte[] downloadFile(String fileName) {
		S3Object s3Obj = s3Client.getObject(bucket, fileName);
		S3ObjectInputStream inputStream = s3Obj.getObjectContent();
		
		try {
			byte[] content = IOUtils.toByteArray(inputStream);
			return content;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String deleteFile(String fileName) {
		s3Client.deleteObject(bucket, fileName);
		return fileName + " removed!";
	}
	
	
	private File convertMultipartToFile(MultipartFile mf) {
		File convertedFile = new File(mf.getOriginalFilename());
		try {
			FileOutputStream fos = new FileOutputStream(convertedFile);
			fos.write(mf.getBytes());
			
		} catch (IOException e) {
			System.out.println("Error convert MultipartFile");
		}
		
		return convertedFile;
	}
	
}
