package com.google.android.systemui.elmyra.proto.nano;

import com.google.android.systemui.elmyra.proto.nano.ElmyraChassis.SensorEvent;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface SnapshotMessages {

    public static final class Event extends MessageNano {
        private static volatile Event[] _emptyArray;
        private int typesCase_ = 0;
        private Object types_;

        public Event() {
            clear();
        }

        public static Event[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Event[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Event clear() {
            clearTypes();
            this.cachedSize = -1;
            return this;
        }

        public Event clearTypes() {
            this.typesCase_ = 0;
            this.types_ = null;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.typesCase_ == 1) {
                computeSerializedSize = CodedOutputByteBufferNano.computeMessageSize(1, (MessageNano) this.types_) + computeSerializedSize;
            }
            return this.typesCase_ == 2 ? computeSerializedSize + CodedOutputByteBufferNano.computeEnumSize(2, ((Integer) this.types_).intValue()) : computeSerializedSize;
        }

        public int getGestureStage() {
            return this.typesCase_ == 2 ? ((Integer) this.types_).intValue() : 0;
        }

        public SensorEvent getSensorEvent() {
            return this.typesCase_ == 1 ? (SensorEvent) this.types_ : null;
        }

        public boolean hasGestureStage() {
            return this.typesCase_ == 2;
        }

        public boolean hasSensorEvent() {
            return this.typesCase_ == 1;
        }

        public Event mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 10) {
                    if (this.typesCase_ != 1) {
                        this.types_ = new SensorEvent();
                    }
                    codedInputByteBufferNano.readMessage((MessageNano) this.types_);
                    this.typesCase_ = 1;
                } else if (readTag == 16) {
                    this.types_ = Integer.valueOf(codedInputByteBufferNano.readEnum());
                    this.typesCase_ = 2;
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.typesCase_ == 1) {
                codedOutputByteBufferNano.writeMessage(1, (MessageNano) this.types_);
            }
            if (this.typesCase_ == 2) {
                codedOutputByteBufferNano.writeEnum(2, ((Integer) this.types_).intValue());
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class Snapshot extends MessageNano {
        private static volatile Snapshot[] _emptyArray;
        public Event[] events;
        public SnapshotHeader header;

        public Snapshot() {
            clear();
        }

        public static Snapshot[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Snapshot[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Snapshot clear() {
            this.header = null;
            this.events = Event.emptyArray();
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.header != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.header);
            }
            if (this.events != null && this.events.length > 0) {
                for (MessageNano messageNano : this.events) {
                    if (messageNano != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, messageNano);
                    }
                }
            }
            return computeSerializedSize;
        }

        public Snapshot mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 10) {
                    if (this.header == null) {
                        this.header = new SnapshotHeader();
                    }
                    codedInputByteBufferNano.readMessage(this.header);
                } else if (readTag == 18) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                    readTag = this.events == null ? 0 : this.events.length;
                    Event[] obj = new Event[(repeatedFieldArrayLength + readTag)];
                    if (readTag != 0) {
                        System.arraycopy(this.events, 0, obj, 0, readTag);
                    }
                    while (readTag < obj.length - 1) {
                        obj[readTag] = new Event();
                        codedInputByteBufferNano.readMessage(obj[readTag]);
                        codedInputByteBufferNano.readTag();
                        readTag++;
                    }
                    obj[readTag] = new Event();
                    codedInputByteBufferNano.readMessage(obj[readTag]);
                    this.events = obj;
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.header != null) {
                codedOutputByteBufferNano.writeMessage(1, this.header);
            }
            if (this.events != null && this.events.length > 0) {
                for (MessageNano messageNano : this.events) {
                    if (messageNano != null) {
                        codedOutputByteBufferNano.writeMessage(2, messageNano);
                    }
                }
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class SnapshotHeader extends MessageNano {
        public int feedback;
        public int gestureType;
        public long identifier;

        public SnapshotHeader() {
            clear();
        }

        public SnapshotHeader clear() {
            this.identifier = 0;
            this.gestureType = 0;
            this.feedback = 0;
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.identifier != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, this.identifier);
            }
            if (this.gestureType != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.gestureType);
            }
            return this.feedback != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, this.feedback) : computeSerializedSize;
        }

        public SnapshotHeader mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag != 8) {
                    if (readTag != 16) {
                        if (readTag == 24) {
                            readTag = codedInputByteBufferNano.readInt32();
                            switch (readTag) {
                                case 0:
                                case 1:
                                case 2:
                                    this.feedback = readTag;
                                    break;
                                default:
                                    break;
                            }
                        } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                            break;
                        }
                    } else {
                        readTag = codedInputByteBufferNano.readInt32();
                        switch (readTag) {
                            case 0:
                            case 1:
                            case 2:
                                this.gestureType = readTag;
                                break;
                            default:
                                break;
                        }
                    }
                } else {
                    this.identifier = codedInputByteBufferNano.readInt64();
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.identifier != 0) {
                codedOutputByteBufferNano.writeInt64(1, this.identifier);
            }
            if (this.gestureType != 0) {
                codedOutputByteBufferNano.writeInt32(2, this.gestureType);
            }
            if (this.feedback != 0) {
                codedOutputByteBufferNano.writeInt32(3, this.feedback);
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }

    public static final class Snapshots extends MessageNano {
        public Snapshot[] snapshots;

        public Snapshots() {
            clear();
        }

        public Snapshots clear() {
            this.snapshots = Snapshot.emptyArray();
            this.cachedSize = -1;
            return this;
        }

        protected int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.snapshots != null && this.snapshots.length > 0) {
                for (MessageNano messageNano : this.snapshots) {
                    if (messageNano != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, messageNano);
                    }
                }
            }
            return computeSerializedSize;
        }

        public Snapshots mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    break;
                } else if (readTag == 10) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                    readTag = this.snapshots == null ? 0 : this.snapshots.length;
                    Snapshot[] obj = new Snapshot[(repeatedFieldArrayLength + readTag)];
                    if (readTag != 0) {
                        System.arraycopy(this.snapshots, 0, obj, 0, readTag);
                    }
                    while (readTag < obj.length - 1) {
                        obj[readTag] = new Snapshot();
                        codedInputByteBufferNano.readMessage(obj[readTag]);
                        codedInputByteBufferNano.readTag();
                        readTag++;
                    }
                    obj[readTag] = new Snapshot();
                    codedInputByteBufferNano.readMessage(obj[readTag]);
                    this.snapshots = obj;
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    break;
                }
            }
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.snapshots != null && this.snapshots.length > 0) {
                for (MessageNano messageNano : this.snapshots) {
                    if (messageNano != null) {
                        codedOutputByteBufferNano.writeMessage(1, messageNano);
                    }
                }
            }
            super.writeTo(codedOutputByteBufferNano);
        }
    }
}
