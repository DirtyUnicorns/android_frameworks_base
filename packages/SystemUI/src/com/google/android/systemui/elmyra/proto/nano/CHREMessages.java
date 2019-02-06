package com.google.android.systemui.elmyra.proto.nano;

import com.android.systemui.R;
import com.google.android.systemui.elmyra.proto.nano.ElmyraChassis.Chassis;
import com.google.android.systemui.elmyra.proto.nano.ElmyraGestureDetector.AggregateDetector;
import com.google.android.systemui.elmyra.proto.nano.SnapshotMessages.Snapshot;
import com.google.android.systemui.elmyra.proto.nano.SnapshotMessages.SnapshotHeader;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface CHREMessages {

    public static final class GestureDetected extends MessageNano {
        public boolean hapticConsumed;
        public boolean hostSuspended;

        public GestureDetected() {
            clear();
        }

        public GestureDetected clear() {
            this.hostSuspended = false;
            this.hapticConsumed = false;
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.hostSuspended) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(1, this.hostSuspended);
            }
            return this.hapticConsumed ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(2, this.hapticConsumed) : computeSerializedSize;
        }

        public GestureDetected mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 8) {
                    this.hostSuspended = codedInputByteBufferNano.readBool();
                } else if (readTag == 16) {
                    this.hapticConsumed = codedInputByteBufferNano.readBool();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.hostSuspended) {
                codedOutputByteBufferNano.writeBool(1, this.hostSuspended);
            }
            if (this.hapticConsumed) {
                codedOutputByteBufferNano.writeBool(2, this.hapticConsumed);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class MessageV1 extends MessageNano {
        private int parametersCase_ = 0;
        private Object parameters_;

        public MessageV1() {
            clear();
        }

        public static MessageV1 parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (MessageV1) MessageNano.mergeFrom(new MessageV1(), bArr);
        }

        public MessageV1 clear() {
            clearParameters();
            this.cachedSize = -1;
            return this;
        }

        public MessageV1 clearParameters() {
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
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, ((Boolean) this.parameters_).booleanValue());
            }
            if (this.parametersCase_ == 4) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 5) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(5, ((Float) this.parameters_).floatValue());
            }
            if (this.parametersCase_ == 6) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(6, ((Boolean) this.parameters_).booleanValue());
            }
            if (this.parametersCase_ == 7) {
                computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(7, ((Float) this.parameters_).floatValue());
            }
            if (this.parametersCase_ == 8) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(8, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 9) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(9, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 10) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(10, ((Boolean) this.parameters_).booleanValue());
            }
            return this.parametersCase_ == 11 ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(11, (MessageNano) this.parameters_) : computeSerializedSize;
        }

        public Chassis getChassis() {
            return this.parametersCase_ == 11 ? (Chassis) this.parameters_ : null;
        }

        public GestureDetected getGestureDetected() {
            return this.parametersCase_ == 4 ? (GestureDetected) this.parameters_ : null;
        }

        public float getGestureProgress() {
            return this.parametersCase_ == 5 ? ((Float) this.parameters_).floatValue() : 0.0f;
        }

        public Snapshot getSnapshot() {
            return this.parametersCase_ == 9 ? (Snapshot) this.parameters_ : null;
        }

        public boolean hasChassis() {
            return this.parametersCase_ == 11;
        }

        public boolean hasGestureDetected() {
            return this.parametersCase_ == 4;
        }

        public boolean hasGestureProgress() {
            return this.parametersCase_ == 5;
        }

        public boolean hasSnapshot() {
            return this.parametersCase_ == 9;
        }

        public MessageV1 mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 10:
                        if (this.parametersCase_ != 1) {
                            this.parameters_ = new NanoappInit();
                        }
                        codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                        this.parametersCase_ = 1;
                        continue;
                    case 18:
                        if (this.parametersCase_ != 2) {
                            this.parameters_ = new AggregateDetector();
                        }
                        codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                        this.parametersCase_ = 2;
                        continue;
                    case 24:
                        this.parameters_ = Boolean.valueOf(codedInputByteBufferNano.readBool());
                        this.parametersCase_ = 3;
                        continue;
                    case 34:
                        if (this.parametersCase_ != 4) {
                            this.parameters_ = new GestureDetected();
                        }
                        codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                        this.parametersCase_ = 4;
                        continue;
                    case 45:
                        this.parameters_ = Float.valueOf(codedInputByteBufferNano.readFloat());
                        this.parametersCase_ = 5;
                        continue;
                    case 48:
                        this.parameters_ = Boolean.valueOf(codedInputByteBufferNano.readBool());
                        this.parametersCase_ = 6;
                        continue;
                    case 61:
                        this.parameters_ = Float.valueOf(codedInputByteBufferNano.readFloat());
                        this.parametersCase_ = 7;
                        continue;
                    case R.styleable.AppCompatTheme_editTextBackground /*66*/:
                        if (this.parametersCase_ != 8) {
                            this.parameters_ = new SnapshotHeader();
                        }
                        codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                        this.parametersCase_ = 8;
                        continue;
                    case R.styleable.AppCompatTheme_listPopupWindowStyle /*74*/:
                        if (this.parametersCase_ != 9) {
                            this.parameters_ = new Snapshot();
                        }
                        codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                        this.parametersCase_ = 9;
                        continue;
                    case 80:
                        this.parameters_ = Boolean.valueOf(codedInputByteBufferNano.readBool());
                        this.parametersCase_ = 10;
                        continue;
                    case R.styleable.AppCompatTheme_seekBarStyle /*90*/:
                        if (this.parametersCase_ != 11) {
                            this.parameters_ = new Chassis();
                        }
                        codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                        this.parametersCase_ = 11;
                        continue;
                    default:
                        if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                            break;
                        }
                        continue;
                }
            }
        }

        public MessageV1 setAggregateDetector(AggregateDetector aggregateDetector) {
            if (aggregateDetector != null) {
                this.parametersCase_ = 2;
                this.parameters_ = aggregateDetector;
                return this;
            }
            throw new NullPointerException();
        }

        public MessageV1 setCalibrationRequest(boolean z) {
            this.parametersCase_ = 10;
            this.parameters_ = Boolean.valueOf(z);
            return this;
        }

        public MessageV1 setProgressReportThreshold(float f) {
            this.parametersCase_ = 7;
            this.parameters_ = Float.valueOf(f);
            return this;
        }

        public MessageV1 setRecognizerStart(boolean z) {
            this.parametersCase_ = 6;
            this.parameters_ = Boolean.valueOf(z);
            return this;
        }

        public MessageV1 setSnapshotRequest(SnapshotHeader snapshotHeader) {
            if (snapshotHeader != null) {
                this.parametersCase_ = 8;
                this.parameters_ = snapshotHeader;
                return this;
            }
            throw new NullPointerException();
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.parametersCase_ == 1) {
                codedOutputByteBufferNano.writeMessage(1, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 2) {
                codedOutputByteBufferNano.writeMessage(2, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 3) {
                codedOutputByteBufferNano.writeBool(3, ((Boolean) this.parameters_).booleanValue());
            }
            if (this.parametersCase_ == 4) {
                codedOutputByteBufferNano.writeMessage(4, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 5) {
                codedOutputByteBufferNano.writeFloat(5, ((Float) this.parameters_).floatValue());
            }
            if (this.parametersCase_ == 6) {
                codedOutputByteBufferNano.writeBool(6, ((Boolean) this.parameters_).booleanValue());
            }
            if (this.parametersCase_ == 7) {
                codedOutputByteBufferNano.writeFloat(7, ((Float) this.parameters_).floatValue());
            }
            if (this.parametersCase_ == 8) {
                codedOutputByteBufferNano.writeMessage(8, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 9) {
                codedOutputByteBufferNano.writeMessage(9, (MessageNano) this.parameters_);
            }
            if (this.parametersCase_ == 10) {
                codedOutputByteBufferNano.writeBool(10, ((Boolean) this.parameters_).booleanValue());
            }
            if (this.parametersCase_ == 11) {
                codedOutputByteBufferNano.writeMessage(11, (MessageNano) this.parameters_);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class NanoappInit extends MessageNano {
        public Chassis chassis;
        public int samplingInterval;

        public NanoappInit() {
            clear();
        }

        public NanoappInit clear() {
            this.chassis = null;
            this.samplingInterval = 0;
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.chassis != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.chassis);
            }
            return this.samplingInterval != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeUInt32Size(2, this.samplingInterval) : computeSerializedSize;
        }

        public NanoappInit mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 10) {
                    if (this.chassis == null) {
                        this.chassis = new Chassis();
                    }
                    codedInputByteBufferNano.readMessage(this.chassis);
                } else if (readTag == 16) {
                    this.samplingInterval = codedInputByteBufferNano.readUInt32();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.chassis != null) {
                codedOutputByteBufferNano.writeMessage(1, this.chassis);
            }
            if (this.samplingInterval != 0) {
                codedOutputByteBufferNano.writeUInt32(2, this.samplingInterval);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
