package com.spring.nuqta.organization.Controller;

import com.spring.nuqta.organization.Dto.AddOrgDto;
import com.spring.nuqta.organization.Dto.OrgDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Mapper.AddOrgMapper;
import com.spring.nuqta.organization.Mapper.OrgMapper;
import com.spring.nuqta.organization.Services.OrgServices;
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

@Tag(name = "Organization", description = "APIs for managing organizations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/org")
public class OrgController {

    private final OrgServices orgServices;
    private final OrgMapper orgMapper;
    private final AddOrgMapper addOrgMapper;

    @Operation(summary = "Get All Organizations", description = "Retrieve a list of all organizations")
    @GetMapping()
    public ResponseEntity<?> getAllOrg() {
        List<OrgDto> dto = orgMapper.map(orgServices.findAll());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Get Organization by ID", description = "Retrieve details of a specific organization by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrgById(@PathVariable Long id) {
        OrgDto dto = orgMapper.map(orgServices.findById(id));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Add New Organization", description = "Create a new organization")
    @PostMapping()
    public ResponseEntity<?> addOrg(@RequestBody AddOrgDto addOrgDto) {
        OrgEntity entity = addOrgMapper.unMap(addOrgDto);
        orgServices.insert(entity);
        AddOrgDto dto = addOrgMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an Organization", description = "Update an existing organization's details")
    @PutMapping()
    public ResponseEntity<?> updateOrg(@RequestBody AddOrgDto addOrgDto) {
        OrgEntity entity = addOrgMapper.unMap(addOrgDto);
        orgServices.update(entity);
        AddOrgDto dto = addOrgMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Delete Organization by ID", description = "Delete an organization by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrgById(@PathVariable Long id) {
        orgServices.deleteById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
