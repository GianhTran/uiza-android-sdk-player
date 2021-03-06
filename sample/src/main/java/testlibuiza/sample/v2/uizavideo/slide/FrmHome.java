package testlibuiza.sample.v2.uizavideo.slide;

/**
 * Created by www.muathu@gmail.com on 12/24/2017.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import testlibuiza.R;
import vn.uiza.core.base.BaseFragment;
import vn.uiza.uzv1.view.IOnBackPressed;

public class FrmHome extends BaseFragment implements IOnBackPressed {

    @Override
    protected String setTag() {
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        frmRootView.findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((V2UizaVideoIMActivitySlide) getActivity()).play();
            }
        });
        frmRootView.findViewById(R.id.bt_switch_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((V2UizaVideoIMActivitySlide) getActivity()).replaceFragment(new FrmLogin());
            }
        });
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.v2_frm_home;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
