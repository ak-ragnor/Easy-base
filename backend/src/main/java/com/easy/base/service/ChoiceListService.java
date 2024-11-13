package com.easy.base.service;

import com.easy.base.model.ChoiceList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChoiceListService {

    public ChoiceList createChoiceList(ChoiceList choiceList);

    public ChoiceList getChoiceListById(String id);

    public Page<ChoiceList> getAllChoiceLists(Pageable pageable);

    public ChoiceList updateChoiceList(ChoiceList updatedChoiceList);

    public void deleteChoiceList(String id);
}
