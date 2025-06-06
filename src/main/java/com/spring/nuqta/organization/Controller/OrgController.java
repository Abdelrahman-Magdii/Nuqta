package com.spring.nuqta.organization.Controller;

import com.spring.nuqta.organization.Dto.AddOrgDto;
import com.spring.nuqta.organization.Dto.OrgDto;
import com.spring.nuqta.organization.Dto.OrgRequestDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Mapper.AddOrgMapper;
import com.spring.nuqta.organization.Mapper.OrgMapper;
import com.spring.nuqta.organization.Mapper.OrgRequestMapper;
import com.spring.nuqta.organization.Services.OrgServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final OrgRequestMapper orgRequestMapper;
    private final MessageSource ms;

    @GetMapping("")
    public ResponseEntity<List<OrgDto>> getAllOrg() {
        List<OrgDto> dtos = orgMapper.map(orgServices.findAll());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrgDto> getOrgById(@PathVariable Long id) {
        OrgDto dto = orgMapper.map(orgServices.findById(id));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @PutMapping("")
    public ResponseEntity<OrgRequestDto> updateOrg(@RequestBody AddOrgDto addOrgDto) {
        OrgEntity entity = addOrgMapper.unMap(addOrgDto);
        entity = orgServices.update(entity);
        OrgRequestDto dto = orgRequestMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrgById(@PathVariable Long id) {
        orgServices.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", getMS("org.delete.success"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestParam Long orgId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        orgServices.changeOrgPassword(orgId, oldPassword, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("message", getMS("error.user.password.change"));

        return ResponseEntity.ok(response);
    }

    @PutMapping("fcmToken/{id}")
    public ResponseEntity<?> updateFcmToken(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String fcmToken = request.get("fcmToken");
        return orgServices.updateFcmToken(id, fcmToken);
    }

    private String getMS(String messageKey) {
        return ms.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}
