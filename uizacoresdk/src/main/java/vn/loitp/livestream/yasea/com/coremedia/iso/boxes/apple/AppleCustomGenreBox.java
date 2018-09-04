package vn.loitp.livestream.yasea.com.coremedia.iso.boxes.apple;

import vn.loitp.livestream.yasea.com.coremedia.iso.Utf8;

/**
 *
 */
public final class AppleCustomGenreBox extends AbstractAppleMetaDataBox {
    public static final String TYPE = "\u00a9gen";


    public AppleCustomGenreBox() {
        super(TYPE);
        appleDataBox = AppleDataBox.getStringAppleDataBox();
    }

    public String getGenre() {
        return Utf8.convert(appleDataBox.getData());
    }

    public void setGenre(String genre) {
        appleDataBox = new AppleDataBox();
        appleDataBox.setVersion(0);
        appleDataBox.setFlags(1);
        appleDataBox.setFourBytes(new byte[4]);
        appleDataBox.setData(Utf8.convert(genre));
    }
}