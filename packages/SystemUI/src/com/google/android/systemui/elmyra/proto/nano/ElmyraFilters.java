package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface ElmyraFilters {

    public static final class FIRFilter extends MessageNano {
        public float[] coefficients;

        public FIRFilter() {
            clear();
        }

        public FIRFilter clear() {
            this.coefficients = WireFormatNano.EMPTY_FLOAT_ARRAY;
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            return (this.coefficients == null || this.coefficients.length <= 0) ? computeSerializedSize : (computeSerializedSize + (this.coefficients.length * 4)) + (this.coefficients.length * 1);
        }

        public FIRFilter mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                int pushLimit;
                if (readTag == 0) {
                    break;
                } else if (readTag == 10) {
                    readTag = codedInputByteBufferNano.readRawVarint32();
                    pushLimit = codedInputByteBufferNano.pushLimit(readTag);
                    int i = readTag / 4;
                    readTag = this.coefficients == null ? 0 : this.coefficients.length;
                    float[] obj = new float[(i + readTag)];
                    if (readTag != 0) {
                        System.arraycopy(this.coefficients, 0, obj, 0, readTag);
                    }
                    while (readTag < obj.length) {
                        obj[readTag] = codedInputByteBufferNano.readFloat();
                        readTag++;
                    }
                    this.coefficients = obj;
                    codedInputByteBufferNano.popLimit(pushLimit);
                } else if (readTag == 13) {
                    pushLimit = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 13);
                    readTag = this.coefficients == null ? 0 : this.coefficients.length;
                    float[] obj2 = new float[(pushLimit + readTag)];
                    if (readTag != 0) {
                        System.arraycopy(this.coefficients, 0, obj2, 0, readTag);
                    }
                    while (readTag < obj2.length - 1) {
                        obj2[readTag] = codedInputByteBufferNano.readFloat();
                        codedInputByteBufferNano.readTag();
                        readTag++;
                    }
                    obj2[readTag] = codedInputByteBufferNano.readFloat();
                    this.coefficients = obj2;
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.coefficients != null && this.coefficients.length > 0) {
                for (float writeFloat : this.coefficients) {
                    codedOutputByteBufferNano.writeFloat(1, writeFloat);
                }
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class Filter extends MessageNano {
        private static volatile Filter[] _emptyArray;
        private int parametersCase_ = 0;
        private Object parameters_;

        public Filter() {
            clear();
        }

        public static Filter[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Filter[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Filter clear() {
            clearParameters();
            this.cachedSize = -1;
            return this;
        }

        public Filter clearParameters() {
            this.parametersCase_ = 0;
            this.parameters_ = null;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.parametersCase_ == 1) {
                computeSerializedSize = CodedOutputByteBufferNano.computeMessageSize(1, (MessageNano) this.parameters_) + computeSerializedSize;
            }
            if (this.parametersCase_ == 2) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 3) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, (MessageNano) this.parameters_);
            }
            return this.parametersCase_ == 4 ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(4, (MessageNano) this.parameters_) : computeSerializedSize;
        }

        public Filter mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 10) {
                    if (this.parametersCase_ != 1) {
                        this.parameters_ = new FIRFilter();
                    }
                    codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                    this.parametersCase_ = 1;
                } else if (readTag == 18) {
                    if (this.parametersCase_ != 2) {
                        this.parameters_ = new HighpassFilter();
                    }
                    codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                    this.parametersCase_ = 2;
                } else if (readTag == 26) {
                    if (this.parametersCase_ != 3) {
                        this.parameters_ = new LowpassFilter();
                    }
                    codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                    this.parametersCase_ = 3;
                } else if (readTag == 34) {
                    if (this.parametersCase_ != 4) {
                        this.parameters_ = new MedianFilter();
                    }
                    codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                    this.parametersCase_ = 4;
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.parametersCase_ == 1) {
                codedOutputByteBufferNano.writeMessage(1, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 2) {
                codedOutputByteBufferNano.writeMessage(2, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 3) {
                codedOutputByteBufferNano.writeMessage(3, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 4) {
                codedOutputByteBufferNano.writeMessage(4, (MessageNano) this.parameters_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class HighpassFilter extends MessageNano {
        public float cutoff;
        public float rate;

        public HighpassFilter() {
            clear();
        }

        public HighpassFilter clear() {
            this.cutoff = 0.0f;
            this.rate = 0.0f;
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (Float.floatToIntBits(this.cutoff) != Float.floatToIntBits(0.0f)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.cutoff);
            }
            return Float.floatToIntBits(this.rate) != Float.floatToIntBits(0.0f) ? computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(2, this.rate) : computeSerializedSize;
        }

        public HighpassFilter mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 13) {
                    this.cutoff = codedInputByteBufferNano.readFloat();
                } else if (readTag == 21) {
                    this.rate = codedInputByteBufferNano.readFloat();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (Float.floatToIntBits(this.cutoff) != Float.floatToIntBits(0.0f)) {
                codedOutputByteBufferNano.writeFloat(1, this.cutoff);
            }
            if (Float.floatToIntBits(this.rate) != Float.floatToIntBits(0.0f)) {
                codedOutputByteBufferNano.writeFloat(2, this.rate);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class LowpassFilter extends MessageNano {
        public float cutoff;
        public float rate;

        public LowpassFilter() {
            clear();
        }

        public LowpassFilter clear() {
            this.cutoff = 0.0f;
            this.rate = 0.0f;
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (Float.floatToIntBits(this.cutoff) != Float.floatToIntBits(0.0f)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.cutoff);
            }
            return Float.floatToIntBits(this.rate) != Float.floatToIntBits(0.0f) ? computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(2, this.rate) : computeSerializedSize;
        }

        public LowpassFilter mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 13) {
                    this.cutoff = codedInputByteBufferNano.readFloat();
                } else if (readTag == 21) {
                    this.rate = codedInputByteBufferNano.readFloat();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (Float.floatToIntBits(this.cutoff) != Float.floatToIntBits(0.0f)) {
                codedOutputByteBufferNano.writeFloat(1, this.cutoff);
            }
            if (Float.floatToIntBits(this.rate) != Float.floatToIntBits(0.0f)) {
                codedOutputByteBufferNano.writeFloat(2, this.rate);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class MedianFilter extends MessageNano {
        public int windowSize;

        public MedianFilter() {
            clear();
        }

        public MedianFilter clear() {
            this.windowSize = 0;
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            return this.windowSize != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeUInt32Size(1, this.windowSize) : computeSerializedSize;
        }

        public MedianFilter mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 8) {
                    this.windowSize = codedInputByteBufferNano.readUInt32();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.windowSize != 0) {
                codedOutputByteBufferNano.writeUInt32(1, this.windowSize);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
