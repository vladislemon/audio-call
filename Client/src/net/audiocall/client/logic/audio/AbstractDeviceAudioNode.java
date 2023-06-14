package net.audiocall.client.logic.audio;

import javax.sound.sampled.*;

public abstract class AbstractDeviceAudioNode extends AbstractAudioNode implements DeviceAudioNode {

    public enum DeviceType {
        TARGET_DATA_LINE,
        SOURCE_DATA_LINE
    }

    private static final int BUFFER_SIZE_UNDEFINED = -1;
    protected final DeviceType deviceType;
    protected int lineBufferSize;
    protected DataLine dataLine;
    protected byte[] internalBuffer;

    protected AbstractDeviceAudioNode(DeviceType deviceType, int lineBufferSize) {
        this.deviceType = deviceType;
        this.lineBufferSize = lineBufferSize;
    }

    protected AbstractDeviceAudioNode(DeviceType deviceType) {
        this(deviceType, BUFFER_SIZE_UNDEFINED);
    }

    @Override
    public void open(AudioFormat format) throws Exception {
        if(deviceType == DeviceType.TARGET_DATA_LINE) {
            dataLine = AudioSystem.getTargetDataLine(format);
            if(lineBufferSize == BUFFER_SIZE_UNDEFINED) {
                ((TargetDataLine) dataLine).open(format);
            } else {
                ((TargetDataLine) dataLine).open(format, lineBufferSize);
            }
        } else {
            dataLine = AudioSystem.getSourceDataLine(format);
            if(lineBufferSize == BUFFER_SIZE_UNDEFINED) {
                ((SourceDataLine) dataLine).open(format);
            } else {
                ((SourceDataLine) dataLine).open(format, lineBufferSize);
            }
        }
        lineBufferSize = dataLine.getBufferSize();
        internalBuffer = new byte[lineBufferSize];
    }

    @Override
    public void start() {
        dataLine.start();
    }

    @Override
    public void stop() {
        dataLine.stop();
    }

    @Override
    public void close() {
        dataLine.close();
        internalBuffer = null;
    }

    @Override
    public DataLine getLine() {
        return dataLine;
    }

    @Override
    public boolean isSupported(AudioFormat format) {
        if(deviceType == DeviceType.TARGET_DATA_LINE) {
            return AudioSystem.getTargetFormats(format.getEncoding(), format).length > 0;
        } else {
            return dataLine.getFormat().equals(format);
        }
    }
}
