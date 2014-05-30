package info.sodapanda.sodaplayer.activities;

import info.sodapanda.sodaplayer.MainActivity;
import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.Status;
import info.sodapanda.sodaplayer.events.AddPrivateItemEvent;
import info.sodapanda.sodaplayer.events.BeForbidEvent;
import info.sodapanda.sodaplayer.events.ChangeMoneyEvent;
import info.sodapanda.sodaplayer.events.ChatClkEvent;
import info.sodapanda.sodaplayer.events.MenuClked;
import info.sodapanda.sodaplayer.events.MuchGiftEvent;
import info.sodapanda.sodaplayer.events.NetworkConnEvent;
import info.sodapanda.sodaplayer.events.NetworkDisconnEvent;
import info.sodapanda.sodaplayer.events.OnPlayWeiVideo;
import info.sodapanda.sodaplayer.events.OnstopWeiVideo;
import info.sodapanda.sodaplayer.events.StartPlayEvent;
import info.sodapanda.sodaplayer.events.StopPlayEvent;
import info.sodapanda.sodaplayer.events.ToChatEvent;
import info.sodapanda.sodaplayer.events.ToGiftEvent;
import info.sodapanda.sodaplayer.pojo.GiftItem;
import info.sodapanda.sodaplayer.pojo.LogedUser;
import info.sodapanda.sodaplayer.pojo.MuchGift;
import info.sodapanda.sodaplayer.receivers.NetworkChangeReceiver;
import info.sodapanda.sodaplayer.socket.BusProvider;
import info.sodapanda.sodaplayer.socket.ChatHandler;
import info.sodapanda.sodaplayer.socket.Client;
import info.sodapanda.sodaplayer.socket.ClientFactory;
import info.sodapanda.sodaplayer.socket.ServerInfo;
import info.sodapanda.sodaplayer.socket.out.PubNoDChatMessage;
import info.sodapanda.sodaplayer.utils.RoomLvToResId;
import info.sodapanda.sodaplayer.utils.SmileyParser;
import info.sodapanda.sodaplayer.views.GiftAnimSurfaceView;
import info.sodapanda.sodaplayer.views.RoomMenuView;
import info.sodapanda.sodaplayer.views.WeiVideoView;
import info.sodapanda.sodaplayer.views.data.PointsInfo;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.TabPageIndicator;

public class ChatRoomActivity extends MainActivity {
	// TODO UI动画效果
	// TODO 暂停按钮
	// TODO 直播时间和预告时间
	// TODO 表情菜单收起

	public static final int TYPE_LIVE = 1;
	public static final int TYPE_HTTP = 2;
	public static final String ENTERTYPE = "entertype";

	public static final String TAG = "ChatRoomActivity";
	private LayoutInflater inflater;

	private ChatContentFragment chatContentFragment = new ChatContentFragment();
	private AudienceFragment audienceFragment = new AudienceFragment();
	private VideoListFragment videoListFragment = new VideoListFragment();
	private ChatRoomMoreFragment chatRoomMoreFragment = new ChatRoomMoreFragment();
	private Bus bus = BusProvider.getBus();
	private CheckBox prichat_checkbox;
	private LinearLayout emo_layout;
	private Client client;
	private ChatStatus chatStatus = new ChatStatus();
	private EditText chat_edit;
	private InputMethodManager imm;
	private ImageView pri_dot_notify;
	private FrameLayout noshow_pic_holder;
	private ImageView no_show_pic;

	private View selectToView;
	private SelectToAdapter selectToAdapter;
	private PopupWindow selectTopop;

	private View select_to_layout;
	private TextView chat_to_nick;
	private ListView selectToList;
	private ViewGroup chat_room_layout_root;

	private WeiVideoView weivideo;
	private ArrayList<ChatUser> chatUserList = new ArrayList<ChatRoomActivity.ChatUser>();

	// 礼物部分
	private PopupWindow giftPop;

	private int screen_width;
	private int screen_height;

	private int enter_type = TYPE_HTTP;
    View activityRootView;
    
    private Handler handler = new Handler();

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		bus.register(this);
		inflater = getLayoutInflater();
		Intent intent = getIntent();
		int enterType = intent.getIntExtra(ENTERTYPE, -1);
		if (enterType == -1 || enterType == TYPE_LIVE) {
			enter_type = TYPE_LIVE;
		} else {
			enter_type = TYPE_HTTP;
		}

		setContentView(R.layout.chatroom_activity_layout);

		initInputMethod();
		findViews();

		initViewPagers();
		initPriChatCheckbox();

		initEditText();
		initEmoLayout();

		initWeiView();
		setVideoSurface();

		connectServer();

		// 1. 把主播放入chatuserlist的前端
		initChatUserList();
		// 2. 创建聊天选人的adapter
		initSelectToPop();
		initSelect_to_layout();

		initMenuPop();

		initGiftViews();
		// 3. 创建送礼选人的adapter
		initGiftToPop();
		updateGiftList();

		initGiftCountPop();

		initGiftAnimView();

