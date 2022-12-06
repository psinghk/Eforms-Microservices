package in.nic.ashwini.eForms.controllers;

import java.io.FileNotFoundException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.nic.ashwini.eForms.services.CreateUserService;

@RestController
public class CommenController {

	@Autowired
	CreateUserService createUserService;

	@GetMapping("/download")
	public ResponseEntity<Object> getFile() throws FileNotFoundException {
		return createUserService.getFile();
	}

	

    public boolean isFilenameValid(String filename) {
        try {
            System.out.println("filename: " + filename);
            String fileNameWithOutExt = FilenameUtils.removeExtension(filename);
            System.out.println("fileNameWithOutExt: " + fileNameWithOutExt);
            if (!fileNameWithOutExt.contains(".")) {
                if ((filename.matches("^[A-Za-z0-9-&\\s\\_\\.]*")) && (filename.endsWith(".csv") || filename.endsWith(".CSV"))) {
                    System.out.println("returning true");
                    return true;
                } else {
                    System.out.println("returning false");
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
	
	@GetMapping("/check")
	public void check() {

		int arr[] = { 1, 2, 3, 4, 5, 6, 7, 8 };

		int arrr[] = new int[arr.length];
		// int len = arr.length-1;
		int[] arr1 = new int[arr.length];

		int arr2[] = new int[arr1.length];
		for (int i = arr.length - 1; i >= 0; i--) {
			// int e = arr[i];
			arr1[i] = arr[arr.length - i - 1];

			System.out.println("reverce Array " + arr[i]);
			System.out.println("reverce Array111 " + arr1[i]);

		}

//for(int i =0 ; i< arr2.length; i++){
// 	int c =arr1[i]+arr2[i];
// 	arr2[i] =  arr2[c];
// 	 System.out.println("final Array "+arr2[i]);
//}
	}
    
    
	
}
