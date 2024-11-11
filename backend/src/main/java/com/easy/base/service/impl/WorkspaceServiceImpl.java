package com.easy.base.service.impl;

import com.easy.base.factory.builder.DefaultCollectionsBuilder;
import com.easy.base.model.Workspace;
import com.easy.base.repository.WorkspaceRepository;
import com.easy.base.service.FlexiTableService;
import com.easy.base.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private DefaultCollectionsBuilder defaultCollectionsBuilder;

    @Autowired
    private FlexiTableService flexiTableService;

    @Transactional(rollbackFor = Exception.class)
    public Workspace createWorkspace(Workspace workspace) {
        try {
            Workspace savedWorkspace = workspaceRepository.save(workspace);

            savedWorkspace.setFlexiTables(defaultCollectionsBuilder.
                    createDefaultCollections(savedWorkspace)
                    .stream()
                    .map(flexiTableService::createFlexiTable)
                    .toList());

            return workspaceRepository.save(savedWorkspace);
        } catch (Exception e) {
            // Log error and rethrow to trigger rollback
            throw new RuntimeException("Error during workspace creation", e);
        }
    }

    public List<Workspace> getWorkspacesByUserId(Long userId) {
        return workspaceRepository.findByOwnerId(userId);
    }

    public Workspace getWorkspaceById(String id) {
        return workspaceRepository.findById(id).orElse(null);
    }

    public Workspace updateWorkspace(Workspace workspace) {
        return workspaceRepository.save(workspace);
    }

    public void deleteWorkspace(Long id) {
        workspaceRepository.deleteById(String.valueOf(id));
    }
}
