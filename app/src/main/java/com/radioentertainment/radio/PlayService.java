package com.radioentertainment.radio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.radioentertainment.asyncTasks.LoadOnDemandViewed;
import com.radioentertainment.asyncTasks.LoadRadioViewed;
import com.radioentertainment.interfaces.RadioViewListener;
import com.radioentertainment.item.ItemRadio;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.MediaButtonIntentReceiver;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.ParserM3UToURL;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import saschpe.exoplayer2.ext.icy.IcyHttpDataSource;
import saschpe.exoplayer2.ext.icy.IcyHttpDataSourceFactory;

public class PlayService extends Service {

    static private final int NOTIFICATION_ID = 119;
    static private PlayService service;
    static private Context context;
    static NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    static ItemRadio itemRadio;
    LoadSong loadSong;
    private Boolean isCanceled = false;
    RemoteViews bigViews, smallViews;
    Methods methods;
    Bitmap bitmap;
    ComponentName componentName;
    AudioManager mAudioManager;

    public static final String ACTION_STOP = "com.prince.viavi.saveimage.action.STOP";
    public static final String ACTION_PLAY = "com.prince.viavi.saveimage.action.PLAY";
    public static final String ACTION_PREVIOUS = "com.prince.viavi.saveimage.action.PREVIOUS";
    public static final String ACTION_NEXT = "com.prince.viavi.saveimage.action.NEXT";
    public static final String ACTION_TOGGLE = "com.prince.viavi.saveimage.action.TOGGLE_PLAYPAUSE";

