package com.spring.nuqta.organization.Controller;

import com.spring.nuqta.organization.Dto.AddOrgDto;
import com.spring.nuqta.organization.Dto.OrgDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Mapper.AddOrgMapper;
import com.spring.nuqta.organization.Mapper.OrgMapper;
import com.spring.nuqta.organization.Services.OrgServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Organization", description = "APIs for managing organizations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/org")
public class OrgController {

    private final OrgServices orgServices;
    private final OrgMapper orgMapper;
    private final AddOrgMapper addOrgMapper;

    @Operation(summary = "Get All Organizations", description = "Retrieve a list of all organizations")
    @ApiResponse(responseCode = "200", description = "Organization get successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrgDto.class)))
    @GetMapping("")
    public ResponseEntity<List<OrgDto>> getAllOrg() {
        List<OrgDto> dtos = orgMapper.map(orgServices.findAll());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Operation(summary = "Get Organization by ID", description = "Retrieve details of a specific organization by its ID")
    @ApiResponse(responseCode = "200", description = "Organization get successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrgDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<OrgDto> getOrgById(@PathVariable Long id) {
        OrgDto dto = orgMapper.map(orgServices.findById(id));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @Operation(summary = "Sign in Organization", description = "Create a new organization")
    @ApiResponse(responseCode = "201", description = "Organization added successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AddOrgDto.class)))
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AddOrgDto addOrgDto) {
        OrgEntity entity = addOrgMapper.unMap(addOrgDto);
        orgServices.saveOrg(entity);

        // Create a map for the JSON response
        Map<String, String> response = new HashMap<>();
        response.put("message", "Organization registered successfully. Please verify your email.");

        // Return the response as JSON
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Update an Organization", description = "Update an existing organization's details")
    @ApiResponse(responseCode = "200", description = "Organization update successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AddOrgDto.class)))
    @PutMapping("")
    public ResponseEntity<OrgDto> updateOrg(@RequestBody AddOrgDto addOrgDto) {
        OrgEntity entity = addOrgMapper.unMap(addOrgDto);
        entity = orgServices.update(entity);
        OrgDto dto = orgMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Delete Organization by ID", description = "Delete an organization by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrgById(@PathVariable Long id) {
        orgServices.deleteById(id);
        return new ResponseEntity<>("Success Delete Organization", HttpStatus.OK);
    }
}
