package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface ElmyraGestureDetector {

    public static final class AggregateDetector extends MessageNano {
        public int count;
        public SlopeDetector detector;

        public AggregateDetector() {
            clear();
        }

        public AggregateDetector clear() {
            this.count = 0;
            this.detector = null;
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.count != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.count);
            }
            return this.detector != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(2, this.detector) : computeSerializedSize;
        }

        public AggregateDetector mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 8) {
                    this.count = codedInputByteBufferNano.readInt32();
                } else if (readTag == 18) {
                    if (this.detector == null) {
                        this.detector = new SlopeDetector();
                    }
                    codedInputByteBufferNano.readMessage(this.detector);
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.count != 0) {
                codedOutputByteBufferNano.writeInt32(1, this.count);
            }
            if (this.detector != null) {
                codedOutputByteBufferNano.writeMessage(2, this.detector);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class SlopeDetector extends MessageNano {
        public float lowerThreshold;
        public float releaseThreshold;
        public float sensitivity;
        public long timeThreshold;
        public float upperThreshold;

        public SlopeDetector() {
            clear();
        }

        public SlopeDetector clear() {
            this.sensitivity = 0.0f;
            this.upperThreshold = 0.0f;
            this.lowerThreshold = 0.0f;
            this.releaseThreshold = 0.0f;
            this.timeThreshold = 0;
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.sensitivity);
            }
            if (Float.floatToIntBits(this.upperThreshold) != Float.floatToIntBits(0.0f)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.upperThreshold);
            }
            if (Float.floatToIntBits(this.lowerThreshold) != Float.floatToIntBits(0.0f)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(3, this.lowerThreshold);
            }
            if (Float.floatToIntBits(this.releaseThreshold) != Float.floatToIntBits(0.0f)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(4, this.releaseThreshold);
            }
            return this.timeThreshold != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(5, this.timeThreshold) : computeSerializedSize;
        }

        public SlopeDetector mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 13) {
                    this.sensitivity = codedInputByteBufferNano.readFloat();
                } else if (readTag == 21) {
                    this.upperThreshold = codedInputByteBufferNano.readFloat();
                } else if (readTag == 29) {
                    this.lowerThreshold = codedInputByteBufferNano.readFloat();
                } else if (readTag == 37) {
                    this.releaseThreshold = codedInputByteBufferNano.readFloat();
                } else if (readTag == 40) {
                    this.timeThreshold = codedInputByteBufferNano.readInt64();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
                codedOutputByteBufferNano.writeFloat(1, this.sensitivity);
            }
            if (Float.floatToIntBits(this.upperThreshold) != Float.floatToIntBits(0.0f)) {
                codedOutputByteBufferNano.writeFloat(2, this.upperThreshold);
            }
            if (Float.floatToIntBits(this.lowerThreshold) != Float.floatToIntBits(0.0f)) {
                codedOutputByteBufferNano.writeFloat(3, this.lowerThreshold);
            }
            if (Float.floatToIntBits(this.releaseThreshold) != Float.floatToIntBits(0.0f)) {
                codedOutputByteBufferNano.writeFloat(4, this.releaseThreshold);
            }
            if (this.timeThreshold != 0) {
                codedOutputByteBufferNano.writeInt64(5, this.timeThreshold);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
