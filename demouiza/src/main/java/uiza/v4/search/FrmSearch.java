package uiza.v4.search;

/**
 * Created by www.muathu@gmail.com on 12/24/2017.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uiza.R;
import uiza.app.LSApplication;
import uiza.v4.HomeV4CanSlideActivity;
import uiza.v4.entities.EntitiesAdapter;
import vn.uiza.core.base.BaseFragment;
import vn.uiza.core.common.Constants;
import vn.uiza.core.utilities.LLog;
import vn.uiza.core.utilities.LUIUtil;
import vn.uiza.restapi.restclient.UZRestClient;
import vn.uiza.restapi.uiza.UZService;
import vn.uiza.restapi.uiza.model.v3.metadata.getdetailofmetadata.Data;
import vn.uiza.restapi.uiza.model.v3.videoondeman.listallentity.ResultListEntity;
import vn.uiza.rxandroid.ApiSubscriber;
import vn.uiza.utils.util.KeyboardUtils;
import vn.uiza.uzv1.view.IOnBackPressed;
import vn.uiza.uzv3.util.UZUtil;
import vn.uiza.views.LToast;

public class FrmSearch extends BaseFragment implements View.OnClickListener, IOnBackPressed {
    private ImageView ivBack;
    private ImageView ivClearText;
    private EditText etSearch;
    private TextView tv;
    private final int limit = 20;
    private final String orderBy = "createdAt";
    private final String orderType = "DESC";
    private final String publishToCdn = "success";
    private RecyclerView recyclerView;
    private EntitiesAdapter mAdapter;
    private TextView tvMsg;
    private List<Data> dataList = new ArrayList<>();
    private int page = 0;
    private int totalPage = Integer.MAX_VALUE;

    @Override
    protected String setTag() {
        return "Search";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LLog.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) frmRootView.findViewById(R.id.rv);
        recyclerView = (RecyclerView) frmRootView.findViewById(R.id.rv);
        ivBack = (ImageView) view.findViewById(R.id.iv_back);
        ivClearText = (ImageView) view.findViewById(R.id.iv_clear_text);
        etSearch = (EditText) view.findViewById(R.id.et_search);
        etSearch.requestFocus();
        tv = (TextView) view.findViewById(R.id.tv);
        ivBack.setOnClickListener(this);
        ivClearText.setOnClickListener(this);
        mAdapter = new EntitiesAdapter(getActivity(), dataList, new EntitiesAdapter.Callback() {
            @Override
            public void onClick(Data data, int position) {
                UZUtil.setClickedPip(getActivity(), false);
                ((HomeV4CanSlideActivity) getActivity()).playEntityId(data.getId());
            }

            @Override
            public void onLongClick(Data data, int position) {
            }

            @Override
            public void onLoadMore() {
                loadMore();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        LUIUtil.setPullLikeIOSVertical(recyclerView);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    ivClearText.setVisibility(View.GONE);
                    tv.setVisibility(View.GONE);
                } else {
                    ivClearText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        LUIUtil.setImeiActionSearch(etSearch, new LUIUtil.CallbackSearch() {
            @Override
            public void onSearch() {
                search(etSearch.getText().toString());
            }
        });
        KeyboardUtils.showSoftInput(etSearch);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.v4_frm_search;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.iv_clear_text:
                etSearch.setText("");
                break;
        }
    }

    private void search(String keyword) {
        //TODO chia page https://jira.uiza.io/browse/UIZA-3157
        tv.setVisibility(View.GONE);
        page++;
        if (page >= totalPage) {
            if (Constants.IS_DEBUG) {
                LToast.show(getActivity(), getString(R.string.this_is_last_page));
            }
            return;
        }
        if (Constants.IS_DEBUG) {
            LToast.show(getActivity(), getString(R.string.load_page) + page);
        }
        LLog.d(TAG, "search " + page + "/" + totalPage);
        UZService service = UZRestClient.createService(UZService.class);
        subscribe(service.searchEntity(keyword), new ApiSubscriber<ResultListEntity>() {
            @Override
            public void onSuccess(ResultListEntity result) {
                LLog.d(TAG, "onSuccess " + LSApplication.getInstance().getGson().toJson(result));
                if (result == null || result.getData().isEmpty()) {
                    tv.setText(getString(R.string.empty_list));
                    tv.setVisibility(View.VISIBLE);
                    return;
                }
                if (result == null || result.getMetadata() == null || result.getData().isEmpty()) {
                    tvMsg.setVisibility(View.VISIBLE);
                    tvMsg.setText(getString(R.string.empty_list));
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
                LLog.d(TAG, "-> totalPage: " + totalPage);
                dataList.addAll(result.getData());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Throwable e) {
                LLog.e(TAG, "search onFail " + e.toString());
                tv.setText("Error search " + e.getMessage());
                tv.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        LLog.d(TAG, "onBackPressed " + TAG);
        return false;
        //return ((HomeV4CanSlideActivity) getActivity()).handleOnbackpressFrm();
    }

    @Override
    public void onDestroyView() {
        ((HomeV4CanSlideActivity) getActivity()).llActionBar.setVisibility(View.VISIBLE);
        KeyboardUtils.hideSoftInput(getActivity());
        super.onDestroyView();
    }

    private void loadMore() {
        LLog.d(TAG, "loadMore");
        search(etSearch.getText().toString());
    }
}