		initInfoPop();
        activityRootView = findViewById(R.id.chat_room_layout_root);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cutDown();
		bus.unregister(this);
		stop();
		Status.clearData();
	}

	@Override
	protected void onStop() {
		super.onStop();
		infoPop.dismiss();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setgiftbar_money(giftbar_money);

		IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(networkChangeReceiver, intentFilter);

        client = ClientFactory.getInstance().getCurrent();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(networkChangeReceiver);
	}

	// ==============
	// View初始化设置
	// ==============
	private void findViews() {
		pri_dot_notify = (ImageView) findViewById(R.id.pri_dot_notify);
		chat_to_nick = (TextView) findViewById(R.id.chat_to_nick);
		chat_room_layout_root = (ViewGroup) findViewById(R.id.chat_room_layout_root);
	}

	private ViewPager pager;

	/**
	 * viewpager的配置
	 */
	private void initViewPagers() {
		pager = (ViewPager) findViewById(R.id.chat_room_pager);
		TabPageIndicator chat_room_indicator = (TabPageIndicator) findViewById(R.id.chat_room_indicator);
		ChatRoomPagerAdapter pagerAdapter = new ChatRoomPagerAdapter(getSupportFragmentManager());
		pager.setOffscreenPageLimit(4);
		pager.setAdapter(pagerAdapter);
		chat_room_indicator.setViewPager(pager);

		final View chat_bar = findViewById(R.id.chat_bar);

		chat_room_indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					chat_bar.setVisibility(View.VISIBLE);
				} else {
					chat_bar.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int position) {

			}
		});
	}

	/**
	 * 私聊选框的设置
	 */
	private void initPriChatCheckbox() {
		prichat_checkbox = (CheckBox) findViewById(R.id.prichat_checkbox);
		prichat_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!LogedUser.isLoged()) {
					jumpToLog();
				}
				Log.i("test", "选框变化" + isChecked);
				if (isChecked) {
					chatContentFragment.setPriVisibal(View.VISIBLE);
					chatStatus.setPrivate();
					if (chatStatus.getToId().equals("0")) {// 没有选人，自动改为主播
						bus.post(new ToChatEvent(Status.getRoomInfo().getNickname(), Status.getRoomInfo().getuserid()));
					}
					pri_dot_notify.setVisibility(View.INVISIBLE);
				} else {
					chatContentFragment.setPriVisibal(View.GONE);
					chatStatus.setPublic();
				}
			}
		});
	}

	/**
	 * 初始化聊天内容窗口
	 */
	private void initEditText() {
		chat_edit = (EditText) findViewById(R.id.chat_edit);
		chat_edit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					sendMes();
					handled = true;
				}
				return handled;
			}
		});
	}

	/**
	 * 初始化表情列表
	 */
	private void initEmoLayout() {
		emo_layout = (LinearLayout) findViewById(R.id.emo_layout);
		GridView emogridview = (GridView) emo_layout.findViewById(R.id.emogridview);
		final String[] emoList = getResources().getStringArray(R.array.default_smiley_texts);
		EmoAdapter adapter = new EmoAdapter(emoList);
		emogridview.setAdapter(adapter);

		emogridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String emotxt = emoList[position];
				if (position < 40) {// 普通表情
					chat_edit.append(emotxt);
				} else {
					if (LogedUser.getVip() == 1 || LogedUser.getVip() == 2) {
						chat_edit.append(emotxt);
					} else {
						Toast.makeText(ChatRoomActivity.this, "高级表情仅限VIP会员使用", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	private void initInputMethod() {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}


	/**
	 * 设置微视频播放器
	 */
	private void initWeiView() {
		FrameLayout video_holder = (FrameLayout) findViewById(R.id.weivideo_holder);
		weivideo = new WeiVideoView(this);
		video_holder.addView(weivideo);
	}

	/**
	 * 设置播放界面
	 */
	private void setVideoSurface() {
		FrameLayout video_holder = (FrameLayout) findViewById(R.id.video_holder);
		video_holder.addView(player_surface);

		noshow_pic_holder = (FrameLayout) findViewById(R.id.noshow_pic_holder);

		Display display = getWindowManager().getDefaultDisplay();
		screen_width = display.getWidth();
		screen_height = display.getHeight();

		no_show_pic = new ImageView(this);
		no_show_pic.setLayoutParams(new LayoutParams(screen_width, (int) (screen_width * 0.75)));
		no_show_pic.setScaleType(ScaleType.CENTER_CROP);
		no_show_pic.setImageResource(R.drawable.qc_no_video_pic);
		no_show_pic.setVisibility(View.INVISIBLE);
		noshow_pic_holder.addView(no_show_pic);

		if (enter_type == TYPE_HTTP) {
			pager.setCurrentItem(2);
		}
		if (!Status.getRoomInfo().getIsShowing()) {
			no_show_pic.setVisibility(View.VISIBLE);
		} else {
			startPlay(new StartPlayEvent());
		}

	}

	/**
	 * 点击选人的弹出窗口
	 */
	private void initSelectToPop() {
		selectToView = inflater.inflate(R.layout.select_to_pop_layout, chat_room_layout_root, false);
		selectTopop = new PopupWindow(selectToView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		selectTopop.setTouchable(true);
		selectTopop.setFocusable(true);
		selectTopop.setOutsideTouchable(true);
		selectTopop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
		selectToList = (ListView) selectToView.findViewById(R.id.select_to_list);
		selectToAdapter = new SelectToAdapter(1);
		selectToList.setAdapter(selectToAdapter);
	}

	/**
	 * 选择聊天接收者的textview
	 */
	private void initSelect_to_layout() {
		select_to_layout = findViewById(R.id.select_to_layout);
		select_to_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!LogedUser.isLoged()) {
					jumpToLog();
					return;
				}

				int[] location = new int[2];
				select_to_layout.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				int y_bottom = screen_height - y;

				selectTopop.showAtLocation(chat_room_layout_root, Gravity.BOTTOM | Gravity.LEFT, x, y_bottom);

			}
		});
	}

	private GiftAnimSurfaceView giftAnimSurfaceView;

	/**
	 * 初始化大量礼物组合动画的界面
	 */
	private void initGiftAnimView() {
		FrameLayout much_gift_holder = (FrameLayout) findViewById(R.id.much_gift_holder);
		int viewHeight = (int) (screen_width * 0.75f);
		giftAnimSurfaceView = new GiftAnimSurfaceView(this);
		giftAnimSurfaceView.setLayoutParams(new LayoutParams(screen_width, viewHeight));
		much_gift_holder.addView(giftAnimSurfaceView);
	}

	@Subscribe
	public void onMuchGift(MuchGiftEvent e) {
		String picUrl = e.getPic();
		final PointsInfo info = e.getPointsInfo();
		new AsyncHttpClient().get(picUrl, new BinaryHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] b) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
				giftAnimSurfaceView.addAnim(new MuchGift(bitmap, info));
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

			}

		});

	}

	// =====================
	// 点击事件设置
	// =====================
	/**
	 * 点击发送按钮
	 *
	 * @param v
	 */
	public void on_clk_send(View v) {
		if (!LogedUser.isLoged()) {
			jumpToLog();
			return;
		}
		sendMes();
	}

	/**
	 * 点击表情按钮
	 *
	 * @param v
	 */
	public void emo_icon_click(View v) {
		if (!LogedUser.isLoged()) {
			jumpToLog();
			return;
		}
		if (emo_layout.getVisibility() == View.VISIBLE) {
			emo_layout.setVisibility(View.GONE);
            removeOnGlobalLayoutListener(activityRootView,onGlobalLayoutListener);
		} else {
            emo_layout.setVisibility(View.VISIBLE);
            //这里有返回值 又可以回调,如果原来键盘没有显示 则返回值 false,不调用回调了
            boolean isOk =imm.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    Log.d("onKeyBoard", "" + resultCode);
                    if (resultCode == InputMethodManager.RESULT_HIDDEN) {
                        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
                    }
                }
            });
            //键盘本身是隐藏的 则hideSoftInputFromWindow没有回调...
            if(!isOk){
                activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
            }
		}
	}

    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
            Log.d("onKeyBoard","show:"+heightDiff);
            if (heightDiff > 100) {
                if(emo_layout.getVisibility()==View.VISIBLE) {
                    emo_layout.setVisibility(View.GONE);
                    removeOnGlobalLayoutListener(activityRootView,onGlobalLayoutListener);
                }
            }
        }
    };
    private void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
        	//TODO 直播间removeOnGlobalLayoutListener的APIlevel问题
