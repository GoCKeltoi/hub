package de.mobile.inventorylistindexer.indexer.vehicle;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("PMD") // manufacturerColorName to long but i would like to have a consistent naming
@SuppressFBWarnings
public class Vehicle {
    /**
     * Globally unique ID of the mobile-ad
     * (Required)
     *
     */
    public String id;
    /**
     * Incremented value to indicate the current version
     * (Required)
     *
     */
    public Integer version;
    /**
     * ID of the customer the mobile-ad belongs to
     *
     */
    public String customerId;
    /**
     * To which country site does the mobile-ad belong
     *
     */
    public SiteId siteId;
    /**
     * Internal flag is true only when event comes from mobile-db, otherwise the flag should not be set
     *
     */
    public Boolean mobileDbSynced;
    /**
     * Internal flag used by the upload API. It will disappear, when the upload API is shut down.
     *
     */
    public Boolean uploadSticky;
    /**
     * Quality status of the ad
     *
     */
    public QualityStatus qualityStatus;
    /**
     * ID the customer uses in his own systems
     *
     */
    public String internalNumber;
    public String condition;
    /**
     * ISO 8601 date time (UTC) when the mobile-ad has been created e. g. 2016-07-12T02:59:08Z
     *
     */
    public String creationDate;
    /**
     * ISO 8601 date time (UTC) when the mobile-ad has been modified the last time e. g. 2016-07-12T02:59:08Z
     *
     */
    public String modificationDate;
    /**
     *
     * (Required)
     *
     */
    public String modelDescription;
    public String vehicleCategory;
    /**
     *
     * (Required)
     *
     */
    public String category;
    /**
     *
     * (Required)
     *
     */
    public Make make;
    /**
     *
     */
    public Model model;
    /**
     *
     * (Required)
     *
     */
    public Price price;
    /**
     * BB code string
     *
     */
    public String description;
    /**
     *
     */
    public Attributes attributes;
    public Financing financing;
    public FuelEmission fuelEmission;
    public List<String> features = new ArrayList();
    /**
     * URL of the video
     *
     */
    public String videoYouTube;
    /**
     * image URL
     *
     */
    public String heroImage;
    /**
     * image URL
     *
     */
    public String homepageCompanyLogo;
    /**
     * image URL
     *
     */
    public String dealerSearchCompanyLogo;
    /**
     * image URLs of the gallery pictures
     *
     */
    public List<String> showroomGallery = new ArrayList<String>();
    /**
     * rich text
     *
     */
    public String welcomeText;
    /**
     * image URL
     *
     */
    public String companyLogo;
    public List<String> companyServices = new ArrayList<String>();
    public List<String> images = new ArrayList<String>();
    /**
     * ISO 8601 date time (UTC) when the mobile-ad has been modified the last time (incl. miliseconds) e. g. 2016-07-12T02:59:08.973Z
     *
     */
    public String renewalDate;
}