    public void initialize(Context context, ItemRadio station) {
        PlayService.context = context;
        itemRadio = station;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    static public void initNewContext(Context context) {
        PlayService.context = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static PlayService getInstance() {
        return service;
    }

    public static PlayService createInstance() {
        if (service == null) {
            service = new PlayService();
        }
        return service;
    }

    public Boolean isPlaying() {
        if (service == null) {
            return false;
        } else {
            if (Constants.exoPlayer_Radio != null) {
                return Constants.exoPlayer_Radio.getPlayWhenReady();
            } else {
                return false;
            }
        }
    }

    @Override
    public void onCreate() {
        methods = new Methods(context);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        componentName = new ComponentName(getPackageName(), MediaButtonIntentReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(componentName);

        registerReceiver(onCallIncome, new IntentFilter("android.intent.action.PHONE_STATE"));
        registerReceiver(onHeadPhoneDetect, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        Constants.exoPlayer_Radio = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        Constants.exoPlayer_Radio.addListener(eventListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            switch (intent.getAction()) {
                case ACTION_STOP:
                    stop(intent);
                    break;
                case ACTION_PLAY:
                    newPlay();
                    break;
                case ACTION_TOGGLE:
                    togglePlayPause();
                    break;
                case ACTION_PREVIOUS:
                    if (methods.isConnectingToInternet()) {
                        previous();
                    } else {
                        methods.showToast(getString(R.string.internet_not_connected));
                    }
                    break;
                case ACTION_NEXT:
                    if (methods.isConnectingToInternet()) {
                        next();
                    } else {
                        methods.showToast(getString(R.string.internet_not_connected));
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    private class LoadSong extends AsyncTask<String, Void, Boolean> {

        protected void onPreExecute() {
            loadViewed(Constants.pos);
            ((BaseActivity) context).setBuffer(true);
            ((BaseActivity) context).changeSongName(getString(R.string.unknown_song));
        }

        protected Boolean doInBackground(final String... args) {
            try {
                String url = itemRadio.getRadiourl();
                MediaSource mediaSource;
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), null, icy);

                if (url.contains(".m3u8")) {
                    mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(url));
                } else if (url.contains(".m3u") || url.contains("yp.shoutcast.com/sbin/tunein-station.m3u?id=")) {
                    url = ParserM3UToURL.parse(url, "m3u");

                    mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                            .setExtractorsFactory(new DefaultExtractorsFactory())
                            .createMediaSource(Uri.parse(url));

                } else if (url.contains(".pls") || url.contains("listen.pls?sid=") || url.contains("yp.shoutcast.com/sbin/tunein-station.pls?id=")) {
                    url = ParserM3UToURL.parse(url, "pls");

                    mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                            .setExtractorsFactory(new DefaultExtractorsFactory())
                            .createMediaSource(Uri.parse(url));
                } else {
                    mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                            .setExtractorsFactory(new DefaultExtractorsFactory())
                            .createMediaSource(Uri.parse(url));
                }

                Constants.exoPlayer_Radio.prepare(mediaSource);
                Constants.exoPlayer_Radio.setPlayWhenReady(true);
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (context != null) {
                super.onPostExecute(aBoolean);
                if (!aBoolean) {
                    ((BaseActivity) context).setBuffer(false);
                    methods.showToast(getString(R.string.error_loading_radio));
                }
            }
        }
    }

    Player.EventListener eventListener = new Player.DefaultEventListener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_ENDED && !Constants.playTypeRadio) {
                onCompletion();
            }
            if (playbackState == Player.STATE_READY && playWhenReady) {
                if (!isCanceled) {
                    ((BaseActivity) context).seekUpdation();
                    ((BaseActivity) context).setBuffer(false);
                    if (mBuilder == null) {
                        createNotification();
                    } else {
                        updateNoti();
                    }
                    changePlayPause(true);
                } else {
                    isCanceled = false;
                    stopExoPlayer();
                }
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopExoPlayer();
                    stopForeground(true);
                    stopSelf();
                    ((BaseActivity) context).setBuffer(false);
                    ((BaseActivity) context).changePlayPause(false);
                }
            }, 0);
            super.onPlayerError(error);
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            super.onTracksChanged(trackGroups, trackSelections);
        }
    };

    private String getUserAgent() {
        return "radioentertainment";
    }

    private void changePlayPause(Boolean play) {
        ((BaseActivity) context).changePlayPause(play);
    }

    private void togglePlayPause() {
        if (Constants.exoPlayer_Radio.getPlayWhenReady()) {
            pause();
        } else {
            if (methods.isConnectingToInternet()) {
                play();
            } else {
                methods.showToast(getString(R.string.internet_not_connected));
            }
        }
    }

    private void pause() {
        Constants.exoPlayer_Radio.setPlayWhenReady(false);
        changePlayPause(false);
        updateNotiPlay(false);
    }

    private void play() {
        Constants.exoPlayer_Radio.setPlayWhenReady(true);
        changePlayPause(true);
        updateNotiPlay(true);
        ((BaseActivity) context).seekUpdation();
    }

    private void newPlay() {
        loadSong = new LoadSong();
        loadSong.execute();
    }

    private void next() {
        methods.getPosition(true);
        itemRadio = Constants.arrayList_radio.get(Constants.pos);
        newPlay();
    }

    private void previous() {
        methods.getPosition(false);
        itemRadio = Constants.arrayList_radio.get(Constants.pos);
        newPlay();
    }

    public void stop(Intent intent) {
        if (Constants.exoPlayer_Radio != null) {
            try {
                mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
                unregisterReceiver(onCallIncome);
                unregisterReceiver(onHeadPhoneDetect);
                mAudioManager.unregisterMediaButtonEventReceiver(componentName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            changePlayPause(false);
            stopExoPlayer();
            service = null;
            stopService(intent);
            stopForeground(true); // delete notification
        }
    }

    public void stopExoPlayer() {
        if (Constants.exoPlayer_Radio != null) {
            Constants.exoPlayer_Radio.stop();
            Constants.exoPlayer_Radio.addListener(eventListener);
        }
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, BaseActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent previousIntent = new Intent(this, PlayService.class);
        previousIntent.setAction(ACTION_PREVIOUS);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent playIntent = new Intent(this, PlayService.class);
        playIntent.setAction(ACTION_TOGGLE);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, PlayService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent closeIntent = new Intent(this, PlayService.class);
        closeIntent.setAction(ACTION_STOP);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, PendingIntent.FLAG_IMMUTABLE);


        String NOTIFICATION_CHANNEL_ID = "onlinradio_ch_1";
        mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon))
                .setTicker(itemRadio.getRadioName())
                .setContentTitle(itemRadio.getRadioName())
                .setContentText(itemRadio.getCityName())
                .setContentIntent(pi)
                .setPriority(Notification.PRIORITY_LOW)
                .setSmallIcon(R.drawable.ic_notification)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setOnlyAlertOnce(true);

        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);// The user-visible name of the channel.
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);

            MediaSessionCompat mMediaSession;
            mMediaSession = new MediaSessionCompat(context, getString(R.string.app_name));
            mMediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            mBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mMediaSession.getSessionToken())
                    .setShowCancelButton(true)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setCancelButtonIntent(
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    context, PlaybackStateCompat.ACTION_STOP)))
                    .addAction(new NotificationCompat.Action(
                            R.mipmap.ic_noti_previous, "Previous",
                            ppreviousIntent))
                    .addAction(new NotificationCompat.Action(
                            R.mipmap.ic_noti_pause, "Pause",
                            pplayIntent))
                    .addAction(new NotificationCompat.Action(
                            R.mipmap.ic_noti_next, "Next",
                            pnextIntent))
                    .addAction(new NotificationCompat.Action(
                            R.mipmap.ic_noti_close, "Close",
                            pcloseIntent));
        } else {
            bigViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
            smallViews = new RemoteViews(getPackageName(), R.layout.layout_noti_small);
            bigViews.setOnClickPendingIntent(R.id.imageView_noti_play, pplayIntent);

            bigViews.setOnClickPendingIntent(R.id.imageView_noti_next, pnextIntent);

            bigViews.setOnClickPendingIntent(R.id.imageView_noti_prev, ppreviousIntent);

            bigViews.setOnClickPendingIntent(R.id.imageView_noti_close, pcloseIntent);
            smallViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

            bigViews.setImageViewResource(R.id.imageView_noti_play, android.R.drawable.ic_media_pause);

            bigViews.setTextViewText(R.id.textView_noti_name, Constants.arrayList_radio.get(Constants.pos).getRadioName());
            smallViews.setTextViewText(R.id.status_bar_track_name, Constants.arrayList_radio.get(Constants.pos).getRadioName());

            bigViews.setImageViewResource(R.id.imageView_noti, R.mipmap.app_icon);
            smallViews.setImageViewResource(R.id.status_bar_album_art, R.mipmap.app_icon);

            mBuilder.setCustomContentView(smallViews)
                    .setCustomBigContentView(bigViews);
        }

        startForeground(NOTIFICATION_ID, mBuilder.build());
        updateNotiImage();
    }

    private void updateNotiImage() {
        try {
            new AsyncTask<String, String, String>() {

                @Override
                protected String doInBackground(String... strings) {
                    try {

                        getBitmapFromURL(Constants.arrayList_radio.get(Constants.pos).getRadioImageurl());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            mBuilder.setLargeIcon(bitmap);
                        } else {
                            bigViews.setImageViewBitmap(R.id.imageView_noti, bitmap);
                            smallViews.setImageViewBitmap(R.id.status_bar_album_art, bitmap);
                        }
                        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    super.onPostExecute(s);
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNoti() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setContentTitle(Constants.arrayList_radio.get(Constants.pos).getRadioName());
            mBuilder.setContentText(Constants.arrayList_radio.get(Constants.pos).getCityName());
        } else {
            bigViews.setTextViewText(R.id.textView_noti_name, Constants.arrayList_radio.get(Constants.pos).getRadioName());
            smallViews.setTextViewText(R.id.status_bar_track_name, Constants.arrayList_radio.get(Constants.pos).getRadioName());
        }
        updateNotiImage();
        updateNotiPlay(Constants.exoPlayer_Radio.getPlayWhenReady());
    }

    private void updateNotiPlay(Boolean isPlay) {
        if (mBuilder == null) {
            createNotification();
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mBuilder.mActions.remove(1);
                Intent playIntent = new Intent(this, PlayService.class);
                playIntent.setAction(ACTION_TOGGLE);
                PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_IMMUTABLE);
                if (isPlay) {
                    mBuilder.mActions.add(1, new NotificationCompat.Action(
                            R.mipmap.ic_noti_pause, "Pause",
                            ppreviousIntent));

                } else {
                    mBuilder.mActions.add(1, new NotificationCompat.Action(
                            R.mipmap.ic_noti_play, "Play",
                            ppreviousIntent));
                }
            } else {
                if (isPlay) {
                    bigViews.setImageViewResource(R.id.imageView_noti_play, android.R.drawable.ic_media_pause);
                } else {
                    bigViews.setImageViewResource(R.id.imageView_noti_play, android.R.drawable.ic_media_play);
                }
            }
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadViewed(final int pos) {
        if (methods.isConnectingToInternet()) {
            if(Constants.arrayList_radio.get(pos).getType().equals("ondemand")) {
                LoadOnDemandViewed loadOnDemandViewed = new LoadOnDemandViewed(new RadioViewListener() {
                    @Override
                    public void onEnd(String success) {
                    }
                }, methods.getAPIRequest(Constants.METHOD_SINGLE_ONDEMAND, 0, "", Constants.arrayList_radio.get(pos).getRadioId(), "", "", "", "", "", "", "", "", "", "", null));
                loadOnDemandViewed.execute();
            } else {
                LoadRadioViewed loadRadioViewed = new LoadRadioViewed(new RadioViewListener() {
                    @Override
                    public void onEnd(String success) {
                    }
                }, methods.getAPIRequest(Constants.METHOD_SINGLE_RADIO, 0, "", Constants.arrayList_radio.get(pos).getRadioId(), "", "", "", "", "", "", "", "", "", "", null));
                loadRadioViewed.execute();
            }
        }
    }

    public ItemRadio getPlayingRadioStation() {
        return itemRadio;
    }

    private void getBitmapFromURL(String src) {
        try {
            URL url = new URL(src.replace(" ", "%20"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }
    }

    BroadcastReceiver onCallIncome = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String a = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (isPlaying()) {
                if (a.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) || a.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    Intent intent_stop = new Intent(context, PlayService.class);
                    intent_stop.setAction(ACTION_TOGGLE);
                    startService(intent_stop);
                }
            }
        }
    };

    BroadcastReceiver onHeadPhoneDetect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.isPlaying) {
                togglePlayPause();
            }
        }
    };

    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange)
            {
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                    resumePlayer(); // Resume your media player here
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if(isPlaying()) {
                        togglePlayPause();
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public IcyHttpDataSourceFactory icy = new IcyHttpDataSourceFactory.Builder(getUserAgent())
            .setIcyHeadersListener(new IcyHttpDataSource.IcyHeadersListener() {
                @Override
                public void onIcyHeaders(IcyHttpDataSource.IcyHeaders icyHeaders) {

                }
            })
            .setIcyMetadataChangeListener(new IcyHttpDataSource.IcyMetadataListener() {
                @Override
                public void onIcyMetaData(final IcyHttpDataSource.IcyMetadata icyMetadata) {
                    try {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (icyMetadata.getStreamTitle().equals("")) {
                                    ((BaseActivity) context).changeSongName(getString(R.string.unknown_song));
                                } else {
                                    ((BaseActivity) context).changeSongName(icyMetadata.getStreamTitle());
                                }
                            }
                        }, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).build();

    @Override
    public void onDestroy() {
        try {
            Constants.exoPlayer_Radio.stop();
            Constants.exoPlayer_Radio.release();
            Constants.exoPlayer_Radio.removeListener(eventListener);

            try {
                mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
                unregisterReceiver(onCallIncome);
                unregisterReceiver(onHeadPhoneDetect);
                mAudioManager.unregisterMediaButtonEventReceiver(componentName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void onCompletion() {
        next();
    }
}