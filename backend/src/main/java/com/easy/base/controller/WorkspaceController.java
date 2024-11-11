package com.easy.base.controller;

import com.easy.base.model.Workspace;
import com.easy.base.service.WorkspaceService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @Autowired
    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    public Workspace createWorkspace(@RequestBody Workspace workspace) {
        return workspaceService.createWorkspace(workspace);
    }

    @GetMapping("/user/{userId}")
    public List<Workspace> getWorkspacesByUserId(@PathVariable Long userId) {
        return workspaceService.getWorkspacesByUserId(userId);
    }

    @GetMapping("/{id}")
    public Workspace getWorkspaceById(@PathVariable String id) {
        return workspaceService.getWorkspaceById(id);
    }

    @PutMapping("/{id}")
    public Workspace updateWorkspace(@PathVariable ObjectId id, @RequestBody Workspace workspace) {
        workspace.setId(id);
        return workspaceService.updateWorkspace(workspace);
    }

    @DeleteMapping("/{id}")
    public void deleteWorkspace(@PathVariable Long id) {
        workspaceService.deleteWorkspace(id);
    }
}
