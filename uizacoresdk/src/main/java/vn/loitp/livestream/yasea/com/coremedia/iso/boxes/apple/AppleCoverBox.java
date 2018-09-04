package vn.loitp.livestream.yasea.com.coremedia.iso.boxes.apple;

import java.util.logging.Logger;

/**
 *
 */
public final class AppleCoverBox extends AbstractAppleMetaDataBox {
    public static final String TYPE = "covr";
    private static Logger LOG = Logger.getLogger(AppleCoverBox.class.getName());


    public AppleCoverBox() {
        super(TYPE);
    }


    public void setPng(byte[] pngData) {
        appleDataBox = new AppleDataBox();
        appleDataBox.setVersion(0);
        appleDataBox.setFlags(0xe);
        appleDataBox.setFourBytes(new byte[4]);
        appleDataBox.setData(pngData);
    }


    public void setJpg(byte[] jpgData) {
        appleDataBox = new AppleDataBox();
        appleDataBox.setVersion(0);
        appleDataBox.setFlags(0xd);
        appleDataBox.setFourBytes(new byte[4]);
        appleDataBox.setData(jpgData);
    }

    @Override
    public String getValue() {
        return "---";
    }

    @Override
    public void setValue(String value) {
        LOG.warning("---");
    }
}