package com.ashok.it.adminservice.event;

import java.time.LocalDateTime;

public class CategoryEvent {
    
    private EventType eventType;
    private Long categoryId;
    private String name;
    private String description;
    private Boolean active;
    private LocalDateTime timestamp;
    private String sourceService;
    
    public enum EventType {
        CATEGORY_CREATED,
        CATEGORY_UPDATED,
        CATEGORY_DELETED,
        CATEGORY_ACTIVATED,
        CATEGORY_DEACTIVATED
    }
    
    public CategoryEvent() {
    }
    
    public CategoryEvent(EventType eventType, Long categoryId, String name, String description, 
                        Boolean active, String sourceService) {
        this.eventType = eventType;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.active = active;
        this.timestamp = LocalDateTime.now();
        this.sourceService = sourceService;
    }
    
    public EventType getEventType() {
        return eventType;
    }
    
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getSourceService() {
        return sourceService;
    }
    
    public void setSourceService(String sourceService) {
        this.sourceService = sourceService;
    }
}
