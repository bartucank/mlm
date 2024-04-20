package com.metuncc.mlm.api.controller;

import com.metuncc.mlm.api.request.AddReviewRequest;
import com.metuncc.mlm.api.request.ChangePasswordRequest;
import com.metuncc.mlm.api.request.FindBookRequest;
import com.metuncc.mlm.api.response.*;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.api.service.ResponseService;
import com.metuncc.mlm.dto.*;
import com.metuncc.mlm.dto.ml.LightBook;
import com.metuncc.mlm.dto.ml.LightReview;
import com.metuncc.mlm.dto.ml.LightUser;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.service.MlmServices;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping(value ="/api/external", produces = "application/json;charset=UTF-8")
public class MLMExternalController {
    private MlmServices mlmServices;
    private MlmQueryServices mlmQueryServices;
    private ResponseService responseService;
    public MLMExternalController(MlmServices mlmServices, MlmQueryServices mlmQueryServices, ResponseService responseService) {
        this.mlmServices = mlmServices;
        this.mlmQueryServices = mlmQueryServices;
        this.responseService = responseService;
    }

    @GetMapping("/getLightBooks")
    public ResponseEntity<ApiResponse<List<LightBook>>> getLightBooks(){
        return responseService.createResponse(mlmQueryServices.getLightBooks());
    }

    @GetMapping("/getLightUsers")
    public ResponseEntity<ApiResponse<List<LightUser>>> getLightUsers(){
        return responseService.createResponse(mlmQueryServices.getLightUsers());
    }

    @GetMapping("/getLightReviews")
    public ResponseEntity<ApiResponse<List<LightReview>>> getLightReviews(){
        return responseService.createResponse(mlmQueryServices.getLightReviews());
    }

//    @PostMapping(value = "/rate")
//    public ResponseEntity<ApiResponse<StatusDTO>> rate(@RequestParam("userId") Long userId,
//                                                       @RequestParam("rate") Long rate,
//                                                       @RequestParam("isbn") String isbn){
//        return responseService.createResponse(mlmServices.parseRatings(userId,rate,isbn));
//    }

    @GetMapping(value = "/csv/reviews", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> getreviewcsv() {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(outputStream), CSVFormat.DEFAULT.withHeader("userid","isbn", "rate"));
            mlmQueryServices.writeReviewsToCSV(csvPrinter);
            csvPrinter.flush();
            csvPrinter.close();
            byte[] csvBytes = outputStream.toByteArray();
            return ResponseEntity.ok().body(csvBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping(value = "/csv/books", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> getbookcsv() {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(outputStream), CSVFormat.DEFAULT.withHeader("bookid","isbn", "category"));
            mlmQueryServices.writeBooksToCSV(csvPrinter);
            csvPrinter.flush();
            csvPrinter.close();
            byte[] csvBytes = outputStream.toByteArray();
            return ResponseEntity.ok().body(csvBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(value = "/csv/users", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> getuserscsv() {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(outputStream), CSVFormat.DEFAULT.withHeader("id","department"));
            mlmQueryServices.writeUsersToCSV(csvPrinter);
            csvPrinter.flush();
            csvPrinter.close();
            byte[] csvBytes = outputStream.toByteArray();
            return ResponseEntity.ok().body(csvBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
