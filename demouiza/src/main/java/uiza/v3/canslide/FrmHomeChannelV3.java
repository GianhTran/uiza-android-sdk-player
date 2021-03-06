package uiza.v3.canslide;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import uiza.R;
import uiza.v2.home.view.BlankView;
import uiza.v2.home.view.LoadingView;
import uiza.v3.data.HomeDataV3;
import uiza.v3.view.EntityItemV3;
import vn.uiza.core.base.BaseFragment;
import vn.uiza.core.common.Constants;
import vn.uiza.core.utilities.LDisplayUtils;
import vn.uiza.core.utilities.LLog;
import vn.uiza.core.utilities.LUIUtil;
import vn.uiza.restapi.restclient.UZRestClient;
import vn.uiza.restapi.uiza.UZService;
import vn.uiza.restapi.uiza.model.v3.livestreaming.retrievealiveevent.ResultRetrieveALiveEvent;
import vn.uiza.restapi.uiza.model.v3.metadata.getdetailofmetadata.Data;
import vn.uiza.restapi.uiza.model.v3.videoondeman.listallentity.ResultListEntity;
import vn.uiza.rxandroid.ApiSubscriber;
import vn.uiza.uzv3.util.UZData;
import vn.uiza.views.LToast;
import vn.uiza.views.placeholderview.lib.placeholderview.PlaceHolderView;

/**
 * Created by www.muathu@gmail.com on 7/26/2017.
 */

public class FrmHomeChannelV3 extends BaseFragment {
    private TextView tv;
    private TextView tvMsg;
    private PlaceHolderView placeHolderView;
    private ProgressBar progressBar;
    private FloatingActionButton btPlayPlaylistFolder;
    private final int NUMBER_OF_COLUMN_1 = 1;
    private final int NUMBER_OF_COLUMN_2 = 2;
    private final int POSITION_OF_LOADING_REFRESH = 2;

    private boolean isRefreshing;
    private boolean isLoadMoreCalling;
    private final int limit = 50;
    private int page = 0;
    private int totalPage = Integer.MAX_VALUE;
    private final String orderBy = "createdAt";
    private final String orderType = "DESC";
    private final String publishToCdn = "success";
    private boolean isLivestream;
    private String metadataId = "";

