package com.google.protobuf.nano;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class FieldData implements Cloneable {
    private Extension<?, ?> cachedExtension;
    private List<UnknownFieldData> unknownFieldData = new ArrayList();
    private Object value;

    FieldData() {
    }

    private byte[] toByteArray() throws IOException {
        byte[] bArr = new byte[computeSerializedSize()];
        writeTo(CodedOutputByteBufferNano.newInstance(bArr));
        return bArr;
    }

    public final FieldData clone() {
        int i = 0;
        FieldData fieldData = new FieldData();
        try {
            fieldData.cachedExtension = this.cachedExtension;
            if (this.unknownFieldData == null) {
                fieldData.unknownFieldData = null;
            } else {
                fieldData.unknownFieldData.addAll(this.unknownFieldData);
            }
            if (this.value != null) {
                if (this.value instanceof MessageNano) {
                    fieldData.value = ((MessageNano) this.value).clone();
                } else if (this.value instanceof byte[]) {
                    fieldData.value = ((byte[]) this.value).clone();
                } else if (this.value instanceof byte[][]) {
                    byte[][] bArr = (byte[][]) this.value;
                    byte[][] obj = new byte[bArr.length][];
                    fieldData.value = obj;
                    while (true) {
                        int i2 = i;
                        if (i2 >= bArr.length) {
                            break;
                        }
                        obj[i2] = (byte[]) bArr[i2].clone();
                        i = i2 + 1;
                    }
                } else if (this.value instanceof boolean[]) {
                    fieldData.value = ((boolean[]) this.value).clone();
                } else if (this.value instanceof int[]) {
                    fieldData.value = ((int[]) this.value).clone();
                } else if (this.value instanceof long[]) {
                    fieldData.value = ((long[]) this.value).clone();
                } else if (this.value instanceof float[]) {
                    fieldData.value = ((float[]) this.value).clone();
                } else if (this.value instanceof double[]) {
                    fieldData.value = ((double[]) this.value).clone();
                } else if (this.value instanceof MessageNano[]) {
                    MessageNano[] messageNanoArr = (MessageNano[]) this.value;
                    MessageNano[] obj2 = new MessageNano[messageNanoArr.length];
                    fieldData.value = obj2;
                    while (i < messageNanoArr.length) {
                        obj2[i] = messageNanoArr[i].clone();
                        i++;
                    }
                }
            }
            return fieldData;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    int computeSerializedSize() {
        int i = 0;
        if (this.value != null) {
            return this.cachedExtension.computeSerializedSize(this.value);
        }
        Iterator it = this.unknownFieldData.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((UnknownFieldData) it.next()).computeSerializedSize() + i2;
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FieldData)) {
            return false;
        }
        FieldData fieldData = (FieldData) obj;
        if (this.value != null && fieldData.value != null) {
            return this.cachedExtension == fieldData.cachedExtension ? !this.cachedExtension.clazz.isArray() ? this.value.equals(fieldData.value) : this.value instanceof byte[] ? Arrays.equals((byte[]) this.value, (byte[]) fieldData.value) : this.value instanceof int[] ? Arrays.equals((int[]) this.value, (int[]) fieldData.value) : this.value instanceof long[] ? Arrays.equals((long[]) this.value, (long[]) fieldData.value) : this.value instanceof float[] ? Arrays.equals((float[]) this.value, (float[]) fieldData.value) : this.value instanceof double[] ? Arrays.equals((double[]) this.value, (double[]) fieldData.value) : this.value instanceof boolean[] ? Arrays.equals((boolean[]) this.value, (boolean[]) fieldData.value) : Arrays.deepEquals((Object[]) this.value, (Object[]) fieldData.value) : false;
        } else {
            if (this.unknownFieldData != null && fieldData.unknownFieldData != null) {
                return this.unknownFieldData.equals(fieldData.unknownFieldData);
            }
            try {
                return Arrays.equals(toByteArray(), fieldData.toByteArray());
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public int hashCode() {
        try {
            return Arrays.hashCode(toByteArray()) + 527;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (this.value != null) {
            this.cachedExtension.writeTo(this.value, codedOutputByteBufferNano);
            return;
        }
        for (UnknownFieldData writeTo : this.unknownFieldData) {
            writeTo.writeTo(codedOutputByteBufferNano);
        }
    }
}
