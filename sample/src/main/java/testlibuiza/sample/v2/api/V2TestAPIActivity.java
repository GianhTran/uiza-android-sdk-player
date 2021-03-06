package testlibuiza.sample.v2.api;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import testlibuiza.R;
import testlibuiza.app.LSApplication;
import vn.uiza.core.base.BaseActivity;
import vn.uiza.core.common.Constants;
import vn.uiza.core.utilities.LDialogUtil;
import vn.uiza.core.utilities.LLog;
import vn.uiza.core.utilities.LUIUtil;
import vn.uiza.restapi.restclient.RestClientTracking;
import vn.uiza.restapi.restclient.RestClientV2;
import vn.uiza.restapi.uiza.UZServiceV1;
import vn.uiza.restapi.uiza.model.tracking.UizaTracking;
import vn.uiza.restapi.uiza.model.v2.auth.Auth;
import vn.uiza.restapi.uiza.model.v2.auth.JsonBodyAuth;
import vn.uiza.restapi.uiza.model.v2.getdetailentity.GetDetailEntity;
import vn.uiza.restapi.uiza.model.v2.getdetailentity.JsonBodyGetDetailEntity;
import vn.uiza.restapi.uiza.model.v2.getlinkdownload.Mpd;
import vn.uiza.restapi.uiza.model.v2.getlinkplay.GetLinkPlay;
import vn.uiza.restapi.uiza.model.v2.listallentity.JsonBodyListAllEntity;
import vn.uiza.restapi.uiza.model.v2.listallentity.ListAllEntity;
import vn.uiza.restapi.uiza.model.v2.listallentityrelation.JsonBodyListAllEntityRelation;
import vn.uiza.restapi.uiza.model.v2.listallentityrelation.ListAllEntityRelation;
import vn.uiza.restapi.uiza.model.v2.listallmetadata.JsonBodyMetadataList;
import vn.uiza.restapi.uiza.model.v2.listallmetadata.ListAllMetadata;
import vn.uiza.restapi.uiza.model.v2.search.JsonBodySearch;
import vn.uiza.restapi.uiza.model.v2.search.Search;
import vn.uiza.rxandroid.ApiSubscriber;

