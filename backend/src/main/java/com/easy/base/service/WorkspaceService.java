package com.easy.base.service;

import com.easy.base.model.Workspace;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface WorkspaceService {

    public Workspace createWorkspace(Workspace workspace);

    public Page<Workspace> getWorkspacesByUserId(String userId, Pageable pageable);

    public Workspace getWorkspaceById(String id);

    public Workspace updateWorkspace(Workspace workspace);

    public void deleteWorkspace(String id);

    public Workspace updateCollaborators(String id, List<ObjectId> collaboratorIds);
}
