package com.monarchsolutions.sms.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.monarchsolutions.sms.service.SchoolService;
import com.monarchsolutions.sms.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FilesController {
  @Value("${app.upload-dir}")
  private String uploadDir;

  @Value("${app.schools_logos-dir}")
  private String schools_logosDir;
  
  @Value("${app.coffee-dir}")
  private String coffeeDir;
  
  @Value("${app.bulk_files-dir}")
  private String bulkFilesDir;

  @Autowired
  private SchoolService schoolService;

  @Autowired
  private JwtUtil jwtUtil;

  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
  @GetMapping("/api/protectedfiles/{filename:.+}")
  public ResponseEntity<Resource> serveProtectedFile(
      @PathVariable String filename,
      HttpServletRequest request
  ) {
    try {
      // 1) Resolve path safely
      Path filePath = Paths.get(uploadDir).resolve(filename).normalize();

      // 2) Load as Resource
      Resource resource = new UrlResource(filePath.toUri());
      if (!resource.exists() || !resource.isReadable()) {
        return ResponseEntity.notFound().build();
      }

      // 3) Determine content type
      String contentType = request.getServletContext()
          .getMimeType(resource.getFile().getAbsolutePath());
      if (contentType == null) {
        contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
      }

      // 4) Return as attachment
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CONTENT_DISPOSITION,
                  "attachment; filename=\"" + resource.getFilename() + "\"")
          .body(resource);

    } catch (MalformedURLException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (IOException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
  // @GetMapping("/api/school-logo/{filename:.+}")
  // public ResponseEntity<Resource> serveSchoolLogoFile(
  //     @RequestHeader("Authorization") String authHeader,
  //     @PathVariable String filename,
  //     HttpServletRequest request
  // ) {
  //   try {

  //     // 1) Resolve path safely
  //     Path filePath = Paths.get(schools_logosDir).resolve(filename).normalize();

  //     // 2) Load as Resource
  //     Resource resource = new UrlResource(filePath.toUri());
  //     if (!resource.exists() || !resource.isReadable()) {
  //       return ResponseEntity.notFound().build();
  //     }

  //     // 3) Determine content type
  //     String contentType = request.getServletContext()
  //         .getMimeType(resource.getFile().getAbsolutePath());
  //     if (contentType == null) {
  //       contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
  //     }

  //     // 4) Return as attachment
  //     return ResponseEntity.ok()
  //         .contentType(MediaType.parseMediaType(contentType))
  //         .header(HttpHeaders.CONTENT_DISPOSITION,
  //                 "attachment; filename=\"" + resource.getFilename() + "\"")
  //         .body(resource);

  //   } catch (MalformedURLException ex) {
  //     ex.printStackTrace();
  //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  //   } catch (IOException ex) {
  //     ex.printStackTrace();
  //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  //   }
  // }

  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
  @GetMapping("/api/school-logo/{school_id}")
  public ResponseEntity<Resource> serveSchoolLogoFile(
      @RequestHeader("Authorization") String authHeader,
      @PathVariable Long school_id,
      HttpServletRequest request
  ) {
    try {
      String token = authHeader.substring(7);
      Long token_user_id = jwtUtil.extractUserId(token);
      String school_image = schoolService.getSchoolImage(token_user_id, school_id);

      // if there's no image configured, just return 204 No Content
      if (school_image == null || school_image.trim().isEmpty()) {
        return ResponseEntity.noContent().build();
      }

      // 1) Resolve path safely
      Path filePath = Paths.get(schools_logosDir).resolve(school_image).normalize();

      // 2) Load as Resource
      Resource resource = new UrlResource(filePath.toUri());
      if (!resource.exists() || !resource.isReadable()) {
        return ResponseEntity.notFound().build();
      }

      // 3) Determine content type
      String contentType = request.getServletContext()
          .getMimeType(resource.getFile().getAbsolutePath());
      if (contentType == null) {
        contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
      }

      // 4) Return as attachment
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CONTENT_DISPOSITION,
                  "attachment; filename=\"" + resource.getFilename() + "\"")
          .body(resource);

    } catch (MalformedURLException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (IOException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
  @GetMapping("/api/coffee-menu-image/{filename:.+}")
  public ResponseEntity<Resource> serveCoffeeMenuImage(
      @PathVariable String filename,
      HttpServletRequest request
  ) {
    try {
      // 1) Resolve path safely
      Path filePath = Paths.get(coffeeDir).resolve(filename).normalize();

      // 2) Load as Resource
      Resource resource = new UrlResource(filePath.toUri());
      if (!resource.exists() || !resource.isReadable()) {
        return ResponseEntity.notFound().build();
      }

      // 3) Determine content type
      String contentType = request.getServletContext()
          .getMimeType(resource.getFile().getAbsolutePath());
      if (contentType == null) {
        contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
      }

      // 4) Return as attachment
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CONTENT_DISPOSITION,
                  "attachment; filename=\"" + resource.getFilename() + "\"")
          .body(resource);

    } catch (MalformedURLException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (IOException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
  @GetMapping("/api/bulkfile/{filename:.+}")
  public ResponseEntity<Resource> serveBulkFiles(
      @PathVariable String filename,
      HttpServletRequest request
  ) {
    try {
      // 1) Resolve path safely
      Path filePath = Paths.get(bulkFilesDir).resolve(filename).normalize();

      // 2) Load as Resource
      Resource resource = new UrlResource(filePath.toUri());
      if (!resource.exists() || !resource.isReadable()) {
        return ResponseEntity.notFound().build();
      }

      // 3) Determine content type
      String contentType = request.getServletContext()
          .getMimeType(resource.getFile().getAbsolutePath());
      if (contentType == null) {
        contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
      }

      // 4) Return as attachment
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CONTENT_DISPOSITION,
                  "attachment; filename=\"" + resource.getFilename() + "\"")
          .body(resource);

    } catch (MalformedURLException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (IOException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
