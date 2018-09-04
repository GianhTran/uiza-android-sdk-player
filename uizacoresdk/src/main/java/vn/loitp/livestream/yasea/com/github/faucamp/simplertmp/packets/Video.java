package vn.loitp.livestream.yasea.com.github.faucamp.simplertmp.packets;

import vn.loitp.livestream.yasea.com.github.faucamp.simplertmp.io.ChunkStreamInfo;

/**
 * Video data packet
 *
 * @author francois
 */
public class Video extends ContentData {

    public Video(RtmpHeader header) {
        super(header);
    }

    public Video() {
        super(new RtmpHeader(RtmpHeader.ChunkType.TYPE_0_FULL, ChunkStreamInfo.RTMP_CID_VIDEO, RtmpHeader.MessageType.VIDEO));
    }

    @Override
    public String toString() {
        return "RTMP Video";
    }
}
