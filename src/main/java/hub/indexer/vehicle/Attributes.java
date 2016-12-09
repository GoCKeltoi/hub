package de.mobile.inventorylistindexer.indexer.vehicle;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("PMD") // manufacturerColorName to long but i would like to have a consistent naming
@SuppressFBWarnings
    public class Attributes {

        public String usageType;
        /**
         * ISO 8601 date e. g. 2016-07
         * (Required)
         *
         */
        public String firstRegistration;
        /**
         *
         * (Required)
         *
         */
        public Integer mileage;
        public String vin;
        public Integer cubicCapacity;
        /**
         * in kW
         *
         */
        public Integer power;
        public String gearbox;
        public String fuel;
        public Integer axles;
        public String wheelFormula;
        public String exteriorColor;
        public String manufacturerColorName;
        public String doors;
        public Integer seats;
        public String interiorColor;
        public String interiorType;
        /**
         * ISO 8601 date e. g. 2016-07
         *
         */
        public String generalInspection;
        public Integer constructionYear;
        /**
         * ISO 8601 date e. g. 2016-07-06
         *
         */
        public String constructionDate;
        public Integer numberOfPreviousOwners;
        public String countryVersion;
        /**
         *
         * (Required)
         *
         */
        public Boolean damageUnrepaired;
        public Boolean accidentDamaged;
        public Boolean roadworthy;
        public Integer taxPower;
        public String airbag;
        public String brake;
        public String climatisation;
        public Dimension dimension;
        public String drivingCab;
        public String drivingMode;
        public Integer europalletStorageSpaces;
        public String hydraulicInstallation;
        public Integer installationHeight;
        public Integer licensedWeight;
        public Integer liftingCapacity;
        public Integer liftingHeight;
        public Integer loadCapacity;
        public LoadingSpace loadingSpace;
        public Integer numberOfBunks;
        public Integer operatingHours;
        public List<String> parkingAssistants = new ArrayList<String>();
        public Integer shippingVolume;
        public Integer schwackeCode;
        public Kba kba;
        /**
         * ISO 8601 date e. g. 2016-07-06
         *
         */
        public String deliveryDate;
        public Integer deliveryPeriod;
        public String usedCarSeal;
        public Certificate certificate;
        public Boolean fourWheelDrive;
        public Boolean hybridPlugin;
        public Boolean e10Enabled;
        public Boolean catalyticConverter;
        public Boolean particulateFilterDiesel;
        public Boolean biodieselConversion;
        public Boolean biodieselSuitable;
        public Boolean vegetableoilfuelConversion;
        public Boolean vegetableoilfuelSuitable;
        public Boolean performanceHandlingSystem;
        public Boolean startStopSystem;
        public Boolean metallic;
        public Boolean newHuAu;
        public Boolean fullServiceHistory;
        public Boolean nonSmokerVehicle;
        public Boolean taxi;
        public Boolean abs;
        public Boolean airSuspension;
        public Boolean alloyWheels;
        public Boolean automaticRainSensor;
        public Boolean auxiliaryHeating;
        public Boolean awning;
        public Boolean bed;
        public Boolean bendingLights;
        public Boolean bluetooth;
        public Boolean box;
        public Boolean bss;
        public Boolean bunkBed;
        public Boolean cabin;
        public Boolean cdMultichanger;
        public Boolean cdPlayer;
        public Boolean centralLocking;
        public Boolean centralLubricantApplication;
        public Boolean circularSeatingArrangement;
        public Boolean coldstore;
        public Boolean compressor;
        public Boolean crane;
        public Boolean cruiseControl;
        public Boolean damageByHail;
        public Boolean daytimeRunningLights;
        public Boolean disabledAccessible;
        public Boolean disabledConversion;
        public Boolean discBrake;
        public Boolean divider;
        public Boolean driversSleepingCompartment;
        public Boolean ebs;
        public Boolean electricAdjustableSeats;
        public Boolean electricExteriorMirrors;
        public Boolean electricHeatedSeats;
        public Boolean electricStarter;
        public Boolean electricWindows;
        public Boolean esp;
        public Boolean frontFogLights;
        public Boolean frontHydraulics;
        public Boolean frontJack;
        public Boolean fullFairing;
        public Boolean handsFreePhoneSystem;
        public Boolean headUpDisplay;
        public Boolean immobilizer;
        public Boolean isofix;
        public Boolean kickstarter;
        public Boolean kitchen;
        public Boolean lightSensor;
        public Boolean middleSeatingArrangement;
        public Boolean mp3Interface;
        public Boolean multifunctionalWheel;
        public Boolean municipal;
        public Boolean navigationSystem;
        public Boolean onBoardComputer;
        public Boolean panoramicGlassRoof;
        public Boolean parkingSensors;
        public Boolean powerAssistedSteering;
        public Boolean protectionRoof;
        public Boolean quickChangeAttachment;
        public Boolean rearGarage;
        public Boolean retarder;
        public Boolean roadLicence;
        public Boolean rollOverBar;
        public Boolean roofBars;
        public Boolean roofRails;
        public Boolean secondaryAirConditioning;
        public Boolean sepShower;
        public Boolean sideSeatingArrangement;
        public Boolean skiBag;
        public Boolean sleepSeats;
        public Boolean slidingdoor;
        public Boolean solarEnergySystem;
        public Boolean sportPackage;
        public Boolean sportSeats;
        public Boolean sunroof;
        public Boolean superSingleWheels;
        public Boolean tailLift;
        public Boolean tractionControlSystem;
        public Boolean trailerCoupling;
        public Boolean tuner;
        public Boolean tv;
        public Boolean ureaTankAdBlue;
        public Boolean wc;
        public Boolean windshield;
        public Boolean xenonHeadlights;
        public Boolean export;
        public Boolean warranty;
        public Boolean rentingPossible;

    }


