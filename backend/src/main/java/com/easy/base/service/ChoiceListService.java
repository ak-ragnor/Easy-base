package com.easy.base.service;

import com.easy.base.model.ChoiceList;

import java.util.List;

public interface ChoiceListService {

    public ChoiceList createChoiceList(ChoiceList choiceList);

    public ChoiceList getChoiceListById(String id);

    public List<ChoiceList> getAllChoiceLists();

    public ChoiceList updateChoiceList(ChoiceList updatedChoiceList);

    public void deleteChoiceList(String id);
}
