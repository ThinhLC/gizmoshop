package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.utils.FileUtils;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@RequestMapping("/api/public/gizmo")
public class AboutUsController {

    @GetMapping("/t/home")
    public ResponseEntity<byte[]> homeTeam(HttpServletResponse response) throws IOException {
        return FileUtils.getFileAsResponse("documents/html/INDEX_HOME_TEAM.html");
    }
    @GetMapping("/t/aboutUs")
    public ResponseEntity<byte[]> aboutTeam(HttpServletResponse response) throws IOException {
        return FileUtils.getFileAsResponse("documents/html/TEAM&GITHUB_PROJECT_GIZMOSHOP.html");
    }
    @GetMapping("/t/script-gizmo")
    public ResponseEntity<byte[]> ScriptTeam(HttpServletResponse response) throws IOException {
        return FileUtils.getFileAsResponse("documents/html/SCRIPT_TEAM.html");
    }
}