//            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

	public void onClickFinish(View v) {
		stop();
		finish();
	}

	// =======
	// 礼物部分
	// =======
	private GridView tuijian_grid;
	private GridView xingyun_grid;
	private GridView shehua_grid;
	private GridView beibao_grid;

	private ArrayList<GiftItem> tuijian_list = new ArrayList<GiftItem>();
	private ArrayList<GiftItem> xingyun_list = new ArrayList<GiftItem>();
	private ArrayList<GiftItem> shehua_list = new ArrayList<GiftItem>();
	private ArrayList<GiftItem> beibao_List = new ArrayList<GiftItem>();

	GiftGridAdapter tuijianAdapter = new GiftGridAdapter(tuijian_list);
	GiftGridAdapter xingyunAdapter = new GiftGridAdapter(xingyun_list);
	GiftGridAdapter shehuaAdapter = new GiftGridAdapter(shehua_list);
	GiftGridAdapter beibaoAdapter = new GiftGridAdapter(beibao_List);

	GiftStatus giftStatus;

	private View gift_pop_layout;
	private TextView giftbar_money;

	/**
	 * 初始化与礼物有关的view
	 */
	private void initGiftViews() {
		String[] tabTitles = new String[] { "热门", "趣味","特殊", "背包" };
		int[] tabIds = new int[] { R.id.tuijian_tab, R.id.xingyun_tab,R.id.shehua_tab,R.id.beibao_tab };
		gift_pop_layout = inflater.inflate(R.layout.gift_pop_layout, chat_room_layout_root, false);
		giftbar_money = (TextView) gift_pop_layout.findViewById(R.id.giftbar_money);
		setgiftbar_money(giftbar_money);
		initFillMoneyButton();

		TabHost gift_tabhost = (TabHost) gift_pop_layout.findViewById(R.id.gift_tabhost);
		gift_tabhost.setup();

		tuijian_grid = (GridView) gift_pop_layout.findViewById(R.id.tuijian_tab);
		xingyun_grid = (GridView) gift_pop_layout.findViewById(R.id.xingyun_tab);
		shehua_grid = (GridView) gift_pop_layout.findViewById(R.id.shehua_tab);
		beibao_grid = (GridView) gift_pop_layout.findViewById(R.id.beibao_tab);

		for (int i = 0; i < tabTitles.length; i++) {
			View indicator = inflater.inflate(R.layout.gift_tab_item, null);
			TextView tabTitle = (TextView) indicator.findViewById(R.id.tab_title);
			tabTitle.setText(tabTitles[i]);
			gift_tabhost.addTab(gift_tabhost.newTabSpec(tabTitles[i]).setIndicator(indicator).setContent(tabIds[i]));
		}

		giftPop = new PopupWindow(gift_pop_layout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		giftPop.setTouchable(true);
		giftPop.setOutsideTouchable(true);
		giftPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
		giftPop.setAnimationStyle(R.style.pop_anim_style);

		tuijian_grid.setAdapter(tuijianAdapter);
		xingyun_grid.setAdapter(xingyunAdapter);
		shehua_grid.setAdapter(shehuaAdapter);
		beibao_grid.setAdapter(beibaoAdapter);

		gift_to_nick_text = (TextView) gift_pop_layout.findViewById(R.id.gift_to_nick_text);
		gift_count_text = (TextView) gift_pop_layout.findViewById(R.id.gift_count_text);
		final View giftbar_sendto = gift_pop_layout.findViewById(R.id.giftbar_sendto);
		giftbar_sendto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int[] location = new int[2];
				giftbar_sendto.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				int y_bottom = screen_height - y;

				giftToPop.showAtLocation(chat_room_layout_root, Gravity.BOTTOM | Gravity.LEFT, x, y_bottom);
			}
		});

		final View giftbar_count = gift_pop_layout.findViewById(R.id.giftbar_count);
		giftbar_count.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int[] location = new int[2];
				giftbar_count.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				int y_bottom = screen_height - y;

				giftCountPop.showAtLocation(chat_room_layout_root, Gravity.BOTTOM | Gravity.LEFT, x, y_bottom);
			}
		});

		giftPop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				giftStatus.clearStatus();
			}
		});
		Button giftbar_send_btn = (Button) gift_pop_layout.findViewById(R.id.giftbar_send_btn);
		giftbar_send_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				clickSendGiftBtn();
			}
		});

		giftStatus = new GiftStatus();
	}

	private void initFillMoneyButton(){
		View gift_bar_money = gift_pop_layout.findViewById(R.id.gift_bar_money);
		gift_bar_money.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jumpToPay();
			}
		});
	}

	private void setgiftbar_money(TextView moneyView) {
		moneyView.setText(LogedUser.getMoney() + "");
	}

	/**
	 * 点击赠送按钮之后
	 */
	private void clickSendGiftBtn() {
//		if (giftStatus.check()) {
//			HttpApi.sendGift(TAG, Status.getRoomInfo().getArchivesId() + "", LogedUser.getUser_id() + "",
//					giftStatus.getTo_id() + "", giftStatus.getGift_id() + "", giftStatus.getCount() + "",
//					giftStatus.getType(), new QcJsonRh() {
//
//						@Override
//						public void onResponse(JSONObject jsonMsg) {
//							giftPop.dismiss();
//							Log.i("test", "送礼 " + jsonMsg);
//							String code = jsonMsg.optString("code");
//							String msg = jsonMsg.optString("msg");
//							if (!code.equals("200")) {
//								Toast.makeText(ChatRoomActivity.this, msg, Toast.LENGTH_LONG).show();
//								return;
//							}
//							JSONObject data = jsonMsg.optJSONObject("data");
//							int pipiegg = data.optInt("pipiegg");
//
//							giftbar_money.setText(pipiegg+"");
//							LogedUser.setMoney(pipiegg);
//							bus.post(new ChangeMoneyEvent(pipiegg+""));
//						}
//					});
//		}
	}

	public void onClickGiftBtn(View v) {
		if (!LogedUser.isLoged()) {
			jumpToLog();
			return;
		}
		giftPop.showAtLocation(chat_room_layout_root, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
		initBagList();
	}

	private PopupWindow giftToPop;
	private View giftToPopView;
	private SelectToAdapter giftToadapter;

	/**
	 * 初始化送礼对象弹出窗口
	 */
	private void initGiftToPop() {
		giftToPopView = inflater.inflate(R.layout.select_to_pop_layout, chat_room_layout_root, false);
		giftToPop = new PopupWindow(giftToPopView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		giftToPop.setTouchable(true);
		giftToPop.setOutsideTouchable(true);
		giftToPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
		ListView giftToListView = (ListView) giftToPopView.findViewById(R.id.select_to_list);
		giftToadapter = new SelectToAdapter(2);
		giftToListView.setAdapter(giftToadapter);
	}

	private PopupWindow giftCountPop;
	private View giftCountPopView;
	private ArrayAdapter<String> giftCountAdapter;
	private TextView gift_count_text;

	/**
	 * 选择数量的礼物菜单
	 */
	private void initGiftCountPop() {
		final String[] giftCountArray = getResources().getStringArray(R.array.gift_count_array);
		giftCountAdapter = new ArrayAdapter<String>(this, R.layout.select_to_item, R.id.to_nickname, giftCountArray);
		giftCountPopView = inflater.inflate(R.layout.select_to_pop_layout, chat_room_layout_root, false);
		ListView giftCountListView = (ListView) giftCountPopView.findViewById(R.id.select_to_list);
		giftCountListView.setAdapter(giftCountAdapter);
		giftCountPop = new PopupWindow(giftCountPopView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		giftCountPop.setTouchable(true);
		giftCountPop.setOutsideTouchable(true);
		giftCountPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

		final int[] giftCountNumArray = getResources().getIntArray(R.array.gift_count_num_array);
		giftCountListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int count = giftCountNumArray[position];
				giftStatus.setCount(count);
				gift_count_text.setText(giftCountArray[position]);
				giftCountPop.dismiss();
			}
		});

	}

	private TextView gift_to_nick_text;

	/**
	 * 收到选择送礼对象的事件
	 *
	 * @param e
	 */
	@Subscribe
	public void onSendGift(ToGiftEvent e) {
		if (!giftPop.isShowing()) {
			onClickGiftBtn(new View(this));
		}
		addChatUser(new ChatUser(e.getUid(), e.getNickName()));
		gift_to_nick_text.setText(e.getNickName());
		giftStatus.setTo_user(e.getNickName(), e.getUid());
	}

	/**
	 * 下载礼物列表
	 */
	private void updateGiftList() {
//		HttpApi.getGiftList(TAG, new QcJsonRh() {
//
//			@Override
//			public void onResponse(JSONObject jsonMsg) {
//				Log.i("test", "礼物 " + jsonMsg);
//				String code = jsonMsg.optString("code");
//				if (!code.equals("200"))
//					return;
//
//				JSONObject data = jsonMsg.optJSONObject("data");
//				JSONArray list = data.optJSONArray("list");
//
//				for (int i = 0; i < list.length(); i++) {
//					JSONObject thisGift = list.optJSONObject(i);
//					String zh_name = thisGift.optString("zh_name");
//					int gift_id = thisGift.optInt("gift_id");
//					String cat_name = thisGift.optString("cat_name");
//					int pipiegg = thisGift.optInt("pipiegg");
//					String image = thisGift.optString("image");
//					GiftItem giftItem = new GiftItem(image, zh_name, pipiegg + "", gift_id);
//					if (cat_name.equals("推荐") || cat_name.equals("热门")) {
//						tuijian_list.add(giftItem);
//					}
//					if (cat_name.equals("幸运") || cat_name.equals("趣味")) {
//						xingyun_list.add(giftItem);
//					}
//					if (cat_name.equals("特殊")) {
//						shehua_list.add(giftItem);
//					}
//				}
//				tuijianAdapter.notifyDataSetChanged();
//				xingyunAdapter.notifyDataSetChanged();
//				shehuaAdapter.notifyDataSetChanged();
//			}
//		});
	}

	/**下载用户背包数据 */
	private void initBagList(){
//		beibao_List.clear();
//		HttpApi.getUserBag(LogedUser.getUser_id()+"", new QcJsonRh() {
//
//			@Override
//			public void onResponse(JSONObject jsonMsg) {
//				JSONObject data = jsonMsg.optJSONObject("data");
//				if(data == null){
//					return;
//				}
//				JSONArray list = data.optJSONArray("list");
//
//				for (int i = 0; i < list.length(); i++) {
//					JSONObject giftItem = list.optJSONObject(i);
//					int gift_id = giftItem.optInt("gift_id");
//					int num = giftItem.optInt("num");
//					String zh_name = giftItem.optString("zh_name");
//					String image = giftItem.optString("image");
//					GiftItem thisGiftItem = new GiftItem(image, zh_name, num+"", gift_id);
//					beibao_List.add(thisGiftItem);
//				}
//			}
//		});
//		for (int i = 0 ;i< beibao_List.size();i++){
//			Log.i("test","背包 "+beibao_List.get(i).getGift_name());
//		}
//		beibaoAdapter.notifyDataSetChanged();
	}

	class GiftGridAdapter extends BaseAdapter {
		private ArrayList<GiftItem> giftItemList = new ArrayList<GiftItem>();

		public GiftGridAdapter(ArrayList<GiftItem> list) {
			giftItemList = list;
		}

		@Override
		public int getCount() {
			return giftItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return giftItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, final ViewGroup parent) {
			final GiftItem thisGift = giftItemList.get(position);

			final View itemView = inflater.inflate(R.layout.gift_item_layout, parent, false);
			ImageView gift_img = (ImageView) itemView.findViewById(R.id.gift_img);
			TextView gift_name = (TextView) itemView.findViewById(R.id.gift_name);
			TextView gift_price = (TextView) itemView.findViewById(R.id.gift_price);

			Picasso.with(ChatRoomActivity.this).load(thisGift.getGift_img_url()).into(gift_img);
			gift_name.setText(thisGift.getGift_name());
			gift_price.setText(thisGift.getGift_price());

			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					giftStatus.setGift(v, thisGift.getGiftId(), thisGift.getGift_name());

					if(parent == beibao_grid){
						giftStatus.setType("bag");
					}
				}
			});

			return itemView;
		}

	}

	/**
	 * 送礼选择过程变量
	 *
	 * @author qishui
	 *
	 */
	class GiftStatus {
		public void setTo_user(String to_nickname, int to_id) {
			this.to_nickname = to_nickname;
			this.to_uid = to_id;
		}

		public GiftStatus(){
			clearStatus();
		}

		public void setCount(int count) {
			this.count = count;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setGift(View giftView, int gift_id, String gift_name) {
			if (this.clickedGiftItem != null) {
				this.clickedGiftItem.setSelected(false);
			}

			giftView.setSelected(true);
			this.gift_id = gift_id;
			this.gift_name = gift_name;
			this.clickedGiftItem = giftView;
		}

		public int getTo_id() {
			return this.to_uid;
		}

		public int getGift_id() {
			return this.gift_id;
		}

		public int getCount() {
			return this.count;
		}

		public String getType() {
			return this.type;
		}

		private String to_nickname;
		private int to_uid;
		private int count;
		private int gift_id;
		private String gift_name;
		private View clickedGiftItem;
		private String type = "common";

		public boolean check() {
			if (to_nickname == null || to_nickname.equals("")) {
				Toast.makeText(ChatRoomActivity.this, "请选择接收者", Toast.LENGTH_SHORT).show();
				return false;
			}
			if (count == 0) {
				Toast.makeText(ChatRoomActivity.this, "请选择数量", Toast.LENGTH_SHORT).show();
				return false;
			}
			if (gift_name == null || gift_name.equals("")) {
				Toast.makeText(ChatRoomActivity.this, "请选择礼物", Toast.LENGTH_SHORT).show();
				return false;
			}
			return true;
		}

		/**
		 * 清除选择状态
		 */
		public void clearStatus() {
			this.gift_id = -1;
			this.gift_name = null;
			this.to_nickname = Status.getRoomInfo().getNickname();
			this.to_uid =Status.getRoomInfo().getuserid();
			this.count = 1;
			gift_to_nick_text.setText(to_nickname);
			gift_count_text.setText(count+"");
		}

	}

	// ==========
	// 点击昵称弹出的菜单
	// =========

	private PopupWindow menuPop;
	private RoomMenuView menuPopView;

	/**
	 * 初始化房间内弹出的菜单
	 */
	private void initMenuPop() {
		menuPopView = new RoomMenuView(this);
		menuPop = new PopupWindow(menuPopView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		menuPop.setTouchable(true);
		menuPop.setOutsideTouchable(true);
		menuPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

	}

	@Subscribe
	public void onNickClk(ChatClkEvent e) {
		if (!LogedUser.isLoged()) {
			jumpToLog();
			return;
		}
		menuPopView.startMenu(e.getNick_name(), e.getUserid());
		if (!menuPop.isShowing()) {
			int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			menuPopView.measure(w, h);
			int height = menuPopView.getMeasuredHeight();
			Log.i("test", "弹出来的菜单的高度 " + height);

			menuPop.showAsDropDown(select_to_layout, 0, 0 - height);
		}
	}

	// ==========
	// 底部弹出窗口
	// ==========
	private PopupWindow infoPop;
	private View infoPopLayout;
	private View chat_title_bar;
	private View star_info_layout;

	/**
	 * 初始化底部弹出的房间信息窗口
	 */
	private void initInfoPop() {
		infoPopLayout = inflater.inflate(R.layout.chat_popinfo_layout, chat_room_layout_root, false);
		infoPop = new PopupWindow(infoPopLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		infoPop.setFocusable(false);
		infoPop.setOutsideTouchable(true);
		infoPop.setTouchable(true);
		infoPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
		infoPop.setAnimationStyle(R.style.pop_anim_style);

		chat_title_bar = findViewById(R.id.chat_title_bar);
		star_info_layout = infoPopLayout.findViewById(R.id.star_info_layout);

		infoPop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						infoPopIsShowing = false;
						chat_title_bar.setVisibility(View.GONE);
					}
				}, 300);
			}
		});
		View show_info_layout = infoPopLayout.findViewById(R.id.show_info_layout);
		if (Status.getRoomInfo().getIsShowing()) {
			show_info_layout.setVisibility(View.VISIBLE);
		} else {
			show_info_layout.setVisibility(View.GONE);
		}

		initChatTitleBar();

		star_info_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//TODO 从chatroom跳转到各种主页