public class V2TestAPIActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv = (TextView) findViewById(R.id.tv);
        //RestClientV2.init(Constants.URL_DEV_UIZA_VERSION_2);
        RestClientV2.init(Constants.URL_DEV_UIZA_VERSION_2_STAG);
        findViewById(R.id.bt_get_token).setOnClickListener(this);
        findViewById(R.id.bt_check_token).setOnClickListener(this);
        findViewById(R.id.bt_list_metadata).setOnClickListener(this);
        findViewById(R.id.bt_search).setOnClickListener(this);
        findViewById(R.id.bt_list_entity).setOnClickListener(this);
        findViewById(R.id.bt_get_detail_entity).setOnClickListener(this);
        findViewById(R.id.bt_entity_ralation).setOnClickListener(this);
        findViewById(R.id.bt_get_link_play).setOnClickListener(this);

        //for track
        findViewById(R.id.bt_track_1_dev).setOnClickListener(this);
        findViewById(R.id.bt_track_2_dev).setOnClickListener(this);
        findViewById(R.id.bt_track_3_dev).setOnClickListener(this);
        findViewById(R.id.bt_track_4_dev).setOnClickListener(this);
        findViewById(R.id.bt_track_5_dev).setOnClickListener(this);
        findViewById(R.id.bt_track_6_dev).setOnClickListener(this);
        findViewById(R.id.bt_track_7_dev).setOnClickListener(this);
        findViewById(R.id.bt_track_8_dev).setOnClickListener(this);

        LinearLayout rootView = (LinearLayout) findViewById(R.id.root_view);
        viewList = rootView.getTouchables();
        setEnableAllButton(false);
    }

    private List<View> viewList = new ArrayList<>();

    private void setEnableAllButton(boolean isEnable) {
        if (isEnable) {
            for (View view : viewList) {
                view.setEnabled(true);
            }
        } else {
            for (View view : viewList) {
                if (view.getId() != R.id.bt_get_token) {
                    view.setEnabled(false);
                }
            }
        }
    }

    @Override
    protected boolean setFullScreen() {
        return false;
    }

    @Override
    protected String setTag() {
        return getClass().getSimpleName();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.v2_activity_test_api;
    }

    @Override
    public void onClick(View v) {
        tv.setText("Loading...");
        String json;
        switch (v.getId()) {
            case R.id.bt_get_token:
                auth();
                break;
            case R.id.bt_check_token:
                checkToken();
                break;
            case R.id.bt_list_metadata:
                listMetadata();
                break;
            case R.id.bt_search:
                search();
                break;
            case R.id.bt_list_entity:
                listEntity();
                break;
            case R.id.bt_get_detail_entity:
                getDetailEntity();
                break;
            case R.id.bt_entity_ralation:
                getListAllEntityRelation();
                break;
            case R.id.bt_get_link_play:
                getLinkPlay();
                break;
            case R.id.bt_track_1_dev:
                json = "{\"app_id\":\"a204e9cdeca44948a33e0d012ef74e90\",\"page_type\":\"iframe\",\"viewer_user_id\":\"\",\"user_agent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36\",\"referrer\":\"\",\"device_id\":\"5cb442458ae4cca6ceda2f541c718cd8\",\"player_id\":\"658347e9-e516-4f7d-b4f2-02b23e640724\",\"player_name\":\"DuyQT's Player\",\"player_version\":\"1.0.4\",\"entity_id\":\"513c49db-7b91-4485-949e-80bd0c57d189\",\"entity_name\":\"20170406_031552000_iOS\",\"entity_series\":\"\",\"entity_producer\":\"\",\"entity_content_type\":\"video\",\"entity_language_code\":\"\",\"entity_variant_name\":\"\",\"entity_variant_id\":\"\",\"entity_duration\":\"0\",\"entity_stream_type\":\"on-demand\",\"entity_encoding_variant\":\"\",\"entity_cdn\":\"\",\"play_through\":\"0\",\"event_type\":\"display\",\"timestamp\":\"2018-01-12T05:57:55.735Z\"}";
                init(create(json));
                break;
            case R.id.bt_track_2_dev:
                json = "{\"app_id\":\"a204e9cdeca44948a33e0d012ef74e90\",\"page_type\":\"iframe\",\"viewer_user_id\":\"\",\"user_agent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36\",\"referrer\":\"\",\"device_id\":\"5cb442458ae4cca6ceda2f541c718cd8\",\"player_id\":\"658347e9-e516-4f7d-b4f2-02b23e640724\",\"player_name\":\"DuyQT's Player\",\"player_version\":\"1.0.4\",\"entity_id\":\"513c49db-7b91-4485-949e-80bd0c57d189\",\"entity_name\":\"20170406_031552000_iOS\",\"entity_series\":\"\",\"entity_producer\":\"\",\"entity_content_type\":\"video\",\"entity_language_code\":\"\",\"entity_variant_name\":\"\",\"entity_variant_id\":\"\",\"entity_duration\":\"35\",\"entity_stream_type\":\"on-demand\",\"entity_encoding_variant\":\"\",\"entity_cdn\":\"\",\"play_through\":\"0\",\"event_type\":\"plays_requested\",\"timestamp\":\"2018-01-12T05:57:56.546Z\"}";
                init(create(json));
                break;
            case R.id.bt_track_3_dev:
                json = "{\"app_id\":\"a204e9cdeca44948a33e0d012ef74e90\",\"page_type\":\"iframe\",\"viewer_user_id\":\"\",\"user_agent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36\",\"referrer\":\"\",\"device_id\":\"5cb442458ae4cca6ceda2f541c718cd8\",\"player_id\":\"658347e9-e516-4f7d-b4f2-02b23e640724\",\"player_name\":\"DuyQT's Player\",\"player_version\":\"1.0.4\",\"entity_id\":\"513c49db-7b91-4485-949e-80bd0c57d189\",\"entity_name\":\"20170406_031552000_iOS\",\"entity_series\":\"\",\"entity_producer\":\"\",\"entity_content_type\":\"video\",\"entity_language_code\":\"\",\"entity_variant_name\":\"\",\"entity_variant_id\":\"\",\"entity_duration\":\"35\",\"entity_stream_type\":\"on-demand\",\"entity_encoding_variant\":\"\",\"entity_cdn\":\"\",\"play_through\":\"0\",\"event_type\":\"video_starts\",\"timestamp\":\"2018-01-12T05:58:14.563Z\"}";
                init(create(json));
                break;
            case R.id.bt_track_4_dev:
                json = "{\"app_id\":\"a204e9cdeca44948a33e0d012ef74e90\",\"page_type\":\"iframe\",\"viewer_user_id\":\"\",\"user_agent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36\",\"referrer\":\"\",\"device_id\":\"5cb442458ae4cca6ceda2f541c718cd8\",\"player_id\":\"658347e9-e516-4f7d-b4f2-02b23e640724\",\"player_name\":\"DuyQT's Player\",\"player_version\":\"1.0.4\",\"entity_id\":\"513c49db-7b91-4485-949e-80bd0c57d189\",\"entity_name\":\"20170406_031552000_iOS\",\"entity_series\":\"\",\"entity_producer\":\"\",\"entity_content_type\":\"video\",\"entity_language_code\":\"\",\"entity_variant_name\":\"\",\"entity_variant_id\":\"\",\"entity_duration\":\"35\",\"entity_stream_type\":\"on-demand\",\"entity_encoding_variant\":\"\",\"entity_cdn\":\"\",\"play_through\":\"25\",\"event_type\":\"play_through\",\"timestamp\":\"2018-01-12T05:58:17.817Z\"}";
                init(create(json));
                break;
            case R.id.bt_track_5_dev:
                json = "{\"app_id\":\"a204e9cdeca44948a33e0d012ef74e90\",\"page_type\":\"iframe\",\"viewer_user_id\":\"\",\"user_agent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36\",\"referrer\":\"\",\"device_id\":\"5cb442458ae4cca6ceda2f541c718cd8\",\"player_id\":\"658347e9-e516-4f7d-b4f2-02b23e640724\",\"player_name\":\"DuyQT's Player\",\"player_version\":\"1.0.4\",\"entity_id\":\"513c49db-7b91-4485-949e-80bd0c57d189\",\"entity_name\":\"20170406_031552000_iOS\",\"entity_series\":\"\",\"entity_producer\":\"\",\"entity_content_type\":\"video\",\"entity_language_code\":\"\",\"entity_variant_name\":\"\",\"entity_variant_id\":\"\",\"entity_duration\":\"35\",\"entity_stream_type\":\"on-demand\",\"entity_encoding_variant\":\"\",\"entity_cdn\":\"\",\"play_through\":\"50\",\"event_type\":\"play_through\",\"timestamp\":\"2018-01-12T05:58:17.816Z\"}";
                init(create(json));
                break;
            case R.id.bt_track_6_dev:
                json = "{\"app_id\":\"a204e9cdeca44948a33e0d012ef74e90\",\"page_type\":\"iframe\",\"viewer_user_id\":\"\",\"user_agent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36\",\"referrer\":\"\",\"device_id\":\"5cb442458ae4cca6ceda2f541c718cd8\",\"player_id\":\"658347e9-e516-4f7d-b4f2-02b23e640724\",\"player_name\":\"DuyQT's Player\",\"player_version\":\"1.0.4\",\"entity_id\":\"513c49db-7b91-4485-949e-80bd0c57d189\",\"entity_name\":\"20170406_031552000_iOS\",\"entity_series\":\"\",\"entity_producer\":\"\",\"entity_content_type\":\"video\",\"entity_language_code\":\"\",\"entity_variant_name\":\"\",\"entity_variant_id\":\"\",\"entity_duration\":\"35\",\"entity_stream_type\":\"on-demand\",\"entity_encoding_variant\":\"\",\"entity_cdn\":\"\",\"play_through\":\"75\",\"event_type\":\"play_through\",\"timestamp\":\"2018-01-12T05:58:17.815Z\"}";
                init(create(json));
                break;
            case R.id.bt_track_7_dev:
                json = "{\"app_id\":\"a204e9cdeca44948a33e0d012ef74e90\",\"page_type\":\"iframe\",\"viewer_user_id\":\"\",\"user_agent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36\",\"referrer\":\"\",\"device_id\":\"5cb442458ae4cca6ceda2f541c718cd8\",\"player_id\":\"658347e9-e516-4f7d-b4f2-02b23e640724\",\"player_name\":\"DuyQT's Player\",\"player_version\":\"1.0.4\",\"entity_id\":\"513c49db-7b91-4485-949e-80bd0c57d189\",\"entity_name\":\"20170406_031552000_iOS\",\"entity_series\":\"\",\"entity_producer\":\"\",\"entity_content_type\":\"video\",\"entity_language_code\":\"\",\"entity_variant_name\":\"\",\"entity_variant_id\":\"\",\"entity_duration\":\"35\",\"entity_stream_type\":\"on-demand\",\"entity_encoding_variant\":\"\",\"entity_cdn\":\"\",\"play_through\":\"100\",\"event_type\":\"play_through\",\"timestamp\":\"2018-01-12T05:58:17.815Z\"}";
                init(create(json));
                break;
            case R.id.bt_track_8_dev:
                json = "{\"app_id\":\"a204e9cdeca44948a33e0d012ef74e90\",\"page_type\":\"iframe\",\"viewer_user_id\":\"\",\"user_agent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36\",\"referrer\":\"\",\"device_id\":\"5cb442458ae4cca6ceda2f541c718cd8\",\"player_id\":\"658347e9-e516-4f7d-b4f2-02b23e640724\",\"player_name\":\"DuyQT's Player\",\"player_version\":\"1.0.4\",\"entity_id\":\"513c49db-7b91-4485-949e-80bd0c57d189\",\"entity_name\":\"20170406_031552000_iOS\",\"entity_series\":\"\",\"entity_producer\":\"\",\"entity_content_type\":\"video\",\"entity_language_code\":\"\",\"entity_variant_name\":\"\",\"entity_variant_id\":\"\",\"entity_duration\":\"35\",\"entity_stream_type\":\"on-demand\",\"entity_encoding_variant\":\"\",\"entity_cdn\":\"\",\"play_through\":\"0\",\"event_type\":\"replay\",\"timestamp\":\"2018-01-12T05:58:17.834Z\"}";
                init(create(json));
                break;
        }
    }

    private void showTv(Object o) {
        LUIUtil.printBeautyJson(o, tv);
    }

    private String appId;

    private void auth() {
        UZServiceV1 service = RestClientV2.createService(UZServiceV1.class);
        /*String accessKeyId = Constants.A_K_DEV;
        String secretKeyId = Constants.S_K_DEV;*/
        String accessKeyId = Constants.A_K_UQC;
        String secretKeyId = Constants.S_K_UQC;

        JsonBodyAuth jsonBodyAuth = new JsonBodyAuth();
        jsonBodyAuth.setAccessKeyId(accessKeyId);
        jsonBodyAuth.setSecretKeyId(secretKeyId);

        subscribe(service.auth(jsonBodyAuth), new ApiSubscriber<Auth>() {
            @Override
            public void onSuccess(Auth auth) {
                showTv(auth);
                RestClientV2.addAuthorization(auth.getData().getToken());
                appId = auth.getData().getAppId();
                setEnableAllButton(true);
            }

            @Override
            public void onFail(Throwable e) {
                LLog.e(TAG, "auth onFail " + e.getMessage());
                showDialogError("auth onFail " + e.getMessage());
            }
        });
    }

    private void checkToken() {
        UZServiceV1 service = RestClientV2.createService(UZServiceV1.class);
        subscribe(service.checkToken(), new ApiSubscriber<Auth>() {
            @Override
            public void onSuccess(Auth auth) {
                showTv(auth);
            }

            @Override
            public void onFail(Throwable e) {
                LLog.e(TAG, "checkToken onFail " + e.getMessage());
                showDialogError("checkToken onFail " + e.getMessage());
            }
        });
    }

    private void showDialogError(String msg) {
        LDialogUtil.showDialog1(activity, msg, new LDialogUtil.Callback1() {
            @Override
            public void onClick1() {
                /*if (activity != null) {
                    activity.onBackPressed();
                }*/
            }

            @Override
            public void onCancel() {
                /*if (activity != null) {
                    activity.onBackPressed();
                }*/
            }
        });
    }

    private void listMetadata() {
        UZServiceV1 service = RestClientV2.createService(UZServiceV1.class);
        int limit = 999;
        String orderBy = "name";
        String orderType = "ASC";
        JsonBodyMetadataList jsonBodyMetadataList = new JsonBodyMetadataList();
        jsonBodyMetadataList.setLimit(limit);
        jsonBodyMetadataList.setOrderBy(orderBy);
        jsonBodyMetadataList.setOrderType(orderType);
        subscribe(service.listAllMetadataV2(jsonBodyMetadataList), new ApiSubscriber<ListAllMetadata>() {
            @Override
            public void onSuccess(ListAllMetadata listAllMetadata) {
                showTv(listAllMetadata);
            }

            @Override
            public void onFail(Throwable e) {
                LLog.e(TAG, "listAllMetadataV2 onFail " + e.getMessage());
                showDialogError("listAllMetadataV2 onFail " + e.getMessage());
            }
        });
    }

    private void search() {
        UZServiceV1 service = RestClientV2.createService(UZServiceV1.class);
        JsonBodySearch jsonBodySearch = new JsonBodySearch();
        jsonBodySearch.setKeyword("lol");
        jsonBodySearch.setLimit(50);
        jsonBodySearch.setPage(0);

        subscribe(service.searchEntityV2(jsonBodySearch), new ApiSubscriber<Search>() {
            @Override
            public void onSuccess(Search search) {
                showTv(search);
            }

            @Override
            public void onFail(Throwable e) {
                showDialogError("searchEntityV2 onFail " + e.getMessage());
            }
        });
    }

    private void listEntity() {
        UZServiceV1 service = RestClientV2.createService(UZServiceV1.class);
        JsonBodyListAllEntity jsonBodyListAllEntity = new JsonBodyListAllEntity();
        //jsonBodyListAllEntity.setMetadataId(metadataId);
        jsonBodyListAllEntity.setLimit(50);
        jsonBodyListAllEntity.setPage(0);
        jsonBodyListAllEntity.setOrderBy("createdAt");
        jsonBodyListAllEntity.setOrderType("DESC");
        subscribe(service.listAllEntityV2(jsonBodyListAllEntity), new ApiSubscriber<ListAllEntity>() {
            @Override
            public void onSuccess(ListAllEntity listAllEntity) {
                showTv(listAllEntity);
            }

            @Override
            public void onFail(Throwable e) {
                showDialogError("listAllEntityV2 onFail " + e.getMessage());
            }
        });
    }

    private void getDetailEntity() {
        UZServiceV1 service = RestClientV2.createService(UZServiceV1.class);
        JsonBodyGetDetailEntity jsonBodyGetDetailEntity = new JsonBodyGetDetailEntity();
        jsonBodyGetDetailEntity.setId("5bd11904-07b8-4859-bdc8-9fee0b2199b2");
        subscribe(service.getDetailEntityV2(jsonBodyGetDetailEntity), new ApiSubscriber<GetDetailEntity>() {
            @Override
            public void onSuccess(GetDetailEntity getDetailEntityV2) {
                showTv(getDetailEntityV2);
            }

            @Override
            public void onFail(Throwable e) {
                showDialogError("getDetailEntityV2 onFail " + e.getMessage());
            }
        });
    }

    private void getListAllEntityRelation() {
        UZServiceV1 service = RestClientV2.createService(UZServiceV1.class);
        JsonBodyListAllEntityRelation jsonBodyListAllEntityRelation = new JsonBodyListAllEntityRelation();
        jsonBodyListAllEntityRelation.setId("5bd11904-07b8-4859-bdc8-9fee0b2199b2");
        subscribe(service.getListAllEntityRalationV2(jsonBodyListAllEntityRelation), new ApiSubscriber<ListAllEntityRelation>() {
            @Override
            public void onSuccess(ListAllEntityRelation listAllEntityRelation) {
                showTv(listAllEntityRelation);
            }

            @Override
            public void onFail(Throwable e) {
                showDialogError("getListAllEntityRalationV2 onFail " + e.getMessage());
            }
        });
    }

    private void getLinkPlay() {
        UZServiceV1 service = RestClientV2.createService(UZServiceV1.class);
        subscribe(service.getLinkPlayV2("e01c8c6c-c372-4fee-9f31-cb6d5b7fefe7", appId), new ApiSubscriber<GetLinkPlay>() {
            @Override
            public void onSuccess(GetLinkPlay getLinkPlay) {
                List<String> listLinkPlay = new ArrayList<>();
                List<Mpd> mpdList = getLinkPlay.getMpd();
                for (Mpd mpd : mpdList) {
                    if (mpd.getUrl() != null) {
                        listLinkPlay.add(mpd.getUrl());
                    }
                }
                LLog.d(TAG, "getLinkPlayV2 toJson: " + LSApplication.getInstance().getGson().toJson(listLinkPlay));
                showTv(listLinkPlay);
            }

            @Override
            public void onFail(Throwable e) {
                showDialogError("getLinkPlayV2 onFail " + e.getMessage());
            }
        });
    }

    //for track
    private UizaTracking create(String json) {
        return LSApplication.getInstance().getGson().fromJson(json, UizaTracking.class);
    }

    private String s;

    public void init(final UizaTracking uizaTracking) {
        RestClientTracking.init(Constants.URL_TRACKING_DEV);
        s = LSApplication.getInstance().getGson().toJson(uizaTracking);
        UZServiceV1 service = RestClientTracking.createService(UZServiceV1.class);
        subscribe(service.track(uizaTracking), new ApiSubscriber<Object>() {
            @Override
            public void onSuccess(Object tracking) {
                s += "\n\n\n->>>>>>\n\n\n" + LSApplication.getInstance().getGson().toJson(tracking);
                tv.setText(s);
            }

            @Override
            public void onFail(Throwable e) {
                showDialogError("track onFail " + e.getMessage());
            }
        });
    }
}
