package com.example.demo.Controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Hotel;
import com.example.demo.model.Places;
import com.example.demo.repository.PlacesRepository;

import io.micrometer.common.util.StringUtils;

@RestController
@RequestMapping("/api/")
@CrossOrigin("*")

public class PlacesController {
	private static Logger log = LoggerFactory.getLogger(HotelController.class);
    public static String uploadDirectory ="D:\\react\\reactapp\\smartcity\\src\\pimage";
	@Autowired
	private PlacesRepository eRepo;
	@GetMapping("/places")
	public List<Places> getAllPlaces()
	{
		return eRepo.findAll();
	}
	@GetMapping("/places/{id}")
	public Places getPlacesById(@PathVariable Long id)
	{
		return eRepo.findById(id).get();
	}
	@PostMapping("/places")
    public @ResponseBody ResponseEntity<?> createEmployee( @RequestParam("name")  String name,
    		final @RequestParam("file") MultipartFile file, @RequestParam("specification")  String specification,@RequestParam("address")  String address) {
	        try {
	        	
	        	System.out.println("SUCCESS");
	        	
//	        	 String name = hotel.getName();
//	             String designation = hotel.getDesignation();
//	             MultipartFile file = hotel.getFile();
	             System.out.println(name);
	             System.out.println(file);
	             System.out.println(specification);
	             System.out.println(address); 
	             Places places = new Places();
	            HttpHeaders headers = new HttpHeaders();
	            
	            
	            
	            if (places == null) {
	                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	            }
	            String fileName = file.getOriginalFilename();
	            String filePath = Paths.get(uploadDirectory, fileName).toString();
	            String fileType = file.getContentType();
	            long size = file.getSize();
	            String fileSize = String.valueOf(size);
	            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

	            log.info("Name: " + name);
	            log.info("FileName: " + file.getOriginalFilename());
	            log.info("FileType: " + file.getContentType());
	            log.info("FileSize: " + file.getSize());

	            // Save the file locally
	            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
	            stream.write(file.getBytes());
	            stream.close();

	            places.setName(name);
	            places.setSpecification(specification);
	            places.setAddress(address);
	            places.setFileName(fileName);
	            places.setFilePath(filePath);
	            places.setFileType(fileType);
	            places.setFileSize(fileSize);
	            places.setCreatedDate(currentTimestamp);

	            eRepo.save(places);

	            log.info("Hotel Created");

	            headers.add("places Saved With Image - ", fileName);
	            return new ResponseEntity<>("places Saved With File - " + fileName, headers, HttpStatus.OK);

	        } catch (Exception e) {
	            e.printStackTrace();
	            log.info("Exception: " + e);
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	        }
	 }
	 @PutMapping("/places/{id}")
	 public ResponseEntity<?> updateHotelById(@PathVariable Long id,
	                                           @RequestParam("name") String name,
	                                           @RequestParam("specification") String specification,
	                                           @RequestParam(value = "file", required = false) MultipartFile file,
	                                           @RequestParam("address") String address) {
	     try {
	         // Check if hotel with the given ID exists
	         Optional<Places> optionaPlaces = eRepo.findById(id);
	         if (!optionaPlaces.isPresent()) {
	             return new ResponseEntity<>("Hotel not found with ID: " + id, HttpStatus.NOT_FOUND);
	         }

	         Places places = optionaPlaces.get();
	         
	         // Update hotel information based on the provided data
	         places.setName(name);
	         places.setSpecification(specification);
	         places.setAddress(address);
	         // Update other fields as needed...

	         // Handle file upload only if a file is provided in the request
	         if (file != null && !file.isEmpty()) {
	             // Delete the old image file
	             if (places.getFilePath() != null) {
	                 File oldImageFile = new File(places.getFilePath());
	                 if (oldImageFile.exists()) {
	                     if (!oldImageFile.delete()) {
	                         log.error("Failed to delete old image file for Places with ID " + id);
	                         // Handle the error appropriately
	                     }
	                 }
	             }

	             // Save the new image file
	             String fileName = file.getOriginalFilename();
	             String filePath = Paths.get(uploadDirectory, fileName).toString();
	             String fileType = file.getContentType();
	             long size = file.getSize();
	             String fileSize = String.valueOf(size);

	             // Save the file locally
	             BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
	             stream.write(file.getBytes());
	             stream.close();

	             // Update Places file information
	             places.setFileName(fileName);
	             places.setFilePath(filePath);
	             places.setFileType(fileType);
	             places.setFileSize(fileSize);
	         }

	         // Save the updated hotel entity
	         eRepo.save(places);

	         log.info("Places with ID " + id + " updated successfully");

	         return new ResponseEntity<>("Places with ID " + id + " updated successfully", HttpStatus.OK);
	     } catch (Exception e) {
	         log.error("Error occurred while updating Places with ID " + id + ": " + e.getMessage());
	         return new ResponseEntity<>("Failed to update Places with ID " + id, HttpStatus.INTERNAL_SERVER_ERROR);
	     }
	 }
	 @DeleteMapping("/places/{id}")
	 public ResponseEntity<?> deleteHotelById(@PathVariable Long id) {
	     try {
	         // Check if hotel with the given ID exists
	         Optional<Places> optionalPlaces = eRepo.findById(id);
	         if (!optionalPlaces.isPresent()) {
	             return new ResponseEntity<>("places not found with ID: " + id, HttpStatus.NOT_FOUND);
	         }

	         Places places = optionalPlaces.get();
	         
	         // Get the file path of the hotel's image
	         String filePath = places.getFilePath();
	         if (StringUtils.isEmpty(filePath)) {
	             return new ResponseEntity<>("File path is empty for places with ID: " + id, HttpStatus.INTERNAL_SERVER_ERROR);
	         }

	         File imageFile = new File(filePath);
	         if (imageFile.exists()) {
	             // Attempt to delete the image file
	             if (imageFile.delete()) {
	                 log.info("Image file deleted for places with ID " + id);
	             } else {
	                 log.error("Failed to delete image file for places with ID " + id);
	                 return new ResponseEntity<>("Failed to delete image file for places with ID " + id, HttpStatus.INTERNAL_SERVER_ERROR);
	             }
	         } else {
	             log.info("Image file not found for places with ID " + id);
	         }

	         // Delete the hotel entity
	         eRepo.delete(places);

	         log.info("places with ID " + id + " deleted successfully");

	         return new ResponseEntity<>("places with ID " + id + " deleted successfully", HttpStatus.OK);
	     } catch (Exception e) {
	         log.error("Error occurred while deleting places with ID " + id + ": " + e.getMessage());
	         return new ResponseEntity<>("Failed to delete places with ID " + id, HttpStatus.INTERNAL_SERVER_ERROR);
	     }
	 }
}
