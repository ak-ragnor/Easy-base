package com.easy.base.service.impl;

import com.easy.base.exception.user.InvalidUserIdException;
import com.easy.base.exception.workspace.InvalidWorkspaceIdException;
import com.easy.base.exception.workspace.WorkspaceCreationException;
import com.easy.base.exception.workspace.WorkspaceNotFoundException;
import com.easy.base.exception.workspace.WorkspaceDeletionException;
import com.easy.base.exception.workspace.WorkspaceUpdateException;
import com.easy.base.flexi.builder.DefaultCollectionsBuilder;
import com.easy.base.model.FlexiTable;
import com.easy.base.model.Workspace;
import com.easy.base.repository.WorkspaceRepository;
import com.easy.base.service.FlexiTableService;
import com.easy.base.service.WorkspaceService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final DefaultCollectionsBuilder defaultCollectionsBuilder;
    private final FlexiTableService flexiTableService;

    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository,
                                DefaultCollectionsBuilder defaultCollectionsBuilder,
                                FlexiTableService flexiTableService) {
        this.workspaceRepository = workspaceRepository;
        this.defaultCollectionsBuilder = defaultCollectionsBuilder;
        this.flexiTableService = flexiTableService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Workspace createWorkspace(Workspace workspace) {
        try {
            _validateWorkspace(workspace);

            // Initial save to get the workspace ID
            Workspace savedWorkspace = workspaceRepository.save(workspace);
            log.debug("Initial workspace save completed with ID: {}", savedWorkspace.getId());

            // Create default collections
            List<FlexiTable> defaultTables = defaultCollectionsBuilder
                    .createDefaultCollections(savedWorkspace)
                    .stream()
                    .map(flexiTableService::createFlexiTable)
                    .toList();

            log.debug("Created {} default collections for workspace {}",
                    defaultTables.size(), savedWorkspace.getId());

            savedWorkspace.setFlexiTables(defaultTables);

            // Final save with default collections
            return workspaceRepository.save(savedWorkspace);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating workspace", e);
            throw new WorkspaceCreationException("Workspace creation failed due to data integrity violation", e);
        } catch (Exception e) {
            log.error("Unexpected error during workspace creation", e);
            throw new WorkspaceCreationException("Workspace creation failed", e);
        }
    }

    @Override
    public Page<Workspace> getWorkspacesByUserId(String userId, Pageable pageable) {
        try {
            ObjectId ownerId = new ObjectId(userId);
            return workspaceRepository.findByOwnerId(ownerId, pageable);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserIdException("Invalid user ID format: " + userId);
        }
    }

    @Override
    public Workspace getWorkspaceById(String id) {
        try {
            return workspaceRepository.findById(id)
                    .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found with id: " + id));
        } catch (IllegalArgumentException e) {
            throw new InvalidWorkspaceIdException("Invalid workspace ID format: " + id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Workspace updateWorkspace(Workspace workspace) {
        try {
            _validateWorkspace(workspace);

            // Check if workspace exists
            getWorkspaceById(workspace.getId().toString());

            return workspaceRepository.save(workspace);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating workspace", e);
            throw new WorkspaceUpdateException("Workspace update failed due to data integrity violation", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkspace(String id) {
        try {
            Workspace workspace = getWorkspaceById(id);

            // Delete associated FlexiTables first
            if (workspace.getFlexiTables() != null) {
                workspace.getFlexiTables().forEach(flexiTable ->
                        flexiTableService.deleteFlexiTable(flexiTable.getId().toString()));
            }

            workspaceRepository.deleteById(id);
            log.debug("Workspace {} deleted successfully", id);
        } catch (Exception e) {
            log.error("Error deleting workspace {}", id, e);
            throw new WorkspaceDeletionException("Failed to delete workspace: " + id, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Workspace updateCollaborators(String id, List<ObjectId> collaboratorIds) {
        Workspace workspace = getWorkspaceById(id);
        workspace.setCollaboratorId(collaboratorIds);
        return workspaceRepository.save(workspace);
    }

    private void _validateWorkspace(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null");
        }
        if (StringUtils.isBlank(workspace.getName())) {
            throw new IllegalArgumentException("Workspace name cannot be empty");
        }
        if (workspace.getOwnerId() == null) {
            throw new IllegalArgumentException("Workspace owner ID cannot be null");
        }
    }
}
