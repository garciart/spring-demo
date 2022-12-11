package com.rgcoding.springdemo;

import java.util.HashSet;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "medications")
public class Medication {
    // Partition key
    @DynamoDBHashKey(attributeName = "generic_name")
    private String genericName;
    @DynamoDBAttribute(attributeName = "brand_name")
    private String brandName;
    @DynamoDBAttribute(attributeName = "action")
    private String action;
    @DynamoDBAttribute(attributeName = "conditions")
    private Set<String> conditions;
    @DynamoDBAttribute(attributeName = "blood_thinner")
    private Boolean bloodThinner;
    @DynamoDBAttribute(attributeName = "schedule")
    private Integer schedule;
    @DynamoDBAttribute(attributeName = "warnings")
    private String warnings;
    @DynamoDBAttribute(attributeName = "interactions")
    private String interactions;
    @DynamoDBAttribute(attributeName = "side_effects")
    private String sideEffects;
    @DynamoDBAttribute(attributeName = "link")
    private String link;

    // Lookahead to prevent injections that use dashes or pluses (e.g., 'rm -rf', etc.)
    private String lookaheadRegex = "(?!.+?(?: -|\\- | \\+|\\+ |\\+\\-|\\-\\+|\\-\\-).+?)";
    // Short descriptions must start and end with a letter
    // Details must start with a letter and end with a letter or period
    // RegEx for name, action, and condition
    private String shortDescRegex = "^" + lookaheadRegex + "([A-Z])([A-Z\\d\\#\\-\\+ ]{1,32})([A-Z\\d])$";
    // RegEx for interactions, side effects, and warnings
    private String detailsRegex = "^" + lookaheadRegex + "([A-Z])([A-Za-z\\d\\-\\+\\,\\. ]{1,255})([A-Z\\.])$";
    private String linkRegex = "^http([s]?)\\:\\/\\/[A-Za-z\\d\\.\\_\\-\\/]{1,255}$";

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        Validator.validateRegexMatch(genericName, shortDescRegex);
        this.genericName = genericName.toUpperCase();
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        Validator.validateRegexMatch(brandName, shortDescRegex);
        this.brandName = brandName.toUpperCase();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        Validator.validateRegexMatch(action, shortDescRegex);
        this.action = action.toUpperCase();
    }

    public Set<String> getConditions() {
        return conditions;
    }

    public void setConditions(Set<String> conditions) {
        for (String c : conditions) {
            Validator.validateRegexMatch(c, shortDescRegex);
        }
        this.conditions = this.convertSetToUpper(conditions);
    }

    public Boolean getBloodThinner() {
        return bloodThinner;
    }

    public void setBloodThinner(Boolean bloodThinner) {
        this.bloodThinner = bloodThinner;
    }

    public Integer getSchedule() {
        return schedule;
    }

    public void setSchedule(Integer schedule) {
        Validator.validateInteger(schedule, 0, 4);
        this.schedule = schedule;
    }

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        Validator.validateRegexMatch(warnings, detailsRegex);
        this.warnings = warnings;
    }

    public String getInteractions() {
        return interactions;
    }

    public void setInteractions(String interactions) {
        Validator.validateRegexMatch(interactions, detailsRegex);
        this.interactions = interactions;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        Validator.validateRegexMatch(sideEffects, detailsRegex);
        this.sideEffects = sideEffects;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        Validator.validateRegexMatch(link, linkRegex);
        this.link = link;
    }

    /**
     * Convert items in a set to uppercase. You must copy the set to a separate object, to prevent Concurrent
     * Modification Exceptions
     * 
     * @param rawSet The set of items to convert to uppercase
     * @return The set, with all items in uppercase
     */
    @DynamoDBIgnore
    private Set<String> convertSetToUpper(Set<String> rawSet) {
        Set<String> temp = new HashSet<>();
        rawSet.forEach(e -> temp.add(e.toUpperCase()));
        return temp;
    }
}