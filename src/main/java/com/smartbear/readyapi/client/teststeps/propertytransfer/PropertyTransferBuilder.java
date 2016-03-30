package com.smartbear.readyapi.client.teststeps.propertytransfer;

import com.smartbear.readyapi.client.model.PropertyTransfer;

public class PropertyTransferBuilder {
    private PropertyTransfer transfer = new PropertyTransfer();
    private PropertyTransferSourceBuilder propertyTransferSourceBuilder;
    private PropertyTransferTargetBuilder propertyTransferTargetBuilder;

    public static PropertyTransferBuilder from(PropertyTransferSourceBuilder propertyTransferSourceBuilder) {
        return new PropertyTransferBuilder().withSource(propertyTransferSourceBuilder);
    }

    public static PropertyTransferBuilder newTransfer() {
        return new PropertyTransferBuilder().named( "Transfer " + String.valueOf(Math.random()));
    }

    public static PropertyTransferBuilder newTransfer( String name ) {
        return new PropertyTransferBuilder().named( name );
    }

    private PropertyTransferBuilder named(String name) {
        transfer.setTransferName( name );
        return this;
    }

    public PropertyTransferBuilder withSource(PropertyTransferSourceBuilder propertyTransferSourceBuilder) {
        this.propertyTransferSourceBuilder = propertyTransferSourceBuilder;
        return this;
    }

    public PropertyTransferBuilder withTarget(PropertyTransferTargetBuilder propertyTransferTargetBuilder) {
        return to(propertyTransferTargetBuilder);
    }

    public PropertyTransferBuilder to(PropertyTransferTargetBuilder propertyTransferTargetBuilder) {
        this.propertyTransferTargetBuilder = propertyTransferTargetBuilder;
        return this;
    }


    PropertyTransfer build() {
        if (propertyTransferSourceBuilder == null) {
            throw new IllegalStateException("No source defined for property transfer");
        }
        if (propertyTransferTargetBuilder == null) {
            throw new IllegalStateException("No target defined for property transfer");
        }
        transfer.setSource(propertyTransferSourceBuilder.build());
        transfer.setTarget(propertyTransferTargetBuilder.build());
        return transfer;
    }
}
