package de.mobile.inventorylistindexer.indexer;

import static de.mobile.inventorylistindexer.indexer.Tokenizers.indexTokenizer;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import de.mobile.inventorylistindexer.indexer.vehicle.Vehicle;

class VehicleEsDocAssembler {
    private static final Logger logger = LoggerFactory.getLogger(VehicleEsDocAssembler.class);
    private static final Map<String, Integer> CATEGORY_SORT = Maps.newHashMap();
    private static final Joiner TOK_JOINER = Joiner.on(' ').skipNulls();

    static {
        CATEGORY_SORT.put("Car", 10);
        CATEGORY_SORT.put("Motorbike", 20);
        CATEGORY_SORT.put("Motorhome", 30);
        CATEGORY_SORT.put("VanUpTo7500", 40);
        CATEGORY_SORT.put("TruckOver7500", 50);
        CATEGORY_SORT.put("Trailer", 60);
        CATEGORY_SORT.put("SemiTrailerTruck", 70);
        CATEGORY_SORT.put("SemiTrailer", 80);
        CATEGORY_SORT.put("ConstructionMachine", 90);
        CATEGORY_SORT.put("Bus", 100);
        CATEGORY_SORT.put("AgriculturalVehicle", 110);
        CATEGORY_SORT.put("ForkliftTruck", 120);

    }
    static VehicleESDoc buildESDocument(Vehicle vehicle) {
        Preconditions.checkNotNull(vehicle, "vehicle");
        VehicleESDoc doc = new VehicleESDoc();
        doc.setVehicleId(vehicle.id);
        Optional.ofNullable(vehicle.customerId).map(Object::toString).ifPresent(doc::setCustomerId);
        doc.setInternalNumber(vehicle.internalNumber);
        doc.setCondition(vehicle.condition);
        doc.setCreationDate(vehicle.creationDate);
        doc.setRenewalDate(vehicle.renewalDate);
        doc.setModelDescription(vehicle.modelDescription);

            doc.setModelDescriptionSearch(tokenize(vehicle.modelDescription));


        doc.setVehicleCategory(vehicle.vehicleCategory);
        doc.setVehicleCategorySort(CATEGORY_SORT.get(vehicle.vehicleCategory));
        doc.setCategory(vehicle.category);
        Optional.ofNullable(vehicle.make).ifPresent(make -> {
            doc.setMakeId(make.id);
            doc.setMakeName(make.name);
        });
        Optional.ofNullable(vehicle.model).ifPresent(model -> {
            doc.setModelId(model.id);
            doc.setModelName(model.name);
        });
        Optional.ofNullable(vehicle.price).ifPresent(price -> {
            doc.setCurrency(price.currency);
            doc.setVatRate(price.vatRate);
            Optional.ofNullable(price.consumerValue).ifPresent(value -> {
                doc.setConsumerPriceGross(value.gross);
                doc.setConsumerPriceNet(value.net);
            });
            Optional.ofNullable(price.dealerValue).ifPresent(value -> {
                doc.setDealerPriceGross(value.gross);
                doc.setDealerPriceNet(value.net);
            });
        });


        Optional.ofNullable(vehicle.attributes).ifPresent(attr -> {
            Optional.ofNullable(attr.firstRegistration).ifPresent(doc::setFirstRegistration);
            Optional.ofNullable(attr.mileage).map(Integer::valueOf).ifPresent(doc::setMileage);
            Optional.ofNullable(attr.power).map(Integer::valueOf).ifPresent(doc::setPower);
            Optional.ofNullable(attr.gearbox).ifPresent(doc::setGearbox);
            Optional.ofNullable(attr.fuel).ifPresent(doc::setFuel);
            Optional.ofNullable(attr.usageType).ifPresent(doc::setUsageType);
            //
            Optional.ofNullable(attr.metallic).ifPresent(value -> {
                doc.setMetallic(value.toString());
            });
            Optional.ofNullable(attr.export).ifPresent(value -> {
                doc.setExport(value);
            });
            Optional.ofNullable(attr.exteriorColor).ifPresent(doc::setExteriorColor);
            Optional.ofNullable(attr.manufacturerColorName).ifPresent(doc::setManufactuererColorName);
            Optional.ofNullable(attr.constructionYear).ifPresent(value -> {
                doc.setConstructionYear(value.toString());
            });
            Optional.ofNullable(attr.axles).ifPresent(value -> {
                doc.setAxles(value.toString());
            });
            Optional.ofNullable(attr.operatingHours).ifPresent(value -> {
                doc.setOperatingHours(value.toString());
            });
            Optional.ofNullable(attr.seats).ifPresent(value -> {
                doc.setNumSeats(value.toString());
            });
        });


        Optional.ofNullable(vehicle.fuelEmission).ifPresent(emission -> {

            Optional.ofNullable(emission.emissionSticker).ifPresent(value -> {
                doc.setEmissionSticker(value);
            });
        });

        doc.setFeatures(vehicle.features);

        if(null != vehicle.features && vehicle.features.contains("closedDomain")){
            doc.setReserved(true);
        } else {
            doc.setReserved(false);
        }


        Optional.ofNullable(vehicle.uploadSticky).ifPresent(doc::setUploadSticky);

        Optional.ofNullable(vehicle.qualityStatus).ifPresent(value -> {
            doc.setQualityStatus(value.toString());
        });
        Optional.ofNullable(vehicle.images).ifPresent( doc::setImages);



        return doc;
    }

    private static String tokenize(final String text) {
        return hasText(text) ? TOK_JOINER.join(indexTokenizer.tokenize(text)) :"";
    }

    private static boolean hasText(String s){
        return null!=s && s.trim().length()>0;
    }
}
