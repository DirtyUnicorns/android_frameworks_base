package com.google.android.systemui.elmyra.proto.nano;

import com.google.android.systemui.elmyra.proto.nano.ElmyraFilters.Filter;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface ElmyraChassis {

    public static final class Chassis extends MessageNano {
        public Filter[] defaultFilters;
        public float samplingRate;
        public Sensor[] sensors;

        public Chassis() {
            clear();
        }

        public Chassis clear() {
            this.sensors = Sensor.emptyArray();
            this.defaultFilters = Filter.emptyArray();
            this.samplingRate = 0.0f;
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int i = 0;
            int computeSerializedSize = super.computeSerializedSize();
            if (this.sensors != null && this.sensors.length > 0) {
                for (MessageNano messageNano : this.sensors) {
                    if (messageNano != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, messageNano);
                    }
                }
            }
            if (this.defaultFilters != null && this.defaultFilters.length > 0) {
                while (i < this.defaultFilters.length) {
                    MessageNano messageNano2 = this.defaultFilters[i];
                    if (messageNano2 != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, messageNano2);
                    }
                    i++;
                }
            }
            return Float.floatToIntBits(this.samplingRate) != Float.floatToIntBits(0.0f) ? computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(3, this.samplingRate) : computeSerializedSize;
        }

        public Chassis mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                int repeatedFieldArrayLength;
                Sensor[] sensors;
                Filter[] filters;
                if (readTag == 0) {
                    break;
                } else if (readTag == 10) {
                    repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                    readTag = this.sensors == null ? 0 : this.sensors.length;
                    sensors = new Sensor[(repeatedFieldArrayLength + readTag)];
                    if (readTag != 0) {
                        System.arraycopy(this.sensors, 0, sensors, 0, readTag);
                    }
                    while (readTag < sensors.length - 1) {
                        sensors[readTag] = new Sensor();
                        codedInputByteBufferNano.readMessage(sensors[readTag]);
                        codedInputByteBufferNano.readTag();
                        readTag++;
                    }
                    sensors[readTag] = new Sensor();
                    codedInputByteBufferNano.readMessage(sensors[readTag]);
                    this.sensors = sensors;
                } else if (readTag == 18) {
                    repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                    readTag = this.defaultFilters == null ? 0 : this.defaultFilters.length;
                    filters = new Filter[(repeatedFieldArrayLength + readTag)];
                    if (readTag != 0) {
                        System.arraycopy(this.defaultFilters, 0, filters, 0, readTag);
                    }
                    while (readTag < filters.length - 1) {
                        filters[readTag] = new Filter();
                        codedInputByteBufferNano.readMessage(filters[readTag]);
                        codedInputByteBufferNano.readTag();
                        readTag++;
                    }
                    filters[readTag] = new Filter();
                    codedInputByteBufferNano.readMessage(filters[readTag]);
                    this.defaultFilters = filters;
                } else if (readTag == 29) {
                    this.samplingRate = codedInputByteBufferNano.readFloat();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = 0;
            if (this.sensors != null && this.sensors.length > 0) {
                for (MessageNano messageNano : this.sensors) {
                    if (messageNano != null) {
                        codedOutputByteBufferNano.writeMessage(1, messageNano);
                    }
                }
            }
            if (this.defaultFilters != null && this.defaultFilters.length > 0) {
                while (i < this.defaultFilters.length) {
                    MessageNano messageNano2 = this.defaultFilters[i];
                    if (messageNano2 != null) {
                        codedOutputByteBufferNano.writeMessage(2, messageNano2);
                    }
                    i++;
                }
            }
            if (Float.floatToIntBits(this.samplingRate) != Float.floatToIntBits(0.0f)) {
                codedOutputByteBufferNano.writeFloat(3, this.samplingRate);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class Sensor extends MessageNano {
        private static volatile Sensor[] _emptyArray;
        public Filter[] filters;
        public int gain;
        public float sensitivity;
        public int source;

        public Sensor() {
            clear();
        }

        public static Sensor[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Sensor[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Sensor clear() {
            this.source = 0;
            this.gain = 0;
            this.sensitivity = 0.0f;
            this.filters = Filter.emptyArray();
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.source != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeUInt32Size(1, this.source);
            }
            if (this.gain != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.gain);
            }
            if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(3, this.sensitivity);
            }
            if (this.filters != null && this.filters.length > 0) {
                for (MessageNano messageNano : this.filters) {
                    if (messageNano != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, messageNano);
                    }
                }
            }
            return computeSerializedSize;
        }

        public Sensor mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 8) {
                    this.source = codedInputByteBufferNano.readUInt32();
                } else if (readTag == 16) {
                    this.gain = codedInputByteBufferNano.readInt32();
                } else if (readTag == 29) {
                    this.sensitivity = codedInputByteBufferNano.readFloat();
                } else if (readTag == 34) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                    readTag = this.filters == null ? 0 : this.filters.length;
                    Filter[] filters = new Filter[(repeatedFieldArrayLength + readTag)];
                    if (readTag != 0) {
                        System.arraycopy(this.filters, 0, filters, 0, readTag);
                    }
                    while (readTag < filters.length - 1) {
                        filters[readTag] = new Filter();
                        codedInputByteBufferNano.readMessage(filters[readTag]);
                        codedInputByteBufferNano.readTag();
                        readTag++;
                    }
                    filters[readTag] = new Filter();
                    codedInputByteBufferNano.readMessage(filters[readTag]);
                    this.filters = filters;
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.source != 0) {
                codedOutputByteBufferNano.writeUInt32(1, this.source);
            }
            if (this.gain != 0) {
                codedOutputByteBufferNano.writeInt32(2, this.gain);
            }
            if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
                codedOutputByteBufferNano.writeFloat(3, this.sensitivity);
            }
            if (this.filters != null && this.filters.length > 0) {
                for (MessageNano messageNano : this.filters) {
                    if (messageNano != null) {
                        codedOutputByteBufferNano.writeMessage(4, messageNano);
                    }
                }
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class SensorEvent extends MessageNano {
        public long timestamp;
        public float[] values;

        public SensorEvent() {
            clear();
        }

        public SensorEvent clear() {
            this.timestamp = 0;
            this.values = WireFormatNano.EMPTY_FLOAT_ARRAY;
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.timestamp != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeUInt64Size(1, this.timestamp);
            }
            return (this.values == null || this.values.length <= 0) ? computeSerializedSize : (computeSerializedSize + (this.values.length * 4)) + (this.values.length * 1);
        }

        public SensorEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                int pushLimit;
                if (readTag == 0) {
                    break;
                } else if (readTag == 8) {
                    this.timestamp = codedInputByteBufferNano.readUInt64();
                } else if (readTag == 18) {
                    readTag = codedInputByteBufferNano.readRawVarint32();
                    pushLimit = codedInputByteBufferNano.pushLimit(readTag);
                    int i = readTag / 4;
                    readTag = this.values == null ? 0 : this.values.length;
                    float[] obj = new float[(i + readTag)];
                    if (readTag != 0) {
                        System.arraycopy(this.values, 0, obj, 0, readTag);
                    }
                    while (readTag < obj.length) {
                        obj[readTag] = codedInputByteBufferNano.readFloat();
                        readTag++;
                    }
                    this.values = obj;
                    codedInputByteBufferNano.popLimit(pushLimit);
                } else if (readTag == 21) {
                    pushLimit = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 21);
                    readTag = this.values == null ? 0 : this.values.length;
                    float[] obj2 = new float[(pushLimit + readTag)];
                    if (readTag != 0) {
                        System.arraycopy(this.values, 0, obj2, 0, readTag);
                    }
                    while (readTag < obj2.length - 1) {
                        obj2[readTag] = codedInputByteBufferNano.readFloat();
                        codedInputByteBufferNano.readTag();
                        readTag++;
                    }
                    obj2[readTag] = codedInputByteBufferNano.readFloat();
                    this.values = obj2;
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.timestamp != 0) {
                codedOutputByteBufferNano.writeUInt64(1, this.timestamp);
            }
            if (this.values != null && this.values.length > 0) {
                for (float writeFloat : this.values) {
                    codedOutputByteBufferNano.writeFloat(2, writeFloat);
                }
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
