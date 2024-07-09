package com.example.demo.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import com.example.demo.repository.HotelRepository;

import io.micrometer.common.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class HotelController {
	private static Logger log = LoggerFactory.getLogger(HotelController.class);
//    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";
    
	public static String uploadDirectory = "D:\\react\\reactapp\\smartcity\\src\\himage";
    

	@Autowired
	
	private HotelRepository eRepo;
	
	@GetMapping("/hotels")
	public List<Hotel> getAllHotels() {
		return eRepo.findAll();
	}
	@GetMapping("/hotels/{id}")
	public Hotel getHotelById(@PathVariable Long id)
	{
		return eRepo.findById(id).get();
	}

	

//	 @PostMapping("/hotels")
//	    public @ResponseBody ResponseEntity<?> createEmployee(@Validated Hotel hotel,
//	            @RequestParam("name")  String name, @RequestParam("designation")  String designation,
//	            final @RequestParam("file") MultipartFile file) {
	
	@PostMapping("/hotels")
    public @ResponseBody ResponseEntity<?> createEmployee( @RequestParam("name")  String name, @RequestParam("address")  String address,
    		final @RequestParam("file") MultipartFile file, @RequestParam("price")  Long price) {
	        try {
	        	
	        	System.out.println("SUCCESS");
	        	
//	        	 String name = hotel.getName();
//	             String designation = hotel.getDesignation();
//	             MultipartFile file = hotel.getFile();
	             System.out.println(name);
	             System.out.println(address);
	             System.out.println(file);
	             System.out.println(price);
	             Hotel hotel = new Hotel();
	            HttpHeaders headers = new HttpHeaders();
	            
	            
	            
	            if (hotel == null) {
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

	            hotel.setName(name);
	            hotel.setAddress(address);
	            hotel.setPrice(price);
	            hotel.setFileName(fileName);
	            hotel.setFilePath(filePath);
	            hotel.setFileType(fileType);
	            hotel.setFileSize(fileSize);
	            hotel.setCreatedDate(currentTimestamp);

	            eRepo.save(hotel);

	            log.info("Hotel Created");

	            headers.add("Hotel Saved With Image - ", fileName);
	            return new ResponseEntity<>("Hotel Saved With File - " + fileName, headers, HttpStatus.OK);

	        } catch (Exception e) {
	            e.printStackTrace();
	            log.info("Exception: " + e);
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	        }
	 }	
	 @DeleteMapping("/hotels/{id}")
	 public ResponseEntity<?> deleteHotelById(@PathVariable Long id) {
	     try {
	         // Check if hotel with the given ID exists
	         Optional<Hotel> optionalHotel = eRepo.findById(id);
	         if (!optionalHotel.isPresent()) {
	             return new ResponseEntity<>("Hotel not found with ID: " + id, HttpStatus.NOT_FOUND);
	         }

	         Hotel hotel = optionalHotel.get();
	         
	         // Get the file path of the hotel's image
	         String filePath = hotel.getFilePath();
	         if (StringUtils.isEmpty(filePath)) {
	             return new ResponseEntity<>("File path is empty for hotel with ID: " + id, HttpStatus.INTERNAL_SERVER_ERROR);
	         }

	         File imageFile = new File(filePath);
	         if (imageFile.exists()) {
	             // Attempt to delete the image file
	             if (imageFile.delete()) {
	                 log.info("Image file deleted for hotel with ID " + id);
	             } else {
	                 log.error("Failed to delete image file for hotel with ID " + id);
	                 return new ResponseEntity<>("Failed to delete image file for hotel with ID " + id, HttpStatus.INTERNAL_SERVER_ERROR);
	             }
	         } else {
	             log.info("Image file not found for hotel with ID " + id);
	         }

	         // Delete the hotel entity
	         eRepo.delete(hotel);

	         log.info("Hotel with ID " + id + " deleted successfully");

	         return new ResponseEntity<>("Hotel with ID " + id + " deleted successfully", HttpStatus.OK);
	     } catch (Exception e) {
	         log.error("Error occurred while deleting hotel with ID " + id + ": " + e.getMessage());
	         return new ResponseEntity<>("Failed to delete hotel with ID " + id, HttpStatus.INTERNAL_SERVER_ERROR);
	     }
	 }
	 @PutMapping("/hotels/{id}")
	 public ResponseEntity<?> updateHotelById(@PathVariable Long id,
	                                           @RequestParam("name") String name,
	                                           @RequestParam("address") String address,
	                                           @RequestParam(value = "file", required = false) MultipartFile file,
	                                           @RequestParam("price") Long price) {
	     try {
	         // Check if hotel with the given ID exists
	         Optional<Hotel> optionalHotel = eRepo.findById(id);
	         if (!optionalHotel.isPresent()) {
	             return new ResponseEntity<>("Hotel not found with ID: " + id, HttpStatus.NOT_FOUND);
	         }

	         Hotel hotel = optionalHotel.get();

	         // Update hotel information based on the provided data
	         hotel.setName(name);
	         hotel.setAddress(address);
	         hotel.setPrice(price);
	         // Update other fields as needed...

	         // Handle file upload only if a file is provided in the request
	         if (file != null && !file.isEmpty()) {
	             // Delete the old image file
	             if (hotel.getFilePath() != null) {
	                 File oldImageFile = new File(hotel.getFilePath());
	                 if (oldImageFile.exists()) {
	                     if (!oldImageFile.delete()) {
	                         log.error("Failed to delete old image file for hotel with ID " + id);
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

	             // Update hotel file information
	             hotel.setFileName(fileName);
	             hotel.setFilePath(filePath);
	             hotel.setFileType(fileType);
	             hotel.setFileSize(fileSize);
	         }

	         // Save the updated hotel entity
	         eRepo.save(hotel);

	         log.info("Hotel with ID " + id + " updated successfully");

	         return new ResponseEntity<>("Hotel with ID " + id + " updated successfully", HttpStatus.OK);
	     } catch (Exception e) {
	         log.error("Error occurred while updating hotel with ID " + id + ": " + e.getMessage());
	         return new ResponseEntity<>("Failed to update hotel with ID " + id, HttpStatus.INTERNAL_SERVER_ERROR);
	     }
	 }


}
