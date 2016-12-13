package hub.searchgateway;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("PMD")
public class SearchRequest {
    private Optional<Boolean> reserved = Optional.empty();
    private Optional<Boolean> uploadSticky = Optional.empty();
    private Optional<String> vehicleId = Optional.empty();
    private Optional<String> customerId = Optional.empty();
    private Optional<String> vehicleCategory = Optional.empty();
    private Optional<String> category = Optional.empty();
    private Optional<String> modelDescription = Optional.empty();
    private Optional<String> internalNumber = Optional.empty();
    private Optional<String> condition = Optional.empty();
    private Optional<Integer> modelId = Optional.empty();
    private Optional<Integer> makeId = Optional.empty();
    private Optional<String> usageType = Optional.empty();

    private Page page;
    private Optional<Sort> sort = Optional.empty();

    public Optional<String>  vehicleId() {
        return vehicleId;
    }
    public Optional<Boolean>  reserved() {
        return reserved;
    }
    public Optional<Boolean>  uploadSticky() {
        return uploadSticky;
    }
    public Optional<String> customerId() {
        return customerId;
    }
    public Optional<String> vehicleCategory() {
        return vehicleCategory;
    }
    public Optional<String> category() {
        return category;
    }
    public Optional<String> modelDescription() {
        return modelDescription;
    }
    public Optional<String> internalNumber() {
        return internalNumber;
    }
    public Optional<String> usageType() {
        return usageType;
    }
    public Optional<String> condition() {
        return condition;
    }
    public Optional<Integer> modelId() {
        return modelId;
    }
    public Optional<Integer> makeId() {
        return makeId;
    }



    public Page page() {
        return Optional.ofNullable(page).orElse(new Page());
    }


    public Optional<Sort> sort() {
        return sort;
    }

    public SearchRequest withMakeId(Integer makeId) {
        this.makeId = Optional.ofNullable(makeId);
        return this;
    }
    public SearchRequest withVehicleId(String vehicleId) {
        this.vehicleId = Optional.ofNullable(vehicleId);
        return this;
    }

    public SearchRequest withUsageType(String usageType) {
        this.usageType = Optional.ofNullable(usageType);
        return this;
    }

    public SearchRequest withModelId(Integer modelId) {
        this.modelId = Optional.ofNullable(modelId);
        return this;
    }

    public SearchRequest withCondition(String condition) {
        this.condition = Optional.ofNullable(condition);
        return this;
    }

    public SearchRequest withInternalNumber(String internalNumber) {
        this.internalNumber = Optional.ofNullable(internalNumber);
        return this;
    }

    public SearchRequest withModelDescription(String modelDescription) {
        this.modelDescription = Optional.ofNullable(modelDescription);
        return this;
    }

    public SearchRequest withVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = Optional.ofNullable(vehicleCategory);
        return this;
    }

    public SearchRequest withCategory(String category) {
        this.category = Optional.ofNullable(category);
        return this;
    }

    public SearchRequest withCustomerId(String customerId) {
        this.customerId = Optional.of(customerId);
        return this;
    }

    public SearchRequest withReserved(Boolean reserved) {
        this.reserved = Optional.ofNullable(reserved);;
        return this;
    }

    public SearchRequest withUploadSticky(Boolean uploadSticky) {
        this.uploadSticky = Optional.ofNullable(uploadSticky);;
        return this;
    }


    public SearchRequest withPage(Page page) {
        this.page = page;
        return this;
    }


    public SearchRequest sortBy(Sort sort) {
        this.sort = Optional.of(sort);
        return this;
    }


    public static class Page {

        private Integer number;
        private Integer size;

        public Integer number() {
            return number;
        }

        public Integer size() {
            return size;
        }

        public Page withNumber(Integer number) {
            this.number = number;
            return this;
        }

        public Page withSize(Integer size) {
            this.size = size;
            return this;
        }
    }



    public static class Sort {

        private List<String> fields;
        private Order order;

        public List<String> fields() {
            return fields;
        }

        public Order order() {
            return order;
        }

        public Sort withFields(List<String> fields) {
            this.fields = fields;
            return this;
        }

        public Sort withOrder(Order order) {
            this.order = order;
            return this;
        }

        public enum Order {
            ASC("ASCENDING"),
            DESC("DESCENDING");
            private final String description;

            Order(String description) {
                this.description = description;
            }

            public static Order from(String description) {
                return Arrays.stream(Order.values())
                    .filter(o -> o.description.equals(description))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(description));
            }
        }
    }
}
