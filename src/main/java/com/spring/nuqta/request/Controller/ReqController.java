package com.spring.nuqta.request.Controller;

import com.spring.nuqta.request.Dto.AddReqDto;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.request.Mapper.AddReqMapper;
import com.spring.nuqta.request.Mapper.ReqMapper;
import com.spring.nuqta.request.Services.ReqServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Requests", description = "APIs for managing requests")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/request")
public class ReqController {

    private final ReqServices reqServices;
    private final ReqMapper reqMapper;
    private final AddReqMapper addReqMapper;

    @Operation(summary = "Get All Requests", description = "Retrieve a list of all requests")
    @GetMapping()
    public ResponseEntity<?> getAllReq() {
        List<ReqDto> dto = reqMapper.map(reqServices.findAll());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Get Request by ID", description = "Retrieve details of a specific request by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getReqById(@PathVariable Long id) {
        ReqDto reqDto = reqMapper.map(reqServices.findById(id));
        return new ResponseEntity<>(reqDto, HttpStatus.OK);
    }


    @Operation(summary = "Add New Request", description = "Create a new request")
    @PostMapping("/{userId}")
    public ResponseEntity<?> addReq(@PathVariable Long userId, @RequestBody AddReqDto addReqDto) {
        ReqEntity entity = addReqMapper.unMap(addReqDto);
        reqServices.addRequest(userId, entity);
        return new ResponseEntity<>(addReqDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an Existing Request", description = "Update the details of an existing request")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateReq(@PathVariable Long userId, @RequestBody AddReqDto addReqDto) {
        ReqEntity entity = addReqMapper.unMap(addReqDto);
        reqServices.addRequest(userId, entity);
        AddReqDto dto = addReqMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @Operation(summary = "Add New Request", description = "Create a new request")
    @PostMapping("org/{orgId}")
    public ResponseEntity<?> addReqForOrg(@PathVariable Long orgId, @RequestBody AddReqDto addReqDto) {
        ReqEntity entity = addReqMapper.unMap(addReqDto);
        reqServices.addRequestForOrg(orgId, entity);
        return new ResponseEntity<>(addReqDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an Existing Request", description = "Update the details of an existing request")
    @PutMapping("org/{orgId}")
    public ResponseEntity<?> updateReqForOrg(@PathVariable Long orgId, @RequestBody AddReqDto addReqDto) {
        ReqEntity entity = addReqMapper.unMap(addReqDto);
        reqServices.addRequestForOrg(orgId, entity);
        AddReqDto dto = addReqMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @Operation(summary = "Delete Request by ID", description = "Delete a request by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReqById(@PathVariable Long id) {
        reqServices.deleteById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