//				UserData user = new UserData();
//				user.setUid(Status.getRoomInfo().getuserid());
//				Intent intent = new Intent(ChatRoomActivity.this, UserPageActivity.class);
//				intent.putExtra("UserData", user);
//				startActivity(intent);
			}
		});
	}

	private boolean infoPopIsShowing = false;

	public void onPlayClick(View v) {
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		if (infoPopIsShowing) {
			return;
		}
		infoPop.showAtLocation(chat_room_layout_root, Gravity.BOTTOM | Gravity.LEFT, 0, 0);
		infoPopIsShowing = true;
		chat_title_bar.setVisibility(View.VISIBLE);

		final ImageView star_avatar = (ImageView) infoPopLayout.findViewById(R.id.star_avatar);
		final TextView star_name = (TextView) infoPopLayout.findViewById(R.id.star_name);
		final TextView star_touxian_text = (TextView) infoPopLayout.findViewById(R.id.star_touxian_text);
		final ImageView star_lv_icon = (ImageView) infoPopLayout.findViewById(R.id.star_lv_icon);
		final TextView user_id = (TextView) infoPopLayout.findViewById(R.id.user_id);
		final View star_touxian = infoPopLayout.findViewById(R.id.star_touxian);
		final TextView show_description = (TextView) infoPopLayout.findViewById(R.id.show_description);
		final TextView show_time_text = (TextView) infoPopLayout.findViewById(R.id.show_time_text);
		final TextView all_time_text = (TextView) infoPopLayout.findViewById(R.id.all_time_text);

//		HttpApi.getUserInfo(TAG, Status.getRoomInfo().getuserid(), new QcStringRh() {
//
//			@Override
//			public void onResponse(String response) {
//				try {
//					JSONObject jsonMsg = new JSONObject(response);
//					String code = jsonMsg.optString("code");
//					if (!code.equals("200")) {
//						return;
//					}
//					JSONObject data = jsonMsg.optJSONObject("data");
//					String nickname = data.optString("nickname");
//					String middle_avatar = data.optString("middle_avatar");
//					int dotey_rank = data.optInt("dotey_rank");
//					int uid = data.optInt("uid");
//					String auth_title = data.optString("auth_title");
//
//					Picasso.with(ChatRoomActivity.this).load(middle_avatar).into(star_avatar);
//					star_name.setText(nickname);
//					if (auth_title == null || auth_title.equals("") || auth_title.equals("null")
//							|| auth_title.equals("无")) {
//						star_touxian.setVisibility(View.GONE);
//					} else {
//						star_touxian_text.setText(auth_title);
//					}
//
//					star_lv_icon.setBackgroundResource(RoomLvToResId.getLvIconResId(dotey_rank + ""));
//					user_id.setText(uid + "");
//					show_description.setText(Status.getRoomInfo().getTitle());
//					show_time_text.setText("已开播：" + Status.getRoomInfo().getlive_date());
////					all_time_text.setText("结束时间 " + Status.getRoomInfo().getend_time());
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});

	}

	private View chat_iv_star;

	/**
	 * 初始化顶部弹出界面
	 */
	private void initChatTitleBar() {
		chat_iv_star = chat_title_bar.findViewById(R.id.chat_iv_star);
//
//		HttpApi.getUserIsAttention(TAG, LogedUser.getUser_id(), Status.getRoomInfo().getuserid(), new QcStringRh() {
//
//			@Override
//			public void onResponse(String msg) {
//				try {
//					JSONObject jsonMsg = new JSONObject(msg);
//					Log.i("test", "是否关注 " + jsonMsg.toString());
//					JSONObject data = jsonMsg.getJSONObject("data");
//					int attention = data.optInt("attention");
//					if (attention == 0) {// 没有关注
//						chat_iv_star.setSelected(false);
//					} else {
//						chat_iv_star.setSelected(true);
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//					return;
//				}
//
//			}
//		});

//		chat_iv_star.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				if(!LogedUser.isLoged()){
//					jumpToLog();
//				}
//
//				boolean isFollowd = v.isSelected();
//				if (isFollowd) {// 如果已经关注了则取消关注
//					HttpApi.removeAttention(TAG, LogedUser.getUser_id(), Status.getRoomInfo().getuserid(),
//							new QcStringRh() {
//
//								@Override
//								public void onResponse(String response) {
//									try {
//										JSONObject jsonMsg = new JSONObject(response);
//										String msg = jsonMsg.optString("msg");
//										if (msg.equals("success")) {
//											chat_iv_star.setSelected(false);
//											Toast.makeText(ChatRoomActivity.this, "取消关注成功", Toast.LENGTH_SHORT).show();
//										}
//									} catch (JSONException e) {
//										e.printStackTrace();
//									}
//								}
//							});
//				} else {// 如果没有关注 则关注
//					HttpApi.addAttention(TAG, LogedUser.getUser_id(), Status.getRoomInfo().getuserid(),
//							new QcStringRh() {
//
//								@Override
//								public void onResponse(String response) {
//									try {
//										JSONObject jsonMsg = new JSONObject(response);
//										String msg = jsonMsg.optString("msg");
//										if (msg.equals("success")) {
//											chat_iv_star.setSelected(true);
//											Toast.makeText(ChatRoomActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
//
//										}
//									} catch (JSONException e) {
//										e.printStackTrace();
//									}
//								}
//							});
//					chat_iv_star.setSelected(true);
//				}
//			}
//		});

		// 分享按钮
		ImageView chat_bar_share = (ImageView) chat_title_bar.findViewById(R.id.chat_bar_share);
		chat_bar_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!LogedUser.isLoged()){
					jumpToLog();
				}
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, "奇橙");
				intent.putExtra(Intent.EXTRA_TEXT, "大家好，我正在奇橙上观看" + Status.getRoomInfo().getNickname()
						+ "的精彩直播，大家快来围观！下载地址http://qicheng.tv");
				startActivity(intent);
			}
		});
	}

	// ===================
	// 网络操作
	// ===================
	/**
	 * 连接服务器
	 */
	private void connectServer() {
		final String serverAddr = Status.getRoomInfo().getDomain();
		final int port = Status.getRoomInfo().getPort();

		client = ClientFactory.getInstance().getClient(new ServerInfo(serverAddr,port));
		client.connect( new ChatHandler() {

			@Override
			public void handle(boolean isSuccess) {
				if (!isSuccess) {
					Toast.makeText(ChatRoomActivity.this, "连接失败", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	/**
	 * 断开服务器连接
	 */
	private void cutDown() {
		client.disconnect();
	}

	/**
	 * 发送消息
	 */
	private void sendMes() {
		String content = chat_edit.getText().toString();
		if (content == null || content.equals("")) {
			return;
		}
		String to_id = chatStatus.getToId();
		String to_nickname = chatStatus.getToNickName();
		String type = chatStatus.getType();

		if (type.equals("private") && to_id.equals("0")) {// 设置为私聊 但是没有选人
			// 自动选为主播
		}

		PubNoDChatMessage msg = new PubNoDChatMessage(content, Status.getRoomInfo().getArchivesId() + "", Status
				.getRoomInfo().getDomain(), LogedUser.getUser_id() + "", LogedUser.getUser_name(), to_id, to_nickname,
				type);
		client.send(msg);

		chat_edit.setText("");

		// imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
		// InputMethodManager.HIDE_NOT_ALWAYS);
		imm.hideSoftInputFromWindow(chat_edit.getWindowToken(), 0);
		if (emo_layout.getVisibility() == View.VISIBLE) {
			emo_layout.setVisibility(View.GONE);
		}
	}

	// ==========
	// 事件总线消息
	// ==========
	/**
	 * 收到私聊消息 打开指示灯
	 *
	 * @param event 私聊消息事件
	 */
	@Subscribe
	public void onPrivateMsg(AddPrivateItemEvent event) {
		if (!prichat_checkbox.isChecked()) {
			pri_dot_notify.setVisibility(View.VISIBLE);
		}
	}

	@Subscribe
	public void startPlay(StartPlayEvent e) {
		if (weivideo.getVisibility() == View.VISIBLE) {
			return;
		}

		no_show_pic.setVisibility(View.INVISIBLE);
		ArrayList<String> rtmpUrl = Status.getRoomInfo().getRtmpRrlList();
		startPlayer(rtmpUrl);
	}

	/**
	 * 收到主播停止的消息
	 *
	 * @param event
	 */
	@Subscribe
	public void stopPlay(StopPlayEvent event) {
		Status.getRoomInfo().setIsShowing(false);

		if(no_show_pic!=null){
			no_show_pic.setVisibility(View.VISIBLE);
		}

		stop();
	}

	/**
	 * 接收到要针对某人聊天的事件
	 *
	 * @param event
	 */
	@Subscribe
	public void toChat(ToChatEvent event) {
		pager.setCurrentItem(0);
		if (event.getUserid() == 0) {// 公聊的
			chatStatus.setToUser("0", "");
			prichat_checkbox.setChecked(false);
			chat_to_nick.setText(event.getNickName());
		}

		if (event.getUserid() != 0) {// 有向的
			addChatUser(new ChatUser(event.getUserid(), event.getNickName()));
			chatStatus.setToUser(event.getUserid() + "", event.getNickName());
			chat_to_nick.setText(event.getNickName());
		}
	}

	/**
	 * 开始播放微视频时间
	 *
	 * @param e
	 */
	@Subscribe
	public void onPlayWeiVideo(OnPlayWeiVideo e) {
		String url = e.getVideoUrl();
		stop();
		weivideo.startVideo(url);
	}

	/**
	 * 结束微视频切换到直播
	 *
	 * @param e
	 */
	@Subscribe
	public void onStopWei(OnstopWeiVideo e) {
		weivideo.stopVideo();
		startPlay(new StartPlayEvent());
	}

	public void stopWeiVideo() {
		weivideo.stopVideo();
	}

	@Subscribe
	public void menuCliced(MenuClked e) {
		if (menuPop.isShowing()) {
			menuPop.dismiss();
		}
	}

	/**
	 * 被踢出房间
	 *
	 * @param e
	 */
	@Subscribe
	public void beKicked(BeForbidEvent e) {
		Toast.makeText(this, "被踢出房间", Toast.LENGTH_LONG).show();
		stop();
		finish();
	}

	// ====================
	// 内部类
	// ====================
	/**
	 * view pager的适配器
	 *
	 * @author qishui
	 *
	 */
	class ChatRoomPagerAdapter extends FragmentPagerAdapter {

		public ChatRoomPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return chatContentFragment;
			case 1:
				return audienceFragment;
			case 2:
				return videoListFragment;
			case 3:
				return chatRoomMoreFragment;
			default:
				return null;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "互动";
			case 1:
				return "观众";
			case 2:
				return "视频";
			case 3:
				return "更多";
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 4;
		}
	}

	/**
	 * 聊天配置状态 是否私聊
	 *
	 * @author qishui
	 *
	 */
	class ChatStatus {
		String type = "common";
		String to_id = "0";
		String to_nickname = "";

		public void setPublic() {
			type = "common";
		}

		public void setPrivate() {
			type = "private";
		}

		public void setToUser(String to_id, String to_nickname) {
			this.to_id = to_id;
			this.to_nickname = to_nickname;
		}

		public String getToId() {
			return to_id;
		}

		public String getToNickName() {
			return to_nickname;
		}

		public String getType() {
			return this.type;
		}
	}

	/**
	 * 选择聊天对象的listview的adapter
	 *
	 * @author qishui
	 *
	 */
	class SelectToAdapter extends BaseAdapter {
		int type;// type = 1 聊天 type = 2 送礼

		public SelectToAdapter(int type) {
			this.type = type;
		}

		@Override
		public int getCount() {
			// return chatUserList.size();
			if (type == 1) {
				return chatUserList.size();
			} else {
				return chatUserList.size() - 1;
			}

		}

		@Override
		public Object getItem(int position) {
			return chatUserList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// final WhoToChat thisWho = chatList.get(position);
			ChatUser thisUser = chatUserList.get(position);
			if (type == 1) {
				thisUser = chatUserList.get(position);
			} else {
				thisUser = chatUserList.get(position + 1);
			}

			final ToChatEvent thisEvent = new ToChatEvent(thisUser.getNickName(), thisUser.getUserid());
			final ToGiftEvent giftEvent = new ToGiftEvent(thisUser.getNickName(), thisUser.getUserid());
			View selectToItem = inflater.inflate(R.layout.select_to_item, null, false);
			TextView to_nickname = (TextView) selectToItem.findViewById(R.id.to_nickname);
			to_nickname.setText(thisEvent.getNickName());

			selectToItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (type == 1) {
						bus.post(thisEvent);
						if (selectTopop.isShowing()) {
							selectTopop.dismiss();
						}
					} else {
						bus.post(giftEvent);
						if (giftToPop.isShowing())
							giftToPop.dismiss();
					}

				}
			});

			return selectToItem;
		}

	}

	class ChatUser {
		int userid;
		String nickname;

		public ChatUser(int uid, String name) {
			userid = uid;
			nickname = name;
		}

		public int getUserid() {
			return userid;
		}

		public String getNickName() {
			return nickname;
		}
	}

	/**
	 * 表情选择的adapter
	 *
	 * @author qishui
	 *
	 */
	class EmoAdapter extends BaseAdapter {
		String[] emoList;

		public EmoAdapter(String[] emoList) {
			this.emoList = emoList;
		}

		@Override
		public int getCount() {
			return emoList.length;
		}

		@Override
		public Object getItem(int position) {
			return emoList[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View face_image_view = inflater.inflate(R.layout.face_image_layout, null);
			ImageView face = (ImageView) face_image_view.findViewById(R.id.face_image);
			// face.setImageResource(Smileys.getSmileyResource(position));
			face.setImageResource(SmileyParser.DEFAULT_SMILEY_RES_IDS[position]);
			return face_image_view;
		}
	}

	// ===============

	/**
	 * 初始化选择聊天对象和送礼对象的列表
	 */
	private void initChatUserList() {
		chatUserList.add(new ChatUser(0, "所有人"));
		chatUserList.add(new ChatUser(Status.getRoomInfo().getuserid(), Status.getRoomInfo().getNickname()));
	}

	/**
	 * 通过各种方式向最近联系人列表中加入数据
	 *
	 * @param chatUser
	 */
	private void addChatUser(ChatUser chatUser) {
		for (ChatUser user : chatUserList) {
			if (user.getUserid() == chatUser.getUserid()) {
				return;
			}
		}

		chatUserList.add(2, chatUser);
		selectToAdapter.notifyDataSetChanged();
		giftToadapter.notifyDataSetChanged();
	}

	public void hideMarquee() {
		View top_marquee_backgroud_layout = findViewById(R.id.top_marquee_backgroud_layout);
		top_marquee_backgroud_layout.setVisibility(View.GONE);
	}

	public void showMqrquee() {
		View top_marquee_backgroud_layout = findViewById(R.id.top_marquee_backgroud_layout);
		top_marquee_backgroud_layout.setVisibility(View.VISIBLE);
	}

	// 跳转到登录界面
	public void jumpToLog() {
		//TODO 跳转到登陆界面
//		Intent intent = new Intent(ChatRoomActivity.this, LoginActivity.class);
//		intent.putExtra(LoginHelper.LOG_TYPE, LoginHelper.CHATROOM_LOGIN);
//		startActivity(intent);
	}

	public void jumpToPay() {
		//TODO 跳转到支付界面
//		Intent intent = new Intent(this, PayMethodActivity.class);
//		stop();
//		finish();
//		startActivity(intent);
	}


	//============
	//网络状态监听
	//============

	NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();

	@Subscribe
	public void onNetworkConn(NetworkConnEvent e){
		TextView disconn_text = (TextView) findViewById(R.id.disconn_text);
		disconn_text.setVisibility(View.GONE);
		if(Status.getRoomInfo().getIsShowing()){
			startPlay(new StartPlayEvent());
		}

        //重连聊天
        ClientFactory.startNewClient();
	}

	@Subscribe
	public void onNetWorkDis(NetworkDisconnEvent e){
		TextView disconn_text = (TextView) findViewById(R.id.disconn_text);
		disconn_text.setVisibility(View.VISIBLE);
		stopPlay(new StopPlayEvent());
	}

}
