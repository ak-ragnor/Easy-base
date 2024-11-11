package com.easy.base.service;

import com.easy.base.model.Workspace;
import org.springframework.stereotype.Service;

import java.util.List;

public interface WorkspaceService {

    public Workspace createWorkspace(Workspace workspace);

    public List<Workspace> getWorkspacesByUserId(Long userId);

    public Workspace getWorkspaceById(String id);

    public Workspace updateWorkspace(Workspace workspace);

    public void deleteWorkspace(Long id);
}
