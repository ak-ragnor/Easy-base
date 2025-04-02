package com.example.product.infrastructure.config;

import com.easybase.core.collection.EntityDefinition;
import com.easybase.core.collection.FieldDefinition;
import com.easybase.core.collection.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
* Schema configuration for Product.
* This class registers the entity with the collection service.
*/
@Configuration
public class ProductSchemaConfig {

@Autowired
private CollectionService collectionService;

@Bean
public EntityDefinition productDefinition() {
EntityDefinition definition = new EntityDefinition("Product", "eb_product");

// Add fields
    FieldDefinition idField = new FieldDefinition("id", "UUID", true);
        idField.setNullable(true);
    definition.addField(idField);
    FieldDefinition nameField = new FieldDefinition("name", "String", false);
        nameField.setNullable(false);
        nameField.setLength(255);
        Map<String, Object> nameSearchMapping = new HashMap<>();
        nameSearchMapping.put("type", "text");
            Map<String, Object> nameFields = new HashMap<>();
                nameFields.put("keyword", true);
            nameSearchMapping.put("fields", nameFields);
        nameField.setSearchMapping(nameSearchMapping);
    definition.addField(nameField);
    FieldDefinition descriptionField = new FieldDefinition("description", "String", false);
        descriptionField.setNullable(true);
        descriptionField.setLength(1,000);
        Map<String, Object> descriptionSearchMapping = new HashMap<>();
        descriptionSearchMapping.put("type", "text");
        descriptionField.setSearchMapping(descriptionSearchMapping);
    definition.addField(descriptionField);
    FieldDefinition priceField = new FieldDefinition("price", "Double", false);
        priceField.setNullable(false);
        Map<String, Object> priceSearchMapping = new HashMap<>();
        priceSearchMapping.put("type", "double");
        priceField.setSearchMapping(priceSearchMapping);
    definition.addField(priceField);
    FieldDefinition skuField = new FieldDefinition("sku", "String", false);
        skuField.setNullable(false);
        skuField.setLength(50);
        Map<String, Object> skuSearchMapping = new HashMap<>();
        skuSearchMapping.put("type", "keyword");
        skuField.setSearchMapping(skuSearchMapping);
    definition.addField(skuField);
    FieldDefinition statusField = new FieldDefinition("status", "Enum", false);
        statusField.setNullable(true);
        Map<String, Object> statusSearchMapping = new HashMap<>();
        statusSearchMapping.put("type", "keyword");
        statusField.setSearchMapping(statusSearchMapping);
    definition.addField(statusField);

// Register with collection service
collectionService.registerEntityDefinition(definition);

return definition;
}
}