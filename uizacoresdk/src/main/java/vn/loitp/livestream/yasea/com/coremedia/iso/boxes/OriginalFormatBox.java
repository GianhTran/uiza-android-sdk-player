/*
 * Copyright 2008 CoreMedia AG, Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.loitp.livestream.yasea.com.coremedia.iso.boxes;

import java.nio.ByteBuffer;

import vn.loitp.livestream.yasea.com.coremedia.iso.IsoFile;
import vn.loitp.livestream.yasea.com.coremedia.iso.IsoTypeReader;
import vn.loitp.livestream.yasea.com.googlecode.mp4parser.AbstractBox;

/**
 * The Original Format Box contains the four-character-code of the original untransformed sample description.
 * See ISO/IEC 14496-12 for details.
 *
 * @see ProtectionSchemeInformationBox
 */

public class OriginalFormatBox extends AbstractBox {
    public static final String TYPE = "frma";

    private String dataFormat = "    ";

    public OriginalFormatBox() {
        super("frma");
    }

    public String getDataFormat() {
        return dataFormat;
    }


    public void setDataFormat(String dataFormat) {
        assert dataFormat.length() == 4;
        this.dataFormat = dataFormat;
    }

    protected long getContentSize() {
        return 4;
    }

    @Override
    public void _parseDetails(ByteBuffer content) {
        dataFormat = IsoTypeReader.read4cc(content);
    }

    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        byteBuffer.put(IsoFile.fourCCtoBytes(dataFormat));
    }


    public String toString() {
        return "OriginalFormatBox[dataFormat=" + getDataFormat() + "]";
    }
}
