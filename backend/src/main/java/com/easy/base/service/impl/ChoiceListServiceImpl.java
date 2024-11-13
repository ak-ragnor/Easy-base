package com.easy.base.service.impl;

import com.easy.base.exception.choiceList.ChoiceListNotFoundException;
import com.easy.base.exception.choiceList.DuplicateChoiceListException;
import com.easy.base.model.ChoiceList;
import com.easy.base.repository.ChoiceListRepository;
import com.easy.base.service.ChoiceListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class ChoiceListServiceImpl implements ChoiceListService {

    private final ChoiceListRepository choiceListRepository;

    @Autowired
    public ChoiceListServiceImpl(ChoiceListRepository choiceListRepository) {
        this.choiceListRepository = choiceListRepository;
    }

    @Override
    @Transactional
    public ChoiceList createChoiceList(ChoiceList choiceList) {
        log.debug("Creating choice list: {}", choiceList.getName());
        choiceList.validate();

        if (choiceListRepository.existsByNameAndWorkspaceId(
                choiceList.getName(), choiceList.getWorkspaceId())) {
            throw new DuplicateChoiceListException(
                    String.format("Choice list with name '%s' already exists in workspace '%s'",
                            choiceList.getName(), choiceList.getWorkspaceId()));
        }

        return choiceListRepository.save(choiceList);
    }

    @Override
    @Transactional(readOnly = true)
    public ChoiceList getChoiceListById(String id) {
        return choiceListRepository.findById(id)
                .orElseThrow(() -> new ChoiceListNotFoundException(
                        String.format("Choice list not found with id: %s", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChoiceList> getAllChoiceLists(Pageable pageable) {
        return choiceListRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public ChoiceList updateChoiceList(ChoiceList updatedChoiceList) {
        ChoiceList existingChoiceList = getChoiceListById(updatedChoiceList.getId().toString());

        if (!existingChoiceList.getName().equals(updatedChoiceList.getName()) &&
                choiceListRepository.existsByNameAndWorkspaceId(
                        updatedChoiceList.getName(), updatedChoiceList.getWorkspaceId())) {
            throw new DuplicateChoiceListException(
                    String.format("Choice list with name '%s' already exists in workspace '%s'",
                            updatedChoiceList.getName(), updatedChoiceList.getWorkspaceId()));
        }

        return choiceListRepository.save(updatedChoiceList);
    }

    @Override
    @Transactional
    public void deleteChoiceList(String id) {
        if (!choiceListRepository.existsById(id)) {
            throw new ChoiceListNotFoundException(
                    String.format("Choice list not found with id: %s", id));
        }
        choiceListRepository.deleteById(id);
    }
}
