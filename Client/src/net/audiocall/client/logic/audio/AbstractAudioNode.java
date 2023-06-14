package net.audiocall.client.logic.audio;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractAudioNode implements AudioNode {

    private final List<AudioNode> linkedNodes;

    protected AbstractAudioNode() {
        linkedNodes = new LinkedList<>();
    }

    @Override
    public AudioNode link(AudioNode node) {
        linkedNodes.add(node);
        return node;
    }

    @Override
    public boolean unlink(AudioNode node) {
        return linkedNodes.remove(node);
    }

    @Override
    public void onData(AudioDataChunk data) throws IOException {
        sendDataToLinkedNodes(data);
    }

    protected void sendDataToLinkedNodes(AudioDataChunk data) throws IOException {
        for(AudioNode node : linkedNodes) {
            node.onData(data.duplicate());
        }
    }
}
