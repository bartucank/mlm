package com.metuncc.mlm.api.controller;

import com.metuncc.mlm.api.request.*;
import com.metuncc.mlm.api.response.*;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.api.service.ResponseService;
import com.metuncc.mlm.dto.*;
import com.metuncc.mlm.entity.enums.Role;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.service.MlmServices;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping(value ="/api/lecturer", produces = "application/json;charset=UTF-8")
public class MLMLecturerController {
    private MlmServices mlmServices;
    private MlmQueryServices mlmQueryServices;
    private ResponseService responseService;
    public MLMLecturerController(MlmServices mlmServices, MlmQueryServices mlmQueryServices, ResponseService responseService) {
        this.mlmServices = mlmServices;
        this.mlmQueryServices = mlmQueryServices;
        this.responseService = responseService;
    }



    @PostMapping("/course/create")
    public ResponseEntity<ApiResponse<StatusDTO>> createCourse(@RequestBody CreateCourseRequest request){
        return responseService.createResponse(mlmServices.createCourse(request));
    }
    @PutMapping("/course/invite")
    public ResponseEntity<ApiResponse<StatusDTO>> inviteStudent(@RequestBody InviteStudentRequest request){
        return responseService.createResponse(mlmServices.inviteStudent(request));
    }
    @PostMapping(value = "/course/bulkAddStudentToCourse",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StatusDTO>> bulkAddStudentToCourse(@RequestParam("file")MultipartFile file,
                                                                         @RequestParam("courseId") Long courseId){
        return responseService.createResponse(mlmServices.bulkAddStudentToCourse(file,courseId));
    }
    @PostMapping(value = "/course/uploadCourseMaterial",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StatusDTO>> uploadCourseMaterial(@RequestParam("file")MultipartFile file,
                                                                      @RequestParam("courseId") Long courseId,
                                                                      @RequestParam("name") String name) throws IOException {
        return responseService.createResponse(mlmServices.uploadCourseMaterial(file,courseId,name));
    }
    @DeleteMapping("/course/deleteCourseMaterial")
    public ResponseEntity<ApiResponse<StatusDTO>> deleteCourseMaterial(@RequestParam("materialId") Long materialId){
        return responseService.createResponse(mlmServices.deleteCourseMaterial(materialId));
    }
    @DeleteMapping("/course/removeStudentFromCourse")
    public ResponseEntity<ApiResponse<StatusDTO>> removeStudentFromCourse(@RequestParam("courseId") Long courseId,
                                                                         @RequestParam("courseStudentId") Long courseStudentId){
        return responseService.createResponse(mlmServices.removeStudentFromCourse(courseId,courseStudentId));
    }
    @PostMapping(value = "/course/bulkRemoveStudentFromCourse",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StatusDTO>> bulkRemoveStudentFromCourse(@RequestParam("file")MultipartFile file,
                                                                         @RequestParam("courseId") Long courseId){
        return responseService.createResponse(mlmServices.bulkRemoveStudentFromCourse(courseId,file));
    }
    @GetMapping("/course/getCourseByIdForLecturer")
    public ResponseEntity<ApiResponse<CourseDTO>> getCourseByIdForLecturer(@RequestParam("id") Long id){
        return responseService.createResponse(mlmQueryServices.getCourseByIdForLecturer(id));
    }
    @GetMapping("/course/getCoursesForLecturer")
    public ResponseEntity<ApiResponse<CourseDTOListResponse>> getCoursesForLecturer(){
        return responseService.createResponse(mlmQueryServices.getCoursesForLecturer());
    }
}
