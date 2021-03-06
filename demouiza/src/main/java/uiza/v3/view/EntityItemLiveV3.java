package uiza.v3.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;

import uiza.R;
import vn.uiza.core.common.Constants;
import vn.uiza.core.utilities.LAnimationUtil;
import vn.uiza.core.utilities.LImageUtil;
import vn.uiza.core.utilities.LUIUtil;
import vn.uiza.restapi.uiza.model.v3.metadata.getdetailofmetadata.Data;
import vn.uiza.views.placeholderview.lib.placeholderview.annotations.Click;
import vn.uiza.views.placeholderview.lib.placeholderview.annotations.Layout;
import vn.uiza.views.placeholderview.lib.placeholderview.annotations.NonReusable;
import vn.uiza.views.placeholderview.lib.placeholderview.annotations.Position;
import vn.uiza.views.placeholderview.lib.placeholderview.annotations.Resolve;
import vn.uiza.views.placeholderview.lib.placeholderview.annotations.View;

/**
 * Created by www.muathu@gmail.com on 9/16/2017.
 */

//@Animate(Animation.CARD_TOP_IN_DESC)
//@Animate(Animation.CARD_BOTTOM_IN_ASC)
@NonReusable
@Layout(R.layout.v3_uiza_entity_item)
public class EntityItemLiveV3 {

    @View(R.id.image_view)
    private ImageView imageView;
    @View(R.id.pb)
    private ProgressBar progressBar;
    @View(R.id.tv_name)
    private TextView tvName;

    private Data data;
    private Context mContext;
    private Callback mCallback;

    @Position
    private int mPosition;

    private int mSizeW;
    private int mSizeH;

    public EntityItemLiveV3(Context context, Data data, int sizeW, int sizeH, Callback callback) {
        this.mContext = context;
        this.data = data;
        this.mSizeW = sizeW;
        this.mSizeH = sizeH;
        this.mCallback = callback;
    }

    @Resolve
    private void onResolved() {
        imageView.getLayoutParams().width = mSizeW;
        imageView.getLayoutParams().height = mSizeH;
        imageView.requestLayout();

        LUIUtil.setColorProgressBar(progressBar, ContextCompat.getColor(mContext, R.color.White));

        if (data.getThumbnail() == null || data.getThumbnail().isEmpty()) {
            LImageUtil.load((Activity) mContext, Constants.URL_IMG_16x9, imageView, progressBar);
        } else {
            LImageUtil.load((Activity) mContext, data.getThumbnail(), imageView, progressBar);
        }

        tvName.setText(data.getName());
        LUIUtil.setTextShadow(tvName);

        if (mCallback != null) {
            mCallback.onPosition(mPosition);
        }
    }

    /*@LongClick(R.id.imageView)
    private void onLongClick() {
        mPlaceHolderView.removeView(this);
    }*/

    @Click(R.id.image_view)
    private void onClick() {
        LAnimationUtil.play(imageView, Techniques.Pulse, new LAnimationUtil.Callback() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onEnd() {
                if (mCallback != null) {
                    mCallback.onClick(data, mPosition);
                }
            }

            @Override
            public void onRepeat() {
            }

            @Override
            public void onStart() {
            }
        });
    }

    public interface Callback {
        public void onClick(Data data, int position);

        public void onPosition(int position);
    }
}