    @Override
    protected String setTag() {
        return getClass().getSimpleName();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isLivestream = HomeDataV3.getInstance().getData().getName().equals(Constants.MENU_LIVESTREAM);
        if (HomeDataV3.getInstance().getData().getName().equals(Constants.MENU_HOME_V3)) {
        } else {
            metadataId = HomeDataV3.getInstance().getData().getId();
        }
        LLog.d(TAG, "metadataId " + metadataId);

        tv = (TextView) view.findViewById(R.id.tv);
        tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        btPlayPlaylistFolder = (FloatingActionButton) view.findViewById(R.id.bt_play_playlist_folder);
        if (isLivestream || metadataId == null || metadataId.isEmpty()) {
            btPlayPlaylistFolder.setVisibility(View.GONE);
        } else {
            btPlayPlaylistFolder.setVisibility(View.VISIBLE);
        }
        if (Constants.IS_DEBUG) {
            tv.setVisibility(View.VISIBLE);
            tv.setText("Debug: " + HomeDataV3.getInstance().getData().getName());
        } else {
            tv.setVisibility(View.GONE);
        }
        placeHolderView = (PlaceHolderView) view.findViewById(R.id.place_holder_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), NUMBER_OF_COLUMN_2);
        placeHolderView.getBuilder()
                .setHasFixedSize(false)
                .setItemViewCacheSize(10)
                .setLayoutManager(gridLayoutManager);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == POSITION_OF_LOADING_REFRESH) {
                    return isRefreshing ? NUMBER_OF_COLUMN_2 : NUMBER_OF_COLUMN_1;
                } else if (position == getListSize() - 1) {
                    if (isLastPage) {
                        return NUMBER_OF_COLUMN_1;
                    } else {
                        return NUMBER_OF_COLUMN_2;
                    }
                } else {
                    return NUMBER_OF_COLUMN_1;
                }
            }
        });
        LUIUtil.setPullLikeIOSVertical(placeHolderView, new LUIUtil.Callback() {
            @Override
            public void onUpOrLeft(float offset) {
            }

            @Override
            public void onUpOrLeftRefresh(float offset) {
                swipeToRefresh();
            }

            @Override
            public void onDownOrRight(float offset) {
            }

            @Override
            public void onDownOrRightRefresh(float offset) {
            }
        });

        progressBar = (ProgressBar) view.findViewById(R.id.pb);
        LUIUtil.setColorProgressBar(progressBar, ContextCompat.getColor(getActivity(), R.color.White));

        getData(false);

        btPlayPlaylistFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LLog.d(TAG, "onClick btPlayPlaylistFolder " + metadataId);
                if (UZData.getInstance().isSettingPlayer()) {
                    LLog.d(TAG, "isSettingPlayer return");
                    return;
                }
                ((HomeV3CanSlideActivity) getActivity()).onClickPlaylistFolder(metadataId);
            }
        });
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.v3_uiza_frm_channel;
    }

    private void setupData(List<Data> dataList, boolean isCallFromLoadMore) {
        int sizeW = LDisplayUtils.getScreenW(getActivity()) / 2;
        int sizeH = sizeW * 9 / 16;

        if (isCallFromLoadMore) {
            placeHolderView.removeView(getListSize() - 1);//remove loading view
        } else {
            addBlankView();
        }
        for (Data data : dataList) {
            placeHolderView.addView(new EntityItemV3(getActivity(), data, sizeW, sizeH, new EntityItemV3.Callback() {
                @Override
                public void onClick(Data data, int position) {
                    if (callback != null) {
                        callback.onClick(data, position);
                    }
                }

                @Override
                public void onPosition(int position) {
                    if (position == getListSize() - 1) {
                        loadMore();
                    }
                    if (callback != null) {
                        callback.onPosition(position);
                    }
                }
            }));
        }
        if (!isCallFromLoadMore) {
            LUIUtil.hideProgressBar(progressBar);
        } else {
            isLoadMoreCalling = false;
        }
    }

    private int getListSize() {
        return placeHolderView.getAllViewResolvers().size();
    }

    private void addBlankView() {
        for (int i = 0; i < NUMBER_OF_COLUMN_2; i++) {
            placeHolderView.addView(new BlankView());
        }
    }

    private EntityItemV3.Callback callback;

    public void setCallback(EntityItemV3.Callback callback) {
        this.callback = callback;
    }

    private boolean isLastPage;

    private void getData(final boolean isCallFromLoadMore) {
        if (page >= totalPage) {
            if (Constants.IS_DEBUG) {
                LToast.show(getActivity(), getString(R.string.this_is_last_page));
            }
            if (getListSize() >= 0) {
                placeHolderView.removeView(getListSize() - 1);//remove loading view
            }
            if (isCallFromLoadMore) {
                isLoadMoreCalling = false;
            }
            isLastPage = true;
            return;
        }

        if (Constants.IS_DEBUG) {
            LToast.show(getActivity(), getString(R.string.load_page) + page);
        }
        if (tvMsg.getVisibility() != View.GONE) {
            tvMsg.setVisibility(View.GONE);
        }

        //check if is livestream true -> get data by using api retrieveALiveEvent
        //else call getListAllEntity
        if (isLivestream) {
            UZService service = UZRestClient.createService(UZService.class);
            subscribe(service.retrieveALiveEvent(limit, page, orderBy, orderType), new ApiSubscriber<ResultRetrieveALiveEvent>() {
                @Override
                public void onSuccess(ResultRetrieveALiveEvent result) {
                    if (result == null || result.getMetadata() == null || result.getData().isEmpty()) {
                        if (tvMsg.getVisibility() != View.VISIBLE) {
                            tvMsg.setVisibility(View.VISIBLE);
                            tvMsg.setText(getString(R.string.empty_list));
                            placeHolderView.setVisibility(View.GONE);
                        }
                        if (!isCallFromLoadMore) {
                            LUIUtil.hideProgressBar(progressBar);
                        } else {
                            isLoadMoreCalling = false;
                        }
                        return;
                    }
                    if (totalPage == Integer.MAX_VALUE) {
                        int totalItem = (int) result.getMetadata().getTotal();
                        float ratio = (float) (totalItem / limit);
                        if (ratio == 0) {
                            totalPage = (int) ratio;
                        } else if (ratio > 0) {
                            totalPage = (int) ratio + 1;
                        } else {
                            totalPage = (int) ratio;
                        }
                    }

                    List<Data> dataList = result.getData();
                    if (dataList == null || dataList.isEmpty()) {
                        if (tvMsg.getVisibility() != View.VISIBLE) {
                            tvMsg.setVisibility(View.VISIBLE);
                            tvMsg.setText(getString(R.string.empty_list));
                            placeHolderView.setVisibility(View.GONE);
                        }
                        if (!isCallFromLoadMore) {
                            LUIUtil.hideProgressBar(progressBar);
                        } else {
                            isLoadMoreCalling = false;
                        }
                    }
                    setupData(dataList, isCallFromLoadMore);
                }

                @Override
                public void onFail(Throwable e) {
                    LLog.e(TAG, "retrieveALiveEvent onFail " + e.getMessage());
                    if (tvMsg.getVisibility() != View.VISIBLE) {
                        tvMsg.setVisibility(View.VISIBLE);
                        if (e != null && e.getMessage() != null) {
                            tvMsg.setText("onFail " + e.getMessage());
                        }
                        placeHolderView.setVisibility(View.GONE);
                    }
                    if (!isCallFromLoadMore) {
                        LUIUtil.hideProgressBar(progressBar);
                    } else {
                        isLoadMoreCalling = false;
                    }
                }
            });
        } else {
            //LLog.d(TAG, "getData metadataId: " + metadataId);

            UZService service = UZRestClient.createService(UZService.class);
            subscribe(service.getListAllEntity(metadataId, limit, page, orderBy, orderType, publishToCdn), new ApiSubscriber<ResultListEntity>() {
                @Override
                public void onSuccess(ResultListEntity result) {
                    if (result == null || result.getMetadata() == null || result.getData().isEmpty()) {
                        if (tvMsg.getVisibility() != View.VISIBLE) {
                            tvMsg.setVisibility(View.VISIBLE);
                            tvMsg.setText(getString(R.string.empty_list));
                            placeHolderView.setVisibility(View.GONE);
                        }
                        if (!isCallFromLoadMore) {
                            LUIUtil.hideProgressBar(progressBar);
                        } else {
                            isLoadMoreCalling = false;
                        }
                        return;
                    }
                    if (totalPage == Integer.MAX_VALUE) {
                        int totalItem = (int) result.getMetadata().getTotal();
                        float ratio = (float) (totalItem / limit);
                        if (ratio == 0) {
                            totalPage = (int) ratio;
                        } else if (ratio > 0) {
                            totalPage = (int) ratio + 1;
                        } else {
                            totalPage = (int) ratio;
                        }
                    }

                    List<Data> dataList = result.getData();
                    if (dataList == null || dataList.isEmpty()) {
                        if (tvMsg.getVisibility() != View.VISIBLE) {
                            tvMsg.setVisibility(View.VISIBLE);
                            tvMsg.setText(getString(R.string.empty_list));
                            placeHolderView.setVisibility(View.GONE);
                        }
                        if (!isCallFromLoadMore) {
                            LUIUtil.hideProgressBar(progressBar);
                        } else {
                            isLoadMoreCalling = false;
                        }
                    }
                    setupData(dataList, isCallFromLoadMore);
                }

                @Override
                public void onFail(Throwable e) {
                    LLog.e(TAG, "getListAllEntity onFail " + e.getMessage());
                    if (tvMsg.getVisibility() != View.VISIBLE) {
                        tvMsg.setVisibility(View.VISIBLE);
                        if (e != null && e.getMessage() != null) {
                            tvMsg.setText("onFail " + e.getMessage());
                        }
                        placeHolderView.setVisibility(View.GONE);
                    }
                    if (!isCallFromLoadMore) {
                        LUIUtil.hideProgressBar(progressBar);
                    } else {
                        isLoadMoreCalling = false;
                    }
                }
            });
        }
    }

    private void swipeToRefresh() {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;

        placeHolderView.addView(POSITION_OF_LOADING_REFRESH, new LoadingView());

        LUIUtil.setDelay(1000, new LUIUtil.DelayCallback() {
            @Override
            public void doAfter(int mls) {
                placeHolderView.removeAllViews();

                if (getParentFragment() != null) {
                    ((FrmHomeV3) getParentFragment()).attachFrm();
                }
            }
        });
    }

    private void loadMore() {
        if (isLoadMoreCalling) {
            return;
        }
        isLoadMoreCalling = true;
        placeHolderView.post(new Runnable() {
            @Override
            public void run() {
                placeHolderView.addView(new LoadingView());
                page++;
                LUIUtil.setDelay(1000, new LUIUtil.DelayCallback() {
                    @Override
                    public void doAfter(int mls) {
                        getData(true);
                    }
                });
            }
        });
    }
}