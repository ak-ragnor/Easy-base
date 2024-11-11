package com.easy.base.service.impl;

import com.easy.base.model.ChoiceList;
import com.easy.base.repository.ChoiceListRepository;
import com.easy.base.service.ChoiceListService;
import com.easy.base.service.FlexiTableService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChoiceListServiceImpl implements ChoiceListService {

    private final ChoiceListRepository choiceListRepository;

    @Autowired
    public ChoiceListServiceImpl(ChoiceListRepository choiceListRepository) {
        this.choiceListRepository = choiceListRepository;
    }

    public ChoiceList createChoiceList(ChoiceList choiceList) {
        choiceList.validate();

        if (choiceListRepository.existsByNameAndWorkspaceId(choiceList.getName(), choiceList.getWorkspaceId())) {
            throw new IllegalArgumentException("Choice list with this name " + choiceList.getName() + " already exists.");
        }
        return choiceListRepository.save(choiceList);
    }

    public ChoiceList getChoiceListById(String id) {
        return choiceListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Choice list not found"));
    }

    public List<ChoiceList> getAllChoiceLists() {
        return choiceListRepository.findAll();
    }

    public ChoiceList updateChoiceList(ChoiceList updatedChoiceList) {
        ChoiceList existingChoiceList = getChoiceListById(updatedChoiceList.getId().toString());

        if (existingChoiceList != null && !existingChoiceList.getName().equals(updatedChoiceList.getName()) &&
                choiceListRepository.existsByNameAndWorkspaceId(updatedChoiceList.getName(), updatedChoiceList.getWorkspaceId())) {
            throw new IllegalArgumentException("Choice list with this name already exists.");
        }

        assert existingChoiceList != null;
        return choiceListRepository.save(existingChoiceList);
    }

    public void deleteChoiceList(String id) {
        choiceListRepository.deleteById(id);
    }
}
