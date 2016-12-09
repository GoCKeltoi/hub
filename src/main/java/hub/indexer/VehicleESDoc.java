package de.mobile.inventorylistindexer.indexer;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD")
class VehicleESDoc {
    private String vehicleId;
    private String customerId;
    private String internalNumber;
    private String condition;
    private String creationDate;
    private String renewalDate;
    private String modelDescription;
    private String modelDescriptionSearch;
    private String vehicleCategory;
    private Integer vehicleCategorySort;
    private String category;
    private Integer makeId;
    private String makeName;
    private Integer modelId;
    private String modelName;
    private String consumerPriceNet;
    private String consumerPriceGross;
    private String dealerPriceNet;
    private String dealerPriceGross;
    private String vatRate;
    private String currency;
    private String firstRegistration;
    private Integer mileage;
    private Integer power;
    private String gearbox;
    private String fuel;
    private String usageType;
    private boolean reserved;
    private boolean uploadSticky;
    private boolean export;

    private List<String> features = new ArrayList();

    // New fields ...
    private String metallic;
    private String exteriorColor;
    private String manufactuererColorName;

    private String qualityStatus;
    private List<String> images;
    private String constructionYear;
    private String axles;
    private String operatingHours;
    private String emissionSticker;
    private String numSeats;


    public String getVehicleId() {
        return this.vehicleId;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public String getInternalNumber() {
        return this.internalNumber;
    }

    public String getCondition() {
        return this.condition;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public String getRenewalDate() {
        return this.renewalDate;
    }

    public String getModelDescription() {
        return this.modelDescription;
    }

    public String getModelDescriptionSearch() {
        return this.modelDescriptionSearch;
    }

    public String getVehicleCategory() {
        return this.vehicleCategory;
    }

    public Integer getVehicleCategorySort() {
        return this.vehicleCategorySort;
    }

    public String getCategory() {
        return this.category;
    }

    public Integer getMakeId() {
        return this.makeId;
    }

    public String getMakeName() {
        return this.makeName;
    }

    public Integer getModelId() {
        return this.modelId;
    }

    public String getModelName() {
        return this.modelName;
    }

    public String getConsumerPriceNet() {
        return this.consumerPriceNet;
    }

    public String getConsumerPriceGross() {
        return this.consumerPriceGross;
    }

    public String getDealerPriceNet() {
        return this.dealerPriceNet;
    }

    public String getDealerPriceGross() {
        return this.dealerPriceGross;
    }

    public String getVatRate() {
        return vatRate;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getFirstRegistration() {
        return this.firstRegistration;
    }

    public Integer getMileage() {
        return this.mileage;
    }

    public Integer getPower() {
        return this.power;
    }

    public String getGearbox() {
        return this.gearbox;
    }

    public String getFuel() {
        return this.fuel;
    }

    public String getUsageType() {
        return usageType;
    }

    public Boolean getReserved() {
        return this.reserved;
    }

    public Boolean getUploadSticky() {
        return this.uploadSticky;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    //
 public String getConstructionYear() {
     return this.constructionYear;
 }

    public String getQualityStatus() {
        return qualityStatus;
    }

    public List<String> getImages() {
        return images;
    }

    public String getMetallic() {
        return metallic;
    }

    public String getExteriorColor() {
        return exteriorColor;
    }

    public String getManufactuererColorName() {
        return manufactuererColorName;
    }

    public String getAxles() {
        return axles;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public String getEmissionSticker() {
        return emissionSticker;
    }

    public String getNumSeats(){return numSeats;}

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setInternalNumber(String internalNumber) {
        this.internalNumber = internalNumber;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    public void setModelDescriptionSearch(String modelDescriptionSearch) {
        this.modelDescriptionSearch = modelDescriptionSearch;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public void setVehicleCategorySort(Integer vehicleCategorySort) {
        this.vehicleCategorySort = vehicleCategorySort;
    }


    public void setCategory(String category) {
        this.category = category;
    }

    public void setMakeId(Integer makeId) {
        this.makeId = makeId;
    }

    public void setMakeName(String makeName) {
        this.makeName = makeName;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setConsumerPriceNet(String consumerPriceNet) {
        this.consumerPriceNet = consumerPriceNet;
    }

    public void setConsumerPriceGross(String consumerPriceGross) {
        this.consumerPriceGross = consumerPriceGross;
    }

    public void setDealerPriceNet(String dealerPriceNet) {
        this.dealerPriceNet = dealerPriceNet;
    }

    public void setDealerPriceGross(String dealerPriceGross) {
        this.dealerPriceGross = dealerPriceGross;
    }

    public void setVatRate(String vatRate) {
        this.vatRate = vatRate;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setFirstRegistration(String firstRegistration) {
        this.firstRegistration = firstRegistration;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public void setGearbox(String gearbox) {
        this.gearbox = gearbox;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }

    public void setUploadSticky(Boolean uploadSticky) {
        this.uploadSticky = uploadSticky;
    }


    public void setConstructionYear(String constructionYear) {
        this.constructionYear = constructionYear;
    }

    public void setQualityStatus(String qualityStatus) {
        this.qualityStatus = qualityStatus;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setMetallic(String metallic) {
        this.metallic = metallic;
    }

    public void setExteriorColor(String exteriorColor) {
        this.exteriorColor = exteriorColor;
    }

    public void setManufactuererColorName(String manufactuererColorName) {
        this.manufactuererColorName = manufactuererColorName;
    }

    public void setAxles(String axles) {
        this.axles = axles;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public void setEmissionSticker(String emissionSticker) {
        this.emissionSticker = emissionSticker;
    }

    public void setNumSeats(String numSeats) {
        this.numSeats = numSeats;
    }

    public boolean isExport() {
        return export;
    }

    public void setExport(boolean export) {
        this.export = export;
    }
}
