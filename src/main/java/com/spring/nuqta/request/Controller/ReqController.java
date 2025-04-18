package com.spring.nuqta.request.Controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.request.Dto.AddReqDto;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.request.Mapper.AddReqMapper;
import com.spring.nuqta.request.Mapper.ReqMapper;
import com.spring.nuqta.request.Services.ReqServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "Requests", description = "APIs for managing requests")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/request")
public class ReqController {

    private final ReqServices reqServices;
    private final ReqMapper reqMapper;
    private final AddReqMapper addReqMapper;
    private final MessageSource ms;

    @Operation(summary = "Get All Requests", description = "Retrieve a list of all requests")
    @ApiResponse(responseCode = "200", description = "Requests get successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReqDto.class)))
    @GetMapping()
    public ResponseEntity<?> getAllReq() {
        List<ReqDto> dto = reqMapper.map(reqServices.findAll());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Get Request by ID", description = "Retrieve details of a specific request by its ID")
    @ApiResponse(responseCode = "200", description = "Request get successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReqDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<?> getReqById(@PathVariable Long id) {
        ReqDto reqDto = reqMapper.map(reqServices.findById(id));
        return new ResponseEntity<>(reqDto, HttpStatus.OK);
    }

    @Operation(summary = "Add New Request", description = "Create a new request for a user or organization")
    @ApiResponse(responseCode = "200", description = "Request created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AddReqDto.class)))
    @PostMapping("")
    public ResponseEntity<?> addRequest(@RequestBody AddReqDto addReqDto) throws FirebaseMessagingException {

        if (addReqDto.getUserId() == null && addReqDto.getOrgId() == null) {
            throw new GlobalException("error.request.notfoundID", HttpStatus.BAD_REQUEST);
        }

        ReqEntity entity = addReqMapper.unMap(addReqDto);

        if (addReqDto.getOrgId() != null) {
            entity = reqServices.addRequest(entity, addReqDto.getOrgId(), true);
        } else {
            entity = reqServices.addRequest(entity, addReqDto.getUserId(), false);
        }

        AddReqDto dto = addReqMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an Existing Request", description = "Update the details of an existing request")
    @ApiResponse(responseCode = "200", description = "Request updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AddReqDto.class)))
    @PutMapping("")
    public ResponseEntity<?> updateReq(@RequestBody AddReqDto addReqDto) {
        ReqEntity entity = addReqMapper.unMap(addReqDto);
        entity = reqServices.update(entity);
        AddReqDto dto = addReqMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Delete Request by ID", description = "Delete a request by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReqById(@PathVariable Long id) {
        reqServices.ReCache(id);
        reqServices.deleteById(id);
        Map<String, String> response = new HashMap<>();
        String message = ms.getMessage("error.request.delete", null, LocaleContextHolder.getLocale());
        response.put("message", message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
