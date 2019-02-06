package com.google.protobuf.nano;

public abstract class ExtendableMessageNano<M extends ExtendableMessageNano<M>> extends MessageNano {
    protected FieldArray unknownFieldData;

    public M clone() throws CloneNotSupportedException {
        ExtendableMessageNano extendableMessageNano = (ExtendableMessageNano) super.clone();
        InternalNano.cloneUnknownFieldData(this, extendableMessageNano);
        return (M)extendableMessageNano;
    }
}