package com.easy.base.controller;

import com.easy.base.controller.dto.ChoiceListDTO;
import com.easy.base.service.ChoiceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ChoiceListController {

    private final ChoiceListService choiceListService;

    @Autowired
    public ChoiceListController(ChoiceListService choiceListService) {
        this.choiceListService = choiceListService;
    }

    @PostMapping("/{workspaceId}/choices")
    public ResponseEntity<ChoiceListDTO> createChoiceList(@PathVariable String workspaceId, @RequestBody ChoiceListDTO choiceList) {
        ChoiceListDTO createdChoiceList = new ChoiceListDTO(choiceListService.createChoiceList(choiceList.toNewChoiceList(workspaceId)));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChoiceList);
    }

    @GetMapping("/{workspaceId}/choices/{id}")
    public ResponseEntity<ChoiceListDTO> getChoiceListById(@PathVariable String id) {
        ChoiceListDTO choiceList = new ChoiceListDTO(choiceListService.getChoiceListById(id));
        return ResponseEntity.ok(choiceList);
    }

    @GetMapping("/{workspaceId}/choices")
    public ResponseEntity<List<ChoiceListDTO>> getAllChoiceLists() {
        List<ChoiceListDTO> choiceLists = choiceListService.getAllChoiceLists().stream().map(ChoiceListDTO::new).toList();
        return ResponseEntity.ok(choiceLists);
    }

    @PutMapping("/{workspaceId}/choices")
    public ResponseEntity<ChoiceListDTO> updateChoiceList(@PathVariable String workspaceId, @RequestBody ChoiceListDTO updatedChoiceList) {
        ChoiceListDTO choiceList =  new ChoiceListDTO(choiceListService.updateChoiceList(updatedChoiceList.toChoiceList(workspaceId)));
        return ResponseEntity.ok(choiceList);
    }

    @DeleteMapping("/{workspaceId}/choices/{id}")
    public ResponseEntity<Void> deleteChoiceList(@PathVariable String id) {
        choiceListService.deleteChoiceList(id);
        return ResponseEntity.noContent().build();
    }
}